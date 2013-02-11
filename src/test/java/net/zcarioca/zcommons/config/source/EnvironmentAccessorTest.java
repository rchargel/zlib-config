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
package net.zcarioca.zcommons.config.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.Environment;
import net.zcarioca.zcommons.config.EnvironmentAccessor;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link EnvironmentAccessor} and the {@link Environment}.
 * 
 * Does not extend {@link BaseTestCase}. Uses the real environment object.
 * 
 * @author zcarioca
 */
public class EnvironmentAccessorTest
{
   private Environment environment;

   @Before
   public void setupEnvironment()
   {
      environment = EnvironmentAccessor.getInstance().getEnvironment();
   }

   @Test
   public void testGetAllEnvProperties()
   {
      Map<String, String> env = environment.getAllEnvProperties();
      assertNotNull(env);
      assertFalse(env.isEmpty());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testGetAllEnvPropertiesImmutable()
   {
      Map<String, String> env = environment.getAllEnvProperties();
      env.clear();
   }

   @Test
   public void testGetAllSystemProperties()
   {
      Map<String, String> sys = environment.getAllSystemProperties();
      assertNotNull(sys);
      assertFalse(sys.isEmpty());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testGetAllSystemPropertiesImmutable()
   {
      Map<String, String> sys = environment.getAllSystemProperties();
      sys.clear();
   }

   @Test
   public void testGetEnvVariable()
   {
      assertNull(environment.getEnvVariable("fake-env-variable-123456"));
   }

   @Test
   public void testGetEnvVariableWithDefault()
   {
      assertEquals("fake value", environment.getEnvVariable("fake-env-variable-123456", "fake value"));
   }

   @Test
   public void testGetSystemProperty()
   {
      assertNull(environment.getSystemProperty("fake-system-property-123456"));
      assertNotNull(environment.getSystemProperty("java.io.tmpdir"));
   }

   public void testGetSystemPropertyWithDefault()
   {
      assertEquals("fake value", environment.getSystemProperty("fake-system-property-123456", "fake value"));

      System.setProperty("another-sys-property", "value123");
      assertEquals("value123", environment.getSystemProperty("another-sys-property", "fake value"));
      System.clearProperty("another-sys-property");
   }

}
