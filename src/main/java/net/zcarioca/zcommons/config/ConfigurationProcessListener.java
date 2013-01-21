/*
 * Project: zlib-config
 * 
 * Copyright (C) 2010 zcarioca.net
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.zcarioca.zcommons.config;

import java.util.EventListener;

/**
 * An event listener used to inform interested systems when an object has either
 * been configured, or is about to be configured.
 * 
 * 
 * @author zcarioca
 */
public interface ConfigurationProcessListener extends EventListener
{
   /**
    * Called when configuration is about to take place.
    * 
    * @param bean The bean to be configured.
    */
   public void startingConfiguration(Object bean);
   
   /**
    * Called when the configuration is complete.
    * 
    * @param bean The bean which has been configured.
    */
   public void completedConfiguration(Object bean);
}
