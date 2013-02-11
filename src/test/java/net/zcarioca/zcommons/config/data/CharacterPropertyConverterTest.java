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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link CharacterPropertyConverter}.
 *
 * @author zcarioca
 */
public class CharacterPropertyConverterTest extends BaseConverterTestCase
{
   private CharacterPropertyConverter converter;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      converter = new CharacterPropertyConverter();
   }

   @Test
   public void testConvertPropertyValue() throws ConfigurationException
   {
      assertEquals(' ', converter.convertPropertyValue(" ", beanPropertyInfo).charValue());
      assertEquals('a', converter.convertPropertyValue("a", beanPropertyInfo).charValue());
      assertEquals('B', converter.convertPropertyValue("B", beanPropertyInfo).charValue());
      assertEquals('$', converter.convertPropertyValue("$", beanPropertyInfo).charValue());
   }

   @Test
   public void testConvertPropertyValueEmptyOrNull() throws ConfigurationException
   {
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testConvertPropertyValueFullString() throws ConfigurationException
   {
      // will still convert strings, but just uses the first character
      assertEquals('T', converter.convertPropertyValue("This is longer string", beanPropertyInfo).charValue());
      assertEquals(' ', converter.convertPropertyValue("  This is an untrimmed string  ", beanPropertyInfo).charValue());
   }

}
