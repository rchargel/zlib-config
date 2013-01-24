/*
 * Project: zlib-config
 * 
 * Copyright (C) 2013 zcarioca.net
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
package net.zcarioca.zcommons.config.data;

import java.util.Properties;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * This class is responsible for setting a property on a bean.
 * 
 * @author zcarioca
 */
public interface BeanPropertySetter
{
   /**
    * Gets the {@link BeanPropertyInfo} as information about the bean and the
    * property this property setter is responsible for.
    * 
    * @return Returns the bean property info.
    */
   public BeanPropertyInfo getBeanPropertyInfo();
   
   /**
    * Unlike the {@link BeanPropertyInfo#getPropertyName()}, this is not the name
    * of the property in the class, but rather the key for finding the property
    * in the {@link Properties} object.
    * 
    * @return Returns the property key.
    */
   public String getPropertyKey();

   /**
    * Uses the supplied {@link Properties} to set the correct property on the
    * bean.
    * 
    * @param properties The properties.
    */
   public void setProperty(Properties properties) throws ConfigurationException;
}
