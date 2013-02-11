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

import java.util.Properties;

import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.ConfigurationProcessListener;
import net.zcarioca.zcommons.config.ConfigurationUpdateListener;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the {@link ConfigurationUtilities} class.
 * 
 * @author zcarioca
 */
public class ConfigurationUtilitiesTest extends BaseTestCase
{
   private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtilitiesTest.class);
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
      assertSame(ConfigurationUtilities.getInstance(), ConfigurationUtilities.getInstance());
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
    * Test method for
    * {@link net.zcarioca.zcommons.config.util.ConfigurationUtilities#configureBean(java.lang.Object)}
    * .
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
      assertEquals((byte) 120, nfc.getMyByte());
      assertEquals(Boolean.TRUE, nfc.getTrueFalse());

   }

   /**
    * Test method for
    * {@link net.zcarioca.zcommons.config.util.ConfigurationUtilities#configureBean(java.lang.Object, boolean)}
    * .
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
      assertEquals((byte) 120, obj.getMyByte());
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
      Properties props = utils.loadProperties(ConfigurableObject.class, "configurableobject.properties");
      assertNotNull(props);
      assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", props.getProperty("property.message"));
   }

   @Test(expected = ConfigurationException.class)
   public void testLoadPropertiesBadProps() throws Exception
   {
      utils.loadProperties(ConfigurableObject.class, "unconfigurableobject.properties");
   }

   private static class MockProcessListener implements ConfigurationProcessListener
   {
      public void completedConfiguration(Object bean)
      {
         logger.debug("Completed Configuration: " + bean);
      }

      public void startingConfiguration(Object bean)
      {
         logger.debug("Starting Configuration: " + bean);
      }
   }

   private static class MockUpdateListener implements ConfigurationUpdateListener
   {
      private Object lastCompleted;
      private int count = 0;

      public void startingBeanUpdate(Object bean)
      {
         logger.debug("Starting Bean Update: " + bean);
      }

      public void completedBeanUpdate(Object bean)
      {
         logger.debug("Completed Bean Update: " + bean);
         this.lastCompleted = bean;
         this.count++;
      }
   }
}