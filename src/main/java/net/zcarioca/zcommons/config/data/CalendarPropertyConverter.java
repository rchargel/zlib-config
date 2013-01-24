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

import java.util.Calendar;
import java.util.Date;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

/**
 * {@link BeanPropertyConverter} for {@link Calendar} values.
 * 
 * @author zcarioca
 */
class CalendarPropertyConverter implements BeanPropertyConverter<Calendar>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<Calendar> getSupportedClass()
   {
      return Calendar.class;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Calendar convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      Calendar calendar = null;
      
      DatePropertyConverter converter = new DatePropertyConverter();
      Date date = converter.convertPropertyValue(value, beanPropertyInfo);
      
      if (date != null)
      {
         calendar = Calendar.getInstance();
         calendar.setTime(date);
      }
      return calendar;
   }

}
