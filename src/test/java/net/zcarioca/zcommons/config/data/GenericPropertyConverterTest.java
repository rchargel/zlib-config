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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GenericPropertyConverter}.
 *
 * @author zcarioca
 */
public class GenericPropertyConverterTest extends BaseConverterTestCase
{

   private GenericPropertyConverter converter;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      converter = new GenericPropertyConverter();
   }

   @Test
   public void testConvertProperty1() throws ConfigurationException
   {
      setPropertyType(File.class);

      assertEquals(new File("/tmp/test.txt"), converter.convertPropertyValue("/tmp/test.txt", beanPropertyInfo));
   }

   @Test
   public void testConvertProperty2() throws ConfigurationException
   {
      setPropertyType(SimpleDateFormat.class);

      assertEquals(new SimpleDateFormat("yyyy"), converter.convertPropertyValue("yyyy", beanPropertyInfo));
   }

   @Test
   public void testConvertProperty3() throws ConfigurationException
   {
      setPropertyType(BigInteger.class);

      assertEquals(new BigInteger("1000"), converter.convertPropertyValue("1000", beanPropertyInfo));
   }

   @Test(expected = ConfigurationException.class)
   public void testConvertPropertyBadValue() throws ConfigurationException
   {
      setPropertyType(BigInteger.class);
      converter.convertPropertyValue("", beanPropertyInfo);
   }

   @Test(expected = ConfigurationException.class)
   public void testConvertPropertyInvalidType() throws ConfigurationException
   {
      setPropertyType(Calendar.class);
      converter.convertPropertyValue("2010", beanPropertyInfo);
   }

}
