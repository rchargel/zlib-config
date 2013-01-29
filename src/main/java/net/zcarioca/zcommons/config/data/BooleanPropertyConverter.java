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
import org.apache.commons.lang.StringUtils;

/**
 * Converts a String into a Boolean value.
 *
 * @author zcarioca
 */
class BooleanPropertyConverter implements BeanPropertyConverter<Boolean>
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
   public Boolean convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      return StringUtils.isBlank(value) ? null : parseBoolean(value.trim());
   }

   private boolean parseBoolean(String value) throws ConfigurationException
   {
      boolean bool = Boolean.parseBoolean(value) ||
            value.equalsIgnoreCase("yes") ||
            value.equalsIgnoreCase("y") ||
            value.equalsIgnoreCase("t") ||
            value.equals("1");

      if (!bool) {
         // test for validity
         if (isNotValid(value)) {
            throw new ConfigurationException(String.format("Could not parse the value %s as a boolean", value));
         }
      }
      return bool;
   }

   private boolean isNotValid(String value)
   {
      return !(value.equalsIgnoreCase("false") ||
            value.equalsIgnoreCase("f") ||
            value.equalsIgnoreCase("no") ||
            value.equalsIgnoreCase("n") ||
            value.equals("0"));
   }
}
