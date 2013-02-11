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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A property converter to convert array properties.
 *
 * @author zcarioca
 */
class ArrayPropertyConverter<T> implements BeanPropertyConverter<T>
{
   private final static Pattern pattern = Pattern.compile("(?:^|,)(\\\"(?:[^\\\"]+|\\\"\\\")*\\\"|[^,]*)");

   private final Class<T> arrayType;

   private ArrayPropertyConverter(Class<T> arrayType)
   {

      this.arrayType = arrayType;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public static <T> ArrayPropertyConverter<T> createNewArrayPropertyConverter(Class<T> arrayType)
   {
      return new ArrayPropertyConverter(arrayType);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getSupportedClass()
   {
      return Array.newInstance(arrayType.getComponentType(), 0).getClass();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings({"rawtypes", "unchecked"})
   public T convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      BeanPropertyConverter converter = BeanPropertyConverterRegistry.getRegistry().getPropertyConverter(arrayType.getComponentType());
      String[] splitValues = split(value);

      T data = (T) Array.newInstance(arrayType.getComponentType(), splitValues.length);

      for (int i = 0; i < splitValues.length; i++) {
         Object itemVal = converter.convertPropertyValue(splitValues[i], beanPropertyInfo);
         if (itemVal == null && arrayType.getComponentType().isPrimitive()) {
            itemVal = BeanPropertySetterFactory.getDefaultValue(arrayType.getComponentType());
         }
         Array.set(data, i, itemVal);
      }
      return data;
   }

   String[] split(String originalValue)
   {
      List<String> list = new ArrayList<String>();

      Matcher matcher = pattern.matcher(originalValue);

      StringBuilder quotedGroup = new StringBuilder();
      boolean inQuotedLoop = false;

      while (matcher.find()) 
      {
         String group = matcher.group();
         if (!inQuotedLoop) 
         {
            if (group.startsWith(",")) 
            {
               group = group.substring(1);
            }
            group = group.trim();
         }

         if (group.startsWith("\"")) 
         {
            group = group.substring(1);

            if (group.endsWith("\"")) 
            {
               group = group.substring(0, group.length() - 1);
            } 
            else 
            {
               quotedGroup.append(group);
               inQuotedLoop = true;
               continue;
            }
         }
         if (inQuotedLoop) 
         {
            if (group.endsWith("\"")) 
            {
               quotedGroup.append(group.substring(0, group.length() - 1));
               inQuotedLoop = false;

               group = quotedGroup.toString();
            } 
            else 
            {
               quotedGroup.append(group);
               continue;
            }
         }

         list.add(group);
      }

      return list.toArray(new String[list.size()]);
   }

}
