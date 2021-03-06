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

package org.anvilpowered.anvil.sponge.entity;

import org.anvilpowered.anvil.common.entity.EntityUtils;
import org.spongepowered.api.util.Identifiable;

import java.util.Optional;
import java.util.UUID;

public class SpongeEntityUtils implements EntityUtils {

    @Override
    public Optional<UUID> extractUUID(Object entity) {
        if (!(entity instanceof Identifiable)) {
            return Optional.empty();
        }
        return Optional.of(((Identifiable) entity).getUniqueId());
    }
}
