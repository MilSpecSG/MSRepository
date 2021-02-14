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

package org.anvilpowered.anvil.common.anvilnet.communicator.node

import org.anvilpowered.anvil.api.Platform
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.Order
import java.util.EnumSet
import kotlin.reflect.KClass

class Node(val ref: NodeRef) {

  lateinit var platform: Platform

  val eventListeners: Map<KClass<out Event>, EnumSet<Order>> = mutableMapOf()

}
