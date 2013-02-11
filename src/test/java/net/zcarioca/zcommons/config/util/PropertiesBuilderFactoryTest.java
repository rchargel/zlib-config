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
import static org.junit.Assert.assertTrue;
import net.zcarioca.zcommons.config.BaseTestCase;

import org.junit.Test;

/**
 * Tests the {@link PropertiesBuilderFactory}
 * 
 * 
 * @author zcarioca
 */
public class PropertiesBuilderFactoryTest extends BaseTestCase
{

   @Test
   public void testPropertiesBuilderFactory()
   {
      PropertiesBuilderFactory factory = new PropertiesBuilderFactory();
      assertFalse(factory.isAddEnvironmentProperties());
      assertFalse(factory.isAddSystemProperties());
   }

   @Test
   public void testPropertiesBuilderFactoryFalseFalse()
   {
      PropertiesBuilderFactory factory = new PropertiesBuilderFactory(false, false);
      assertFalse(factory.isAddEnvironmentProperties());
      assertFalse(factory.isAddSystemProperties());
   }

   @Test
   public void testPropertiesBuilderFactoryFalseTrue()
   {
      PropertiesBuilderFactory factory = new PropertiesBuilderFactory(false, true);
      assertFalse(factory.isAddEnvironmentProperties());
      assertTrue(factory.isAddSystemProperties());
   }

   @Test
   public void testPropertiesBuilderFactoryTrueFalse()
   {
      PropertiesBuilderFactory factory = new PropertiesBuilderFactory(true, false);
      assertTrue(factory.isAddEnvironmentProperties());
      assertFalse(factory.isAddSystemProperties());
   }

   @Test
   public void testPropertiesBuilderFactoryTrueTrue()
   {
      PropertiesBuilderFactory factory = new PropertiesBuilderFactory(true, true);
      assertTrue(factory.isAddEnvironmentProperties());
      assertTrue(factory.isAddSystemProperties());
   }

   @Test
   public void testNewPropertiesBuilder()
   {
      PropertiesBuilder emptyBuilder = new PropertiesBuilderFactory().newPropertiesBuilder();
      assertEquals(0, emptyBuilder.size());

      PropertiesBuilder envBuilder = new PropertiesBuilderFactory(true, false).newPropertiesBuilder();
      assertEquals(2, envBuilder.size());

      PropertiesBuilder fullBuilder = new PropertiesBuilderFactory(true, true).newPropertiesBuilder();
      assertEquals(3, fullBuilder.size());
   }

}
