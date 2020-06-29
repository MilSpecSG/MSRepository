/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.base.data.registry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.data.key.Key;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.data.registry.RegistryScope;
import org.anvilpowered.anvil.api.data.registry.RegistryScoped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
@SuppressWarnings("unchecked")
public class BaseRegistry implements Registry {

    private final Map<Key<?>, Object> defaultMap, valueMap;
    private final Table<RegistryScope, Integer, Collection<Runnable>> listeners;
    private Registry coreRegistry;
    private String stringRepresentation;

    public BaseRegistry() {
        defaultMap = new HashMap<>();
        valueMap = new HashMap<>();
        listeners = HashBasedTable.create(2, 3);
    }

    @Override
    public <T> T getUnsafe(Key<T> key) {
        T t;
        try {
            t = (T) valueMap.get(key);
        } catch (ClassCastException e) {
            throw new NoSuchElementException("Value for key " + key + " is of the wrong type!");
        }
        if (t == null) {
            throw new NoSuchElementException("Could not find value for key " + key);
        }
        return t;
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        try {
            return Optional.ofNullable((T) valueMap.get(key));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T> T getDefault(Key<T> key) {
        T result = null;
        try {
            result = (T) defaultMap.get(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return result == null ? key.getFallbackValue() : result;
    }

    @Override
    public <T> void set(Key<T> key, T value) {
        valueMap.put(key, value);
        stringRepresentation = null;
    }

    @RegistryScoped
    protected <T> void setDefault(Key<T> key, T value) {
        defaultMap.put(key, value);
        stringRepresentation = null;
    }

    @Override
    public <T> T getExtraSafe(Key<T> key) {
        if (coreRegistry == null) {
            coreRegistry = Anvil.getServiceManager().provide(Registry.class);
        }
        if (this != coreRegistry
            && getOrDefault(Keys.USE_SHARED_ENVIRONMENT)) {
            if (key.equals(Keys.DATA_STORE_NAME)
                || key.equals(Keys.MONGODB_HOSTNAME)
                || key.equals(Keys.MONGODB_PORT)
                || key.equals(Keys.MONGODB_USE_SRV)) {
                return coreRegistry.getOrDefault(key);
            } else if (getOrDefault(Keys.USE_SHARED_CREDENTIALS)) {
                if (key.equals(Keys.MONGODB_USE_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_USERNAME)
                    || key.equals(Keys.MONGODB_PASSWORD)
                    || key.equals(Keys.MONGODB_AUTH_DB)
                    || key.equals(Keys.MONGODB_USE_AUTH)) {
                    return coreRegistry.getOrDefault(key);
                }
            }
        }
        return getOrDefault(key);
    }

    @Override
    public <T> void remove(Key<T> key) {
        valueMap.remove(key);
        stringRepresentation = null;
    }

    @Override
    public <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer) {
        valueMap.compute(key, (BiFunction<? super Key<?>, ? super Object, ?>) transformer);
        stringRepresentation = null;
    }

    @Override
    public <T> void transform(Key<T> key, Function<? super T, ? extends T> transformer) {
        transform(key, (k, v) -> transformer.apply((T) v));
        stringRepresentation = null;
    }

    @Override
    public <T> void addToCollection(Key<? extends Collection<T>> key, T value) {
        ((Collection<T>) valueMap.get(key)).add(value);
        stringRepresentation = null;
    }

    @Override
    public <T> void removeFromCollection(Key<? extends Collection<T>> key, T value) {
        ((Collection<T>) valueMap.get(key)).remove(value);
        stringRepresentation = null;
    }

    @Override
    public <K, T> void putInMap(Key<? extends Map<K, T>> key, K mapKey, T mapValue) {
        ((Map<K, T>) valueMap.get(key)).put(mapKey, mapValue);
        stringRepresentation = null;
    }

    @Override
    public <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey) {
        ((Map<K, T>) valueMap.get(key)).remove(mapKey);
        stringRepresentation = null;
    }

    @Override
    public ListenerRegistrationEnd whenLoaded(Runnable listener) {
        return new ListenerRegistrationEndImpl(listener);
    }

    private class ListenerRegistrationEndImpl implements ListenerRegistrationEnd {

        private final Runnable listener;
        private int order;
        private RegistryScope registryScope;

        public ListenerRegistrationEndImpl(Runnable listener) {
            this.listener = listener;
            order = 0;
            registryScope = RegistryScope.DEFAULT;
        }

        @Override
        public ListenerRegistrationEnd order(int order) {
            this.order = 0;
            return this;
        }

        @Override
        public ListenerRegistrationEnd scope(RegistryScope registryScope) {
            this.registryScope = registryScope;
            return this;
        }

        @Override
        public void register() {
            listeners.row(registryScope)
                .computeIfAbsent(order, o -> new ArrayList<>())
                .add(listener);
        }
    }

    @Override
    public void load(RegistryScope registryScope) {
        final int ordinal = registryScope.ordinal();
        if (ordinal <= RegistryScope.DEFAULT.ordinal()) {
            loadDefaultScope();
        }
        loadOrdinal(ordinal);
    }

    protected final void loadOrdinal(int ordinal) {
        final RegistryScope[] values = RegistryScope.values();
        final int valueLength = values.length;
        for (int i = ordinal; i < valueLength; i++) {
            listeners.row(values[i]).forEach((n, l) -> l.forEach(Runnable::run));
        }
    }

    /**
     * Override this method to load values into this registry on normal reloads
     */
    @RegistryScoped
    protected void loadDefaultScope() {
    }

    @Override
    public String toString() {
        if (stringRepresentation != null) {
            return stringRepresentation;
        }
        Set<Key<?>> keys = new HashSet<>();
        int[] width = {0, 32, 32};
        Consumer<? super Key<?>> addToKeys = key -> {
            final int keyLength = key.toString().length();
            if (keyLength > width[0]) {
                width[0] = keyLength;
            }
            keys.add(key);
        };
        valueMap.keySet().forEach(addToKeys);
        defaultMap.keySet().forEach(addToKeys);
        width[0] += 5;
        return stringRepresentation = String.format("%-" + width[0] + "s", "Key")
            + String.format("%-" + width[1] + "s", "Value")
            + String.format("%-" + width[2] + "s", "Default")
            + '\n'
            + keys.stream().map(key ->
            String.format("%-" + width[0] + "s", key.toString())
                + String.format("%-" + width[1] + "s", valueMap.get(key))
                + String.format("%-" + width[2] + "s", defaultMap.get(key))
        ).collect(Collectors.joining("\n"));
    }
}
