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

import org.apache.commons.lang.StringUtils;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * Converts a String into a Boolean value.
 * 
 * @author zcarioca
 */
class BooleanPropertyConverter implements PropertyConverter<Boolean>
{
   
   /**
    * {@inheritDoc}
    */
   @Override
   public Class<Boolean> getSupportedClass()
   {
      return Boolean.class;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public Boolean convertPropertyValue(String value, PropertyInfo propertyInfo) throws ConfigurationException
   {
      return StringUtils.isBlank(value) ? null : Boolean.parseBoolean(value);
   }

}
