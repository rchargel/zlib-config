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

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ValueConverterRegistry} class.
 * 
 * 
 * @author zcarioca
 */
public class ValueConverterRegistryTest
{
   private ValueConverterRegistry registry;
   
   @Before
   public void setup() 
   {
      registry = new ValueConverterRegistry();
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.data.ValueConverterRegistry#getRegistry()}.
    */
   @Test
   public void testGetRegistry()
   {
      assertNotNull(ValueConverterRegistry.getRegistry());
      assertSame(ValueConverterRegistry.getRegistry(), ValueConverterRegistry.getRegistry());
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.data.ValueConverterRegistry#register(net.zcarioca.zcommons.config.data.PropertyConverter)}.
    */
   @Test
   public void testRegister() throws ConfigurationException
   {
      registry.register(new PropertyConverter<SimpleDateFormat>()
      {
         @Override
         public Class<SimpleDateFormat> getSupportedClass()
         {
            return SimpleDateFormat.class;
         }
         
         @Override
         public SimpleDateFormat convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
         {
            return new SimpleDateFormat(value);
         }
      });
      
      assertNotNull(registry.getPropertyConverter(SimpleDateFormat.class));
      assertEquals(SimpleDateFormat.class, registry.getPropertyConverter(SimpleDateFormat.class).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter(File.class).getSupportedClass());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testRegisterNull() throws ConfigurationException
   {
      registry.register(null);
   }
   
   @Test(expected = ConfigurationException.class)
   public void testRegisterBad() throws ConfigurationException
   {
      registry.register(new PropertyConverter<SimpleDateFormat>()
      {
         @Override
         public Class<SimpleDateFormat> getSupportedClass()
         {
            return null;
         }
         
         @Override
         public SimpleDateFormat convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
         {
            return null;
         }
      });
   }
   
   /**
    * Test method for {@link net.zcarioca.zcommons.config.data.ValueConverterRegistry#getPropertyConverter(java.lang.Class)}.
    */
   @Test
   public void testGetPropertyConverter() 
   {
      assertEquals(Boolean.class, registry.getPropertyConverter(boolean.class).getSupportedClass());
      assertEquals(Boolean.class, registry.getPropertyConverter(Boolean.class).getSupportedClass());
      assertEquals(Character.class, registry.getPropertyConverter(char.class).getSupportedClass());
      assertEquals(Character.class, registry.getPropertyConverter(Character.class).getSupportedClass());
      assertEquals(Byte.class, registry.getPropertyConverter(byte.class).getSupportedClass());
      assertEquals(Byte.class, registry.getPropertyConverter(Byte.class).getSupportedClass());
      assertEquals(Short.class, registry.getPropertyConverter(short.class).getSupportedClass());
      assertEquals(Short.class, registry.getPropertyConverter(Short.class).getSupportedClass());
      assertEquals(Integer.class, registry.getPropertyConverter(int.class).getSupportedClass());
      assertEquals(Integer.class, registry.getPropertyConverter(Integer.class).getSupportedClass());
      assertEquals(Long.class, registry.getPropertyConverter(long.class).getSupportedClass());
      assertEquals(Long.class, registry.getPropertyConverter(Long.class).getSupportedClass());
      assertEquals(Float.class, registry.getPropertyConverter(float.class).getSupportedClass());
      assertEquals(Float.class, registry.getPropertyConverter(Float.class).getSupportedClass());
      assertEquals(Double.class, registry.getPropertyConverter(double.class).getSupportedClass());
      assertEquals(Double.class, registry.getPropertyConverter(Double.class).getSupportedClass());
      assertEquals(String.class, registry.getPropertyConverter(String.class).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter(File.class).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter(SimpleDateFormat.class).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter(Object.class).getSupportedClass());
      assertEquals(Date.class, registry.getPropertyConverter(Date.class).getSupportedClass());
      assertEquals(Calendar.class, registry.getPropertyConverter(Calendar.class).getSupportedClass());
   }
   
   @Test
   public void testGetPropertyConverterForArray()
   {
      assertEquals(Boolean.class, registry.getPropertyConverter((new boolean[0]).getClass()).getSupportedClass());
      assertEquals(Boolean.class, registry.getPropertyConverter((new Boolean[0]).getClass()).getSupportedClass());
      assertEquals(Character.class, registry.getPropertyConverter((new char[0]).getClass()).getSupportedClass());
      assertEquals(Character.class, registry.getPropertyConverter((new Character[0]).getClass()).getSupportedClass());
      assertEquals(Byte.class, registry.getPropertyConverter((new byte[0]).getClass()).getSupportedClass());
      assertEquals(Byte.class, registry.getPropertyConverter((new Byte[0]).getClass()).getSupportedClass());
      assertEquals(Short.class, registry.getPropertyConverter((new short[0]).getClass()).getSupportedClass());
      assertEquals(Short.class, registry.getPropertyConverter((new Short[0]).getClass()).getSupportedClass());
      assertEquals(Integer.class, registry.getPropertyConverter((new int[0]).getClass()).getSupportedClass());
      assertEquals(Integer.class, registry.getPropertyConverter((new Integer[0]).getClass()).getSupportedClass());
      assertEquals(Long.class, registry.getPropertyConverter((new long[0]).getClass()).getSupportedClass());
      assertEquals(Long.class, registry.getPropertyConverter((new Long[0]).getClass()).getSupportedClass());
      assertEquals(Float.class, registry.getPropertyConverter((new float[0]).getClass()).getSupportedClass());
      assertEquals(Float.class, registry.getPropertyConverter((new Float[0]).getClass()).getSupportedClass());
      assertEquals(Double.class, registry.getPropertyConverter((new double[0]).getClass()).getSupportedClass());
      assertEquals(Double.class, registry.getPropertyConverter((new Double[0]).getClass()).getSupportedClass());
      assertEquals(String.class, registry.getPropertyConverter((new String[0]).getClass()).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter((new File[0]).getClass()).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter((new SimpleDateFormat[0]).getClass()).getSupportedClass());
      assertEquals(Object.class, registry.getPropertyConverter((new Object[0]).getClass()).getSupportedClass());
      assertEquals(Date.class, registry.getPropertyConverter((new Date[0]).getClass()).getSupportedClass());
      assertEquals(Calendar.class, registry.getPropertyConverter((new Calendar[0]).getClass()).getSupportedClass());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetPropertyConverterNull() 
   {
      registry.getPropertyConverter(null);
   }
}
