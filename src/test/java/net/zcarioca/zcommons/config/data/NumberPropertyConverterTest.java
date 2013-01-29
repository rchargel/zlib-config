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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.ConfigurableNumberEncoding.NumberFormat;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.junit.Test;

/**
 * Tests the {@link NumberPropertyConverter}.
 * 
 * @author zcarioca
 */
public class NumberPropertyConverterTest extends BaseConverterTestCase
{

   @Test
   public void testByte() throws ConfigurationException
   {
      NumberPropertyConverter<Byte> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).byteValue());
      assertEquals(-128, converter.convertPropertyValue("-128", beanPropertyInfo).byteValue());
      assertEquals(127, converter.convertPropertyValue("127", beanPropertyInfo).byteValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testByteOutOfRange() throws ConfigurationException
   {
      NumberPropertyConverter<Byte> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class);
      converter.convertPropertyValue("255", beanPropertyInfo);
   }

   @Test
   public void testShort() throws ConfigurationException
   {
      NumberPropertyConverter<Short> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Short.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).shortValue());
      assertEquals(567, converter.convertPropertyValue("567", beanPropertyInfo).shortValue());
      assertEquals(-2567, converter.convertPropertyValue("-2567", beanPropertyInfo).shortValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testShortOutOfRange() throws ConfigurationException
   {
      NumberPropertyConverter<Short> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Short.class);
      converter.convertPropertyValue("99999999999", beanPropertyInfo);
   }

   @Test
   public void testInteger() throws ConfigurationException
   {
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(567000, converter.convertPropertyValue("567000", beanPropertyInfo).intValue());
      assertEquals(-2003567, converter.convertPropertyValue("-2003567", beanPropertyInfo).intValue());
      assertEquals(2000000000, converter.convertPropertyValue("2000000000", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithNoNumberFormat() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(null));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(567000, converter.convertPropertyValue("567000", beanPropertyInfo).intValue());
      assertEquals(-2003567, converter.convertPropertyValue("-2003567", beanPropertyInfo).intValue());
      assertEquals(2000000000, converter.convertPropertyValue("2000000000", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithDecimal() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.DECIMAL));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(567000, converter.convertPropertyValue("567000", beanPropertyInfo).intValue());
      assertEquals(-2003567, converter.convertPropertyValue("-2003567", beanPropertyInfo).intValue());
      assertEquals(2000000000, converter.convertPropertyValue("2000000000", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithBinary() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.BINARY));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(1, converter.convertPropertyValue("1", beanPropertyInfo).intValue());
      assertEquals(2, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(3, converter.convertPropertyValue("11", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithOctal() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.OCTAL));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(1, converter.convertPropertyValue("1", beanPropertyInfo).intValue());
      assertEquals(7, converter.convertPropertyValue("7", beanPropertyInfo).intValue());
      assertEquals(8, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(9, converter.convertPropertyValue("11", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithHex() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.HEXIDECIMAL));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(10, converter.convertPropertyValue("a", beanPropertyInfo).intValue());
      assertEquals(15, converter.convertPropertyValue("f", beanPropertyInfo).intValue());
      assertEquals(16, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(255, converter.convertPropertyValue("FF", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithHexOnClass() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberFormat.HEXIDECIMAL));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(10, converter.convertPropertyValue("a", beanPropertyInfo).intValue());
      assertEquals(15, converter.convertPropertyValue("f", beanPropertyInfo).intValue());
      assertEquals(16, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(255, converter.convertPropertyValue("FF", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithHexOverridingClassAnnotation() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberFormat.BINARY));
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.HEXIDECIMAL));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(10, converter.convertPropertyValue("a", beanPropertyInfo).intValue());
      assertEquals(15, converter.convertPropertyValue("f", beanPropertyInfo).intValue());
      assertEquals(16, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(255, converter.convertPropertyValue("FF", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testIntegerWithNoFormatOverridingClassAnnotation() throws ConfigurationException
   {
      setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberFormat.BINARY));
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(null));
      
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).intValue());
      assertEquals(10, converter.convertPropertyValue("10", beanPropertyInfo).intValue());
      assertEquals(15, converter.convertPropertyValue("15", beanPropertyInfo).intValue());
      assertEquals(16, converter.convertPropertyValue("16", beanPropertyInfo).intValue());
      assertEquals(255, converter.convertPropertyValue("255", beanPropertyInfo).intValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testIntegerInvalid() throws ConfigurationException
   {
      NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
      converter.convertPropertyValue("123.123412", beanPropertyInfo);
   }

   @Test
   public void testLong() throws ConfigurationException
   {
      NumberPropertyConverter<Long> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Long.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo).longValue());
      assertEquals(567000, converter.convertPropertyValue("567000", beanPropertyInfo).longValue());
      assertEquals(-2003567, converter.convertPropertyValue("-2003567", beanPropertyInfo).longValue());
      assertEquals(2000000000000000000l, converter.convertPropertyValue("2000000000000000000", beanPropertyInfo).longValue());
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testLongInvalid() throws ConfigurationException
   {
      NumberPropertyConverter<Long> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Long.class);
      converter.convertPropertyValue("FFFF", beanPropertyInfo);
   }

   @Test
   public void testFloat() throws ConfigurationException
   {
      NumberPropertyConverter<Float> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Float.class);
      assertEquals(0f, converter.convertPropertyValue("0", beanPropertyInfo), 0);
      assertEquals(123.456f, converter.convertPropertyValue("123.456", beanPropertyInfo), 0);
      assertEquals(-256.99f, converter.convertPropertyValue("-256.99", beanPropertyInfo), 0);
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testFloatInvalid() throws ConfigurationException
   {
      NumberPropertyConverter<Float> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Float.class);
      converter.convertPropertyValue("123.456.789", beanPropertyInfo);
   }

   @Test
   public void testDouble() throws ConfigurationException
   {
      NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo), 0);
      assertEquals(55, converter.convertPropertyValue("55", beanPropertyInfo), 0);
      assertEquals(123.456, converter.convertPropertyValue("123.456", beanPropertyInfo), 0);
      assertEquals(-256.99, converter.convertPropertyValue("-256.99", beanPropertyInfo), 0);
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }

   @Test
   public void testDoubleNumberFormatHasNoAffect() throws ConfigurationException
   {
      setPropertyAnnotations(mockAnnotation(ConfigurableAttribute.class), mockNumberFormatAnnotation(NumberFormat.BINARY));
      
      NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
      assertEquals(0, converter.convertPropertyValue("0", beanPropertyInfo), 0);
      assertEquals(55, converter.convertPropertyValue("55", beanPropertyInfo), 0);
      assertEquals(123.456, converter.convertPropertyValue("123.456", beanPropertyInfo), 0);
      assertEquals(-256.99, converter.convertPropertyValue("-256.99", beanPropertyInfo), 0);
      assertNull(converter.convertPropertyValue(" ", beanPropertyInfo));
      assertNull(converter.convertPropertyValue("", beanPropertyInfo));
      assertNull(converter.convertPropertyValue(null, beanPropertyInfo));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testDoubleInvalid() throws ConfigurationException
   {
      NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
      converter.convertPropertyValue("123.456.789", beanPropertyInfo);
   }
   

}
