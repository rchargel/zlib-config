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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry of value converters. Custom converters should be supplied to this registry.
 *
 * @author zcarioca
 */
public class BeanPropertyConverterRegistry
{
   private static BeanPropertyConverterRegistry beanPropertyConverterRegistry;

   private final Map<Class<?>, BeanPropertyConverter<?>> registry;

   /**
    * Gets access to the {@link BeanPropertyConverterRegistry}.
    *
    * @return Returns access to the {@link BeanPropertyConverterRegistry}.
    */
   public static BeanPropertyConverterRegistry getRegistry()
   {
      if (beanPropertyConverterRegistry == null) 
      {
         beanPropertyConverterRegistry = new BeanPropertyConverterRegistry();
      }
      return beanPropertyConverterRegistry;
   }

   /**
    * Registers a new {@link BeanPropertyConverter}.
    *
    * @param converter The {@link BeanPropertyConverter} to register.
    * @throws IllegalArgumentException if the converter is null.
    * @throws ConfigurationException   if the converter does not return a supported class.
    */
   public void register(BeanPropertyConverter<?> converter) throws ConfigurationException
   {
      if (converter == null) 
      {
         throw new IllegalArgumentException("The property converter cannot be null");
      }

      Class<?> supportedClass = converter.getSupportedClass();
      if (supportedClass != null) 
      {
         this.registry.put(supportedClass, converter);
      } 
      else 
      {
         throw new ConfigurationException("Cannot use a property converter that does not return a supported class: " + converter.getClass());
      }
   }

   /**
    * Gets a {@link BeanPropertyConverter} for the supplied type.
    *
    * @param type The type to convert.
    * @return Returns a {@link BeanPropertyConverter} for the supplied type.
    * @throws IllegalArgumentException if the type is null.
    */
   public BeanPropertyConverter<?> getPropertyConverter(Class<?> type)
   {
      if (type == null) 
      {
         throw new IllegalArgumentException("The supplied type cannot be null");
      }

      if (type.isArray()) 
      {
         return ArrayPropertyConverter.createNewArrayPropertyConverter(type);
      }

      if (this.registry.containsKey(normalizedType(type))) 
      {
         return this.registry.get(normalizedType(type));
      }

      return new GenericPropertyConverter();
   }

   private Class<?> normalizedType(Class<?> type)
   {
      if (type.isPrimitive()) 
      {
         return convertFromPrimitiveType(type);
      }
      return type;
   }

   private Class<?> convertFromPrimitiveType(Class<?> type)
   {
      if (boolean.class == type)
         return Boolean.class;
      if (char.class == type)
         return Character.class;
      if (byte.class == type)
         return Byte.class;
      if (short.class == type)
         return Short.class;
      if (int.class == type)
         return Integer.class;
      if (long.class == type)
         return Long.class;
      if (float.class == type)
         return Float.class;

      //this is all that's left
      return Double.class;
   }

   BeanPropertyConverterRegistry()
   {
      registry = new HashMap<Class<?>, BeanPropertyConverter<?>>();
      registry.put(String.class, new StringPropertyConverter());
      registry.put(Boolean.class, new BooleanPropertyConverter());
      registry.put(Character.class, new CharacterPropertyConverter());
      registry.put(Byte.class, NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class));
      registry.put(Short.class, NumberPropertyConverter.createNewNumberPropertyConverter(Short.class));
      registry.put(Integer.class, NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class));
      registry.put(Long.class, NumberPropertyConverter.createNewNumberPropertyConverter(Long.class));
      registry.put(Float.class, NumberPropertyConverter.createNewNumberPropertyConverter(Float.class));
      registry.put(Double.class, NumberPropertyConverter.createNewNumberPropertyConverter(Double.class));
      registry.put(Date.class, new DatePropertyConverter());
      registry.put(Calendar.class, new CalendarPropertyConverter());
   }
}
