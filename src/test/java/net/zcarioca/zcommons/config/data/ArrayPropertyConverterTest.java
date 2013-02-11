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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.junit.Test;

/**
 * Tests the {@link ArrayPropertyConverter}.
 * 
 * 
 * @author zcarioca
 */
public class ArrayPropertyConverterTest extends BaseConverterTestCase
{

   @Test
   public void testSplit()
   {
      ArrayPropertyConverter<String[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(String[].class);

      String[] output = converter.split("this, \"\"is\"\", \"a test\", \"of, my,delimitedlist\", oh yes, ");

      assertEquals(6, output.length);
      assertArrayEquals(toArray("this", "\"is\"", "a test", "of, my,delimitedlist", "oh yes", ""), output);
   }

   @Test
   public void testBoolean() throws ConfigurationException
   {
      ArrayPropertyConverter<boolean[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(boolean[].class);
      boolean[] data = converter.convertPropertyValue("true, false, 0, 1, yes", beanPropertyInfo);

      assertEquals(5, data.length);
      assertBooleanArrayEquals(new boolean[] { true, false, false, true, true }, data);
   }

   @Test
   public void testBooleanWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<boolean[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(boolean[].class);
      boolean[] data = converter.convertPropertyValue("true, false, , 1, yes", beanPropertyInfo);

      assertEquals(5, data.length);
      assertBooleanArrayEquals(new boolean[] { true, false, false, true, true }, data);

   }

   @Test
   public void testCharWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<char[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(char[].class);
      char[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new char[] { '0', '1', '\u0000', '3', '4' }, data);
   }

   @Test
   public void testFloatWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<float[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(float[].class);
      float[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertFloatArrayEquals(new float[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testDoubleWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<double[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(double[].class);
      double[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertDoubleArrayEquals(new double[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testByteWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<byte[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(byte[].class);
      byte[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new byte[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testShortWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<short[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(short[].class);
      short[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new short[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testIntWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<int[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(int[].class);
      int[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new int[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testLongWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<long[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(long[].class);
      long[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new long[] { 0, 1, 0, 3, 4 }, data);
   }

   @Test
   public void testStringWithEmptyValue() throws ConfigurationException
   {
      ArrayPropertyConverter<String[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(String[].class);
      String[] data = converter.convertPropertyValue("0, 1, , 3, 4", beanPropertyInfo);

      assertEquals(5, data.length);
      assertArrayEquals(new String[] { "0", "1", "", "3", "4" }, data);
   }

   @Test
   public void testDateWithEmptyValue() throws ConfigurationException, ParseException
   {
      setPropertyAnnotations(mockConfigurableDateFormat("yyyy"));

      ArrayPropertyConverter<Date[]> converter = ArrayPropertyConverter.createNewArrayPropertyConverter(Date[].class);
      Date[] data = converter.convertPropertyValue("2001, 2002, , 2004, 2005", beanPropertyInfo);

      assertEquals(5, data.length);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
      assertArrayEquals(new Date[] { sdf.parse("2001"), sdf.parse("2002"), null, sdf.parse("2004"), sdf.parse("2005") }, data);
   }

   private String[] toArray(String... strings)
   {
      return strings;
   }

   private void assertBooleanArrayEquals(boolean[] expected, boolean[] actual)
   {
      assertEquals(expected.length, actual.length);
      for (int i = 0; i < expected.length; i++)
      {
         assertEquals(expected[i], actual[i]);
      }
   }

   private void assertFloatArrayEquals(float[] expected, float[] actual)
   {
      assertEquals(expected.length, actual.length);
      for (int i = 0; i < expected.length; i++)
      {
         assertEquals(expected[i], actual[i], 0);
      }
   }

   private void assertDoubleArrayEquals(double[] expected, double[] actual)
   {
      assertEquals(expected.length, actual.length);
      for (int i = 0; i < expected.length; i++)
      {
         assertEquals(expected[i], actual[i], 0);
      }
   }

}
