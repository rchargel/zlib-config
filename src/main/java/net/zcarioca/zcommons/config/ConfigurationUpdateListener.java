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
 * A listener that receives messages when a configuration file has been
 * updated.
 * 
 * @author zcarioca
 */
public interface ConfigurationUpdateListener extends EventListener
{
   /**
    * Fired when a bean update has started.  This usually happens when the underlying file
    * or data model has been changed.
    * 
    * @param bean The bean that is being updated.
    */
   public void startingBeanUpdate(Object bean);
   
   /**
    * Fired when a bean has been updated by the configuration system. This usually happens when the underlying file
    * or data model has been changed.
    * 
    * @param bean The bean that has been updated.
    */
   public void completedBeanUpdate(Object bean);
}
