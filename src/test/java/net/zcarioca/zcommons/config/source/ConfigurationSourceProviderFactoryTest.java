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
package net.zcarioca.zcommons.config.source;

import static net.zcarioca.zcommons.config.ConfigurationConstants.DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the {@link ConfigurationSourceProviderFactory}.
 * 
 * 
 * @author zcarioca
 */
public class ConfigurationSourceProviderFactoryTest
{

   @Test
   public void testGetInstance()
   {
      assertNotNull(ConfigurationSourceProviderFactory.getInstance());
      assertSame(ConfigurationSourceProviderFactory.getInstance(), ConfigurationSourceProviderFactory.getInstance());
   }

   @Test
   public void testGetConfigurationSourceProviderString()
   {
      ConfigurationSourceProvider provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      assertNotNull(provider);
      assertEquals(provider.getProviderID(), DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      
      provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider((String)null);
      assertNotNull(provider);
      assertEquals(provider.getProviderID(), DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      
      provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider("");
      assertNotNull(provider);
      assertEquals(provider.getProviderID(), DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      
      provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider("mocktest");
      assertNotNull(provider);
      assertEquals(provider.getProviderID(), DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      
      provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider(MockConfSourceProvider.ID);
      assertNotNull(provider);
      assertEquals(provider.getProviderID(), MockConfSourceProvider.ID);
   }
   
   @Test
   public void testIsConfigurationSourceProviderAvailable() throws Exception
   {
      ConfigurationSourceProviderFactory factory = ConfigurationSourceProviderFactory.getInstance();
      
      assertFalse(factory.isConfigurationSourceProviderAvailable(null));
      assertFalse(factory.isConfigurationSourceProviderAvailable(""));
      assertFalse(factory.isConfigurationSourceProviderAvailable("this is a face provider"));
      assertTrue(factory.isConfigurationSourceProviderAvailable(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER));
      assertTrue(factory.isConfigurationSourceProviderAvailable(MockConfSourceProvider.ID));
   }

}
