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

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link DatePropertyConverter}.
 *
 * @author zcarioca
 */
public class DatePropertyConverterTest extends BaseConverterTestCase
{
   private DatePropertyConverter converter;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      converter = new DatePropertyConverter();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   protected void setupBeanPropertyInfo()
   {
      super.setupBeanPropertyInfo();
      when(beanPropertyInfo.getPropertyName()).thenReturn("myDateProperty");
      when(beanPropertyInfo.getBeanType()).thenReturn((Class) Object.class);
   }

   @Test(expected = ConfigurationException.class)
   public void testNoDateFormat() throws ConfigurationException
   {
      converter.convertPropertyValue("2010", beanPropertyInfo);
   }

   @Test(expected = ConfigurationException.class)
   public void testNullDateFormat() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat(null));

      converter.convertPropertyValue("2010", beanPropertyInfo);
   }

   @Test(expected = ConfigurationException.class)
   public void testInvalidDateFormat() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("this isn't a real date format"));

      converter.convertPropertyValue("2010", beanPropertyInfo);
   }

   @Test(expected = ConfigurationException.class)
   public void testInvalidDate() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

      converter.convertPropertyValue("This is not a parsable date", beanPropertyInfo);
   }

   @Test
   public void testConvertPropertyValue() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

      assertEquals(createDate("2010", "yyyy"), converter.convertPropertyValue("2010", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testConvertPropertyValueAnnotationAtField() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockConfigurableDateFormat("yyyy"));

      assertEquals(createDate("2010", "yyyy"), converter.convertPropertyValue("2010", beanPropertyInfo));
   }

   @Test
   public void testConvertPropertyValueAnnotationAtFieldOverridesBeanAnnotation() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockConfigurableDateFormat("yyyyMMdd"));

      assertEquals(createDate("20101223", "yyyyMMdd"), converter.convertPropertyValue("20101223", beanPropertyInfo));
   }

   private Date createDate(String date, String format)
   {
      try {
         return new SimpleDateFormat(format).parse(date);
      } catch (Exception exc) {
         return null;
      }
   }

}
