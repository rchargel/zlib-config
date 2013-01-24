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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link BooleanPropertyConverter}.
 * 
 * 
 * @author zcarioca
 */
public class BooleanPropertyConverterTest extends BaseConverterTestCase
{
   private BooleanPropertyConverter converter;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      converter = new BooleanPropertyConverter();
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.data.BooleanPropertyConverter#convertPropertyValue(java.lang.String, net.zcarioca.zcommons.config.data.BeanPropertyInfo)}.
    */
   @Test
   public void testConvertPropertyValue() throws ConfigurationException
   {
      assertTrue(converter.convertPropertyValue("true", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("True", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("TRUE", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("T", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("t", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("yes", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("YES", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("Y", beanPropertyInfo));
      assertTrue(converter.convertPropertyValue("1", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("false", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("False", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("FALSE", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("F", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("f", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("no", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("NO", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("n", beanPropertyInfo));
      assertFalse(converter.convertPropertyValue("0", beanPropertyInfo));
   }
   
   @Test
   public void testConvertPropertyValueBlankOrNull() throws ConfigurationException
   {
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testConvertPropertyValueBadValue() throws ConfigurationException
   {
      converter.convertPropertyValue("not a boolean", beanPropertyInfo);
   }

}
