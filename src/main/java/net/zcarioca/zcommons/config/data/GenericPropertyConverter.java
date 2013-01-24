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

import java.lang.reflect.Constructor;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * A generic property converter.
 * 
 * 
 * @author zcarioca
 */
class GenericPropertyConverter implements BeanPropertyConverter<Object>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getSupportedClass()
   {
      return Object.class;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      try
      {
         Constructor<?> constructor = beanPropertyInfo.getPropertyType().getConstructor(String.class);
         return constructor.newInstance(value);
      }
      catch (Exception exc)
      {
         throw new ConfigurationException(String.format("Could instantiate instance of class %s: %s", beanPropertyInfo.getPropertyType(), exc.getMessage()), exc);
      }
   }

}
