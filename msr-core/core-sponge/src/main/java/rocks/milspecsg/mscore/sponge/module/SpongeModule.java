/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.mscore.sponge.module;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.mscore.common.module.CommonModule;
import rocks.milspecsg.mscore.sponge.data.config.MSCoreSpongeConfigurationService;
import rocks.milspecsg.msrepository.common.data.config.CommonConfigurationService;

public class SpongeModule extends CommonModule<Text, CommandSource> {

    @Override
    protected void configure() {
        super.configure();

        bind(CommonConfigurationService.class).to(MSCoreSpongeConfigurationService.class);
    }
}