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

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * Converts the String value into the desired class type for your converter;
 * 
 * @author zcarioca
 */
public interface BeanPropertyConverter<T>
{
   /**
    * The class supported by the converter.
    * 
    * @return Returns the class supported by the converter.
    */
   public Class<?> getSupportedClass();

   /**
    * Converts the value to type supported by this converter.
    * 
    * @param value The value to convert.
    * @param beanPropertyInfo The information about the property.
    * 
    * @return Returns the value as the correct type.
    */
   public T convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException;
}
