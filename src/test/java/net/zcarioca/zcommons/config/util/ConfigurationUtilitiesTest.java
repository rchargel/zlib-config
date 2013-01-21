/*
 * Project: zlib-config
 * 
 * Copyright (C) 2010 zcarioca.net
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
package net.zcarioca.zcommons.config.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import net.zcarioca.zcommons.config.ConfigurationProcessListener;
import net.zcarioca.zcommons.config.ConfigurationUpdateListener;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ConfigurationUtilities} class.
 * 
 * @author zcarioca
 */
public class ConfigurationUtilitiesTest
{
   private ConfigurationUtilities utils;
   
   @Before
   public void setUp() throws Exception
   {
      PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));
      
      utils = ConfigurationUtilities.getInstance();
   }
   
   @After
   public void tearDown() throws Exception
   {
      ConfigurationUtilities.resetConfigurationUtilities();
   }

   @Test
   public void testGetInstance()
   {
      assertNotNull(ConfigurationUtilities.getInstance());
      assertSame(ConfigurationUtilities.getInstance(), ConfigurationUtilities.getInstance());
   }

   @Test
   public void testConvertValueType() throws Exception
   {
      String fileString = "/tmp/log4j.properties,/tmp/test.txt";
      File[] files = new File[] { new File("/tmp/log4j.properties"), new File("/tmp/test.txt")};
      File[] conv = (File[])ConfigurationUtilities.convertValueType(files.getClass(), fileString);
      
      assertEquals(files.length, conv.length);
      for (int i = 0; i < files.length; i++)
      {
         assertEquals(files[i], conv[i]);
      }
   }
   
   @Test
   public void testConvertValueTypeNull() throws Exception
   {
      assertNull(ConfigurationUtilities.convertValueType(Object.class, null));
   }
   
   @Test
   public void testConvertValueTypeString() throws Exception
   {
      assertEquals("test", ConfigurationUtilities.convertValueType(String.class, "test"));
   }
   
   @Test
   public void testConvertValueTypeBoolean() throws Exception
   {
      assertEquals(Boolean.FALSE, ConfigurationUtilities.convertValueType(Boolean.class, "false"));
   }
   
   @Test
   public void testConvertValueTypeBooleanPrim() throws Exception
   {
      assertEquals(true, ConfigurationUtilities.convertValueType(boolean.class, "true"));
   }
   
   @Test
   public void testConvertValueTypeLong() throws Exception
   {
      assertEquals(new Long(1999), ConfigurationUtilities.convertValueType(Long.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeLongPrim() throws Exception
   {
      assertEquals(1999l, ConfigurationUtilities.convertValueType(long.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeInt() throws Exception
   {
      assertEquals(new Integer(1999), ConfigurationUtilities.convertValueType(Integer.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeIntPrim() throws Exception
   {
      assertEquals(1999, ConfigurationUtilities.convertValueType(int.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeShort() throws Exception
   {
      assertEquals(new Short((short)1999), ConfigurationUtilities.convertValueType(Short.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeShortPrim() throws Exception
   {
      assertEquals((short)1999, ConfigurationUtilities.convertValueType(short.class, "1999"));
   }
   
   @Test
   public void testConvertValueTypeByte() throws Exception
   {
      assertEquals(new Byte((byte)100), ConfigurationUtilities.convertValueType(Byte.class, "100"));
   }
   
   @Test
   public void testConvertValueTypeBytePrim() throws Exception
   {
      assertEquals((byte)100, ConfigurationUtilities.convertValueType(byte.class, "100"));
   }
   
   @Test
   public void testConvertValueTypeFloat() throws Exception
   {
      assertEquals(new Float(100.001f), ConfigurationUtilities.convertValueType(Float.class, "100.001"));
   }
   
   @Test
   public void testConvertValueTypeFloatPrim() throws Exception
   {
      assertEquals(100.001f, ConfigurationUtilities.convertValueType(float.class, "100.001"));
   }
   
   @Test
   public void testConvertValueTypeDouble() throws Exception
   {
      assertEquals(new Double(100.001), ConfigurationUtilities.convertValueType(Double.class, "100.001"));
   }
   
   @Test
   public void testConvertValueTypeDoublePrim() throws Exception
   {
      assertEquals(100.001, ConfigurationUtilities.convertValueType(double.class, "100.001"));
   }
   
   @Test
   public void testConvertValueTypeChar() throws Exception
   {
      assertEquals(new Character('c'), ConfigurationUtilities.convertValueType(Character.class, "c"));
   }
   
   @Test
   public void testConvertValueTypeCharPrim() throws Exception
   {
      assertEquals('c', ConfigurationUtilities.convertValueType(char.class, "c"));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testConvertValueTypeBadValue() throws Exception
   {
      ConfigurationUtilities.convertValueType(float.class, "123.41.23");
   }
   
   @Test(expected = ConfigurationException.class)
   public void testConvertValueTypeBadClass() throws Exception
   {
      ConfigurationUtilities.convertValueType(Properties.class, "TEST");
   }

   @Test
   public void testConfigureBeanObject() throws Exception
   {  
      ConfigurableObject obj = new ConfigurableObject();
      this.utils.configureBean(obj);
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testConfigureBeanObjectNull() throws Exception
   {
      this.utils.configureBean(null);
   }
   
   @Test(expected = ConfigurationException.class)
   public void testConfigureBeanObjectInvalid() throws Exception
   {
      this.utils.configureBean(new UnconfigurableObject());
   }

   @Test
   public void testReconfigureConfigureBeanObject() throws Exception
   {  
      MockUpdateListener updateListener = new MockUpdateListener();
      this.utils.addConfigurationUpdateListener(updateListener);
      
      ConfigurableObject obj = new ConfigurableObject();
      this.utils.configureBean(obj);
      this.utils.setReconfigureOnUpdateEnabled(true);
      
      for (ConfigurationSourceIdentifier sourceId : this.utils.getConfiguredSourceIdentifiers())
      {
         this.utils.runReconfiguration(sourceId);
      }
      assertEquals(1, updateListener.count);
      assertEquals(obj, updateListener.lastCompleted);
      updateListener.lastCompleted = null;
      
      this.utils.setReconfigureOnUpdateEnabled(false);
      for (ConfigurationSourceIdentifier sourceId : this.utils.getConfiguredSourceIdentifiers()) 
      {
         this.utils.runReconfiguration(sourceId);
      }
      assertEquals(1, updateListener.count);
      assertNull(updateListener.lastCompleted);
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.util.ConfigurationUtilities#configureBean(java.lang.Object)}.
    */
   @Test
   public void testNotFullyConfiguredBeanObject() throws Exception
   {
      NotFullyConfiguredObject nfc = new NotFullyConfiguredObject();
      this.utils.configureBean(nfc);
      
      assertEquals(0.34, nfc.anotherFloat, 0.000001);
      assertEquals(500, nfc.anotherLongValue);
      assertEquals(1780000, nfc.longValue);
      assertEquals('S', nfc.getaCharacter());
      assertEquals("This is a simple message", nfc.getFieldMessage());
      assertEquals(new Double(123.56), nfc.getFloatingPointNumber());
      assertEquals("Hello Z Carioca!", nfc.getMessage());
      assertEquals((byte)120, nfc.getMyByte());
      assertEquals(Boolean.TRUE, nfc.getTrueFalse());
      
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.util.ConfigurationUtilities#configureBean(java.lang.Object, boolean)}.
    */
   @Test
   public void testConfigureBeanObjectBoolean() throws Exception
   {
      MockProcessListener listener = new MockProcessListener();
      MockUpdateListener updateListener = new MockUpdateListener();
      this.utils.addConfigurationProcessListener(listener);
      this.utils.addConfigurationUpdateListener(updateListener);
      ConfigurableObject obj = new ConfigurableObject();
      this.utils.configureBean(obj, false);
      
      assertFalse(obj.getBigNum() == (obj.getNumber() + obj.getFloatingPointNumber()));
      
      this.utils.configureBean(obj, true);
      
      assertEquals(0.34, obj.anotherFloat, 0.000001);
      assertEquals(500, obj.anotherLongValue);
      assertEquals(1780000, obj.longValue);
      assertEquals('S', obj.getaCharacter());
      assertEquals("This is a simple message", obj.getFieldMessage());
      assertEquals(new Double(123.56), obj.getFloatingPointNumber());
      assertEquals("Hello Z Carioca!", obj.getMessage());
      assertEquals((byte)120, obj.getMyByte());
      assertEquals(22, obj.getNumber());
      assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", obj.getPropMessage());
      assertEquals(Boolean.TRUE, obj.getTrueFalse());
      assertTrue(obj.getBigNum() == (obj.getNumber() + obj.getFloatingPointNumber()));
      
      this.utils.configureBean(new BadPostConstructObject(), false);
      
      assertTrue(this.utils.removeConfigurationProcessListener(listener));
      assertTrue(this.utils.removeConfigurationUpdateListener(updateListener));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testConfigurationBeanBadBean() throws Exception
   {
      this.utils.configureBean(new BadPostConstructObject(), true);
   }

   @Test
   public void testLoadProperties() throws Exception
   {
      Properties props = ConfigurationUtilities.getInstance().loadProperties(ConfigurableObject.class, "configurableobject.properties");
      assertNotNull(props);
      assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", props.getProperty("property.message"));
   }

   @Test(expected = ConfigurationException.class)
   public void testLoadPropertiesBadProps() throws Exception
   {
      ConfigurationUtilities.getInstance().loadProperties(ConfigurableObject.class, "unconfigurableobject.properties");
   }

   private static class MockProcessListener implements ConfigurationProcessListener
   {
      public void completedConfiguration(Object bean) {}
      public void startingConfiguration(Object bean) {}
   }
   
   private static class MockUpdateListener implements ConfigurationUpdateListener
   {
      private Object lastCompleted;
      private int count = 0;
      public void startingBeanUpdate(Object bean) { }
      public void completedBeanUpdate(Object bean) 
      { 
         this.lastCompleted = bean;
         this.count ++;
      }
   }

}
