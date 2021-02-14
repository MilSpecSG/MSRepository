/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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

package org.anvilpowered.anvil.api.event;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * The result of a single call to {@link EventManager#post(Event, Class)}
 *
 * @param <E> The event type
 * @see EventManager#post(Event, Class)
 * @see Event
 */
public interface EventResult<E extends Event> {

  Class<E> getEventType();

  List<? extends Batch<E>> getBatches();

  /**
   * A batch of listeners that run for a given {@link Order}
   *
   * @param <E> The event type
   */
  interface Batch<E extends Event> {
    EventResult<E> getParentResult();

    Order getOrder();

    List<? extends Tree<E>> getTrees();
  }

  /**
   * Represents the execution of a group of listeners.
   * <p>
   * The root tree's event type will be the root result's type. The children of this tree represent event listeners
   * listening to supertypes of the posted event.
   *
   * @param <E> The event type for this tree's {@link #getInvocations()} (e.g. whats written in the listener method signature).
   *            This may be a supertype of the posted event type
   */
  interface Tree<E extends Event> {

    /**
     * Returns the listener's event type.
     * Note: this may be a supertype of the posted event's type
     *
     * @return The listener's event type.
     */
    Class<E> getEventType();

    Batch<? extends E> getParentBatch();

    Optional<? extends Tree<? extends E>> getParent();

    List<? extends Tree<? super E>> getChildren();

    List<? extends Invocation<E>> getInvocations();
  }

  /**
   * The result of a single event listener
   *
   * @param <E> The listener's event type (e.g. whats written in the method signature).
   *            This may be a supertype of the posted event type
   */
  interface Invocation<E extends Event> {
    Tree<E> getParentTree();

    Duration getDuration();

    Optional<? extends Exception> getException();
  }
}
