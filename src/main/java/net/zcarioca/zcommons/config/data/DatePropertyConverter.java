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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import net.zcarioca.zcommons.config.ConfigurableDateFormat;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * Converts a {@link Date} property.
 * 
 * 
 * @author zcarioca
 */
class DatePropertyConverter implements PropertyConverter<Date>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getSupportedClass()
   {
      return Date.class;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      SimpleDateFormat simpleDateFormat = getSimpleDateFormat(beanPropertyInfo);
      if (simpleDateFormat == null)
      {
         throw new ConfigurationException(String.format("To convert a value to a date the field '%s' or class '%s' must be annotated with an @ConfigurableDateFormat", 
               beanPropertyInfo.getPropertyName(), beanPropertyInfo.getBeanType().getSimpleName()));
      }
      
      try
      {
         return StringUtils.isBlank(value) ? null : simpleDateFormat.parse(value);
      }
      catch (ParseException exc)
      {
         throw new ConfigurationException(String.format("Could not format property %s of %s, value %s did not fit provided format %s",
               beanPropertyInfo.getPropertyName(), beanPropertyInfo.getBeanType().getSimpleName(),
               value, simpleDateFormat.toPattern()));
      }
   }
   
   protected SimpleDateFormat getSimpleDateFormat(BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      ConfigurableDateFormat format = null;
      try
      {
         SimpleDateFormat simpleDateFormat = null;
         format = getConfigurableDateFormat(beanPropertyInfo.getPropertyAnnotations());
         if (format == null)
         {
            format = getConfigurableDateFormat(beanPropertyInfo.getBeanAnnotations());
         }

         if (format != null)
         {
            simpleDateFormat = new SimpleDateFormat(format.value());
         }
         return simpleDateFormat;
      }
      catch (NullPointerException exc)
      {
         throw new ConfigurationException(String.format("@ConfigurableDateFormat requires a format to be specified for property %s.%s", beanPropertyInfo.getBeanType().getSimpleName(), beanPropertyInfo.getPropertyName()));
      }
      catch (IllegalArgumentException exc)
      {
         throw new ConfigurationException(String.format("%s is not a valid format for the @ConfigurableDateFormat for property %s.%s", format.value(), beanPropertyInfo.getBeanType().getSimpleName(), beanPropertyInfo.getPropertyName()));
      }
   }
   
   protected ConfigurableDateFormat getConfigurableDateFormat(Collection<Annotation> annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation instanceof ConfigurableDateFormat)
         {
            return (ConfigurableDateFormat)annotation;
         }
      }
      return null;
   }

}
