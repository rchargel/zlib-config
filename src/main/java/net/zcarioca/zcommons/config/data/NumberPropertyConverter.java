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

import org.apache.commons.lang.StringUtils;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * A numeric property converter.
 * 
 * @author zcarioca
 */
class NumberPropertyConverter<T extends Number> implements PropertyConverter<T>
{
   private Class<T> supportedClass;
   
   public NumberPropertyConverter(Class<T> supportedClass)
   {
      this.supportedClass = supportedClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<T> getSupportedClass()
   {
      return supportedClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      if (StringUtils.isBlank(value))
         return null;
      
      try
      {
         Constructor<T> constructor = getSupportedClass().getConstructor(String.class);
         return constructor.newInstance(value);
      }
      catch (Exception exc)
      {
         throw new ConfigurationException(String.format("Could not parse %s as a %s", value, getSupportedClass()), exc);
      }
   }

}
