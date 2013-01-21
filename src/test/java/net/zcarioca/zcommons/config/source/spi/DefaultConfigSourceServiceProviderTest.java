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
package net.zcarioca.zcommons.config.source.spi;

import static net.zcarioca.zcommons.config.ConfigurationConstants.*;

import static org.junit.Assert.*;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;

import org.junit.Test;

/**
 * Tests the {@link DefaultConfigSourceServiceProvider}.
 * 
 * 
 * @author zcarioca
 */
public class DefaultConfigSourceServiceProviderTest
{
   private PropertiesBuilderFactory factory = new PropertiesBuilderFactory();

   @Test
   public void testGetProviderID()
   {
      ConfigurationSourceProvider provider = new DefaultConfigSourceServiceProvider();
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, provider.getProviderID());
   }

   @Test
   public void testGetProperties() throws Exception
   {
      ConfigurationSourceProvider provider = new DefaultConfigSourceServiceProvider();
      assertNotNull(provider.getProperties(new ConfigurationSourceIdentifier(getClass(), "/log4j.properties"), this.factory));
   }

   @Test
   public void testGetPropertiesNullFactory() throws Exception
   {
      ConfigurationSourceProvider provider = new DefaultConfigSourceServiceProvider();
      assertNotNull(provider.getProperties(new ConfigurationSourceIdentifier(getClass(), "/log4j.properties"),null));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testGetPropertiesInvalidIdentifier() throws Exception
   {
      ConfigurationSourceProvider provider = new DefaultConfigSourceServiceProvider();
      provider.getProperties(new ConfigurationSourceIdentifier(this), this.factory);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetPropertiesNullID() throws Exception
   {
      ConfigurationSourceProvider provider = new DefaultConfigSourceServiceProvider();
      assertNotNull(provider.getProperties(null, this.factory));
   }
}
