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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import net.zcarioca.zcommons.config.ConfigurableNumberEncoding;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.apache.commons.lang.StringUtils;

/**
 * A numeric property converter.
 * 
 * @author zcarioca
 */
class NumberPropertyConverter<T extends Number> implements BeanPropertyConverter<T>
{
   private final Class<T> supportedClass;

   private NumberPropertyConverter(Class<T> type)
   {
      supportedClass = type;
   }

   public static <T extends Number> NumberPropertyConverter<T> createNewNumberPropertyConverter(Class<T> type)
   {
      return new NumberPropertyConverter<T>(type);
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
   @SuppressWarnings("unchecked")
   public T convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      if (StringUtils.isBlank(value))
         return null;

      try
      {
         Method parse = getParseMethod();
         if (parse.getParameterTypes().length == 1)
         {
            return (T) parse.invoke(null, value.trim());
         }
         return (T) parse.invoke(null, value.trim(), getRadix(beanPropertyInfo));
      }
      catch (Exception exc)
      {
         throw new ConfigurationException(String.format("Could not parse %s as a %s", value, getSupportedClass()), exc);
      }
   }

   private Method getParseMethod() throws NoSuchMethodException
   {
      String methodName = "parse" + getSupportedClass().getSimpleName();
      if (getSupportedClass() == Integer.class)
      {
         methodName = "parseInt";
      }
      if (getSupportedClass() == Float.class || getSupportedClass() == Double.class)
      {
         return getSupportedClass().getMethod(methodName, String.class);
      }

      return getSupportedClass().getMethod(methodName, String.class, int.class);
   }

   private int getRadix(BeanPropertyInfo beanPropertyInfo)
   {
      ConfigurableNumberEncoding configurableNumberEncoding = getConfigurableNumberFormat(beanPropertyInfo);
      return configurableNumberEncoding != null ? configurableNumberEncoding.value().radix() : 10;
   }

   private ConfigurableNumberEncoding getConfigurableNumberFormat(BeanPropertyInfo beanPropertyInfo)
   {
      ConfigurableNumberEncoding configurableNumberEncoding = getConfigurableNumberFormat(beanPropertyInfo.getPropertyAnnotations());

      return configurableNumberEncoding != null ? configurableNumberEncoding : getConfigurableNumberFormat(beanPropertyInfo.getBeanAnnotations());
   }

   private ConfigurableNumberEncoding getConfigurableNumberFormat(Collection<Annotation> annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation instanceof ConfigurableNumberEncoding)
         {
            return (ConfigurableNumberEncoding) annotation;
         }
      }
      return null;
   }

}
