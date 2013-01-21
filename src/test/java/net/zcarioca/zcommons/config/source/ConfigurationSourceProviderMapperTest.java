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

import static net.zcarioca.zcommons.config.ConfigurationConstants.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ConfigurationSourceProviderMapper}
 * 
 * 
 * @author zcarioca
 */
public class ConfigurationSourceProviderMapperTest
{
   @Before
   public void setUp() throws Exception
   {
      PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));
      ConfigurationSourceProviderMapper.getInstance().clearAssociations();
   }

   @Test
   public void testGetInstance()
   {
      assertNotNull(ConfigurationSourceProviderMapper.getInstance());
      assertSame(ConfigurationSourceProviderMapper.getInstance(), ConfigurationSourceProviderMapper.getInstance());
   }

   @Test
   public void testAddAssociationStringString() throws Exception
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.addAssociation("net.zcarioca.zcommons", MockConfSourceProvider.ID);
      assertEquals(MockConfSourceProvider.ID, mapper.getProviderID(getClass()));
      mapper.removeAssociation("net.zcarioca.zcommons");
      assertFalse(MockConfSourceProvider.ID.equals(mapper.getProviderID(getClass())));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddAssociationNullPackage() 
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.addAssociation((String)null, "test");
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddAssociationInvalidProviderID() 
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.addAssociation("net.zcarioca.zcommons", "");
   }

   @Test
   public void testAddAssociationClassOfQString()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      
      mapper.addAssociation(getClass().getPackage(), MockConfSourceProvider.ID);
      assertEquals(MockConfSourceProvider.ID, mapper.getProviderID(getClass()));
      assertFalse(MockConfSourceProvider.ID.equals(mapper.getProviderID(ConfigurationUtilities.class)));
      
      mapper.removeAssociation(getClass().getPackage());
      assertFalse(MockConfSourceProvider.ID.equals(mapper.getProviderID(getClass())));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddAssociationNullPackagePackage()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.addAssociation((Package)null, "test");
   }

   @Test
   public void testSetAssociations()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      
      mapper.setAssociations(new HashMap<String, String>());
      
      Map<String, String> map = new HashMap<String, String>();
      map.put(getClass().getPackage().getName(), "TEST");
      
      mapper.setAssociations(map);
      assertEquals("TEST", mapper.getProviderID(getClass()));
      mapper.removeAssociation(getClass().getPackage());
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(getClass()));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testSetAssociationsEmpty() 
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.setAssociations(null);
   }

   @Test
   public void testGetCurrentProviderAssocations()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      
      assertTrue(StringUtils.isEmpty(mapper.getCurrentProviderAssocations()));
      
      mapper.addAssociation(getClass().getPackage(), "TEST");
      assertEquals(getClass().getPackage().getName() + " = TEST", mapper.getCurrentProviderAssocations());

      mapper.removeAssociation(getClass().getPackage());
      assertTrue(StringUtils.isEmpty(mapper.getCurrentProviderAssocations()));
   }

   @Test
   public void testRemoveAssociation()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.removeAssociation((String)null);
      mapper.removeAssociation("");
      mapper.removeAssociation(getClass().getName());
      
      mapper.addAssociation(getClass().getPackage().getName(), "TEST");
      assertEquals("TEST", mapper.getProviderID(getClass()));
      mapper.removeAssociation(getClass().getPackage().getName());
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(getClass()));
   }

   @Test
   public void testRemoveQAssociation()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.removeAssociation((Package)null);
      mapper.removeAssociation(getClass().getPackage());
      
      mapper.addAssociation(getClass().getPackage(), "TEST");
      assertEquals("TEST", mapper.getProviderID(getClass()));
      mapper.removeAssociation(getClass().getPackage());
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(getClass()));
   }

   @Test
   public void testGetProviderID()
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      
      mapper.addAssociation(getClass().getPackage(), "TEST");
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(null));
      assertEquals("TEST", mapper.getProviderID(getClass()));
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(String.class));
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(ConfigurationUtilities.class));

      mapper.removeAssociation(getClass().getPackage());
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(getClass()));
   }
   
   @Test
   public void testGetSetDefaultConfigurationSourceProviderID() throws Exception
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getDefaultConfigurationSourceProviderID());
      
      mapper.setDefaultConfigurationSourceProviderID(MockConfSourceProvider.ID);
      assertEquals(MockConfSourceProvider.ID, mapper.getDefaultConfigurationSourceProviderID());
      
      assertEquals(MockConfSourceProvider.ID, ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider().getProviderID());
      assertEquals(MockConfSourceProvider.ID, mapper.getProviderID(getClass()));
      
      mapper.setDefaultConfigurationSourceProviderID(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getDefaultConfigurationSourceProviderID());

      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider().getProviderID());
      assertEquals(DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER, mapper.getProviderID(getClass()));
   }
   
   @Test(expected = ConfigurationException.class)
   public void testGetSetDefaultConfigurationSourceProviderIDNullID() throws Exception
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.setDefaultConfigurationSourceProviderID(null);
   }
   
   @Test(expected = ConfigurationException.class)
   public void testGetSetDefaultConfigurationSourceProviderIDInvalid() throws Exception
   {
      ConfigurationSourceProviderMapper mapper = ConfigurationSourceProviderMapper.getInstance();
      mapper.setDefaultConfigurationSourceProviderID("testing");
   }

}
