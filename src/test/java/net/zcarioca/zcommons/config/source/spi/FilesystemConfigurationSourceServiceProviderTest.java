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
package net.zcarioca.zcommons.config.source.spi;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.Environment;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.spring.ConfigurableObject;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;
import net.zcarioca.zcommons.config.util.UnconfigurableObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link FilesystemConfigurationSourceServiceProvider}.
 * 
 * @author zcarioca
 */
public class FilesystemConfigurationSourceServiceProviderTest
{
   private static File appDir;
   private static File confDir;
   
   private Environment environment;
   
   @BeforeClass
   public static void setupFiles() throws Exception
   {
      File tmpDir = new File(System.getProperty("java.io.tmpdir"));
      appDir = new File(tmpDir, "app_root");
      confDir = new File(appDir, "conf");
      
      confDir.mkdirs();
      
      File thisDir = new File(confDir, "net/zcarioca/zcommons/config/source/spi");
      thisDir.mkdirs();
      
      copyFile("net/zcarioca/zcommons/config/util/configurableobject.properties", new File(confDir, "configurableobject.properties"));
      copyFile("net/zcarioca/zcommons/config/util/test.properties", new File(thisDir, "test.properties"));
      copyFile("net/zcarioca/zcommons/config/source/baddata.properties", new File(confDir, "baddata.properties"));
   }
   
   private static void copyFile(String classpathResource, File file) throws Exception
   {
      InputStream in = ClassLoader.getSystemResourceAsStream(classpathResource);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
      
      IOUtils.copy(in, out);
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(out);
   }
   
   @AfterClass
   public static void clearFiles() throws Exception
   {
      FileUtils.deleteDirectory(confDir);
   }
   
   @Before
   public void setupMock() throws Exception
   {
      environment = mock(Environment.class);
      when(environment.getEnvVariable("APP_ROOT")).thenReturn(appDir.getAbsolutePath());
   }

   @Test
   public void testGetResourceName()
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      assertEquals("filesystemconfigurationsourceserviceprovidertest.properties", fcsp.getResourceName(new ConfigurationSourceIdentifier(this)));
      assertEquals("configuration.properties", fcsp.getResourceName(new ConfigurationSourceIdentifier(getClass(), "configuration.properties")));
      assertEquals("configuration.xml", fcsp.getResourceName(new ConfigurationSourceIdentifier(getClass(), "configuration.xml")));
   }
   
   @Test
   public void testGetProperties() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      assertNotNull(fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory(false, false)));
   }
   
   @Test
   public void testGetPropertiesNested() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      assertNotNull(fcsp.getProperties(new ConfigurationSourceIdentifier(new TestOne()), new PropertiesBuilderFactory(false, false)));
   }
   
   @Test
   public void testGetMonitoredConfigurationDirectory()
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      assertEquals(confDir.getAbsolutePath(), fcsp.getMonitoredConfigurationDirectory());
   }
   
   @Test
   public void testBadMonitoredConfigurationDirectory()
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider();
      assertNull(fcsp.getMonitoredConfigurationDirectory());
   }
   
   @Test(expected = ConfigurationException.class)
   public void testBadGetProperties() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider();
      fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory());
   }
   
   @Test
   public void testGetMonitoredFiles() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory(false, false));
      Collection<File> monitoredFiles = fcsp.getMonitoredFiles();
      
      assertEquals(1, monitoredFiles.size());
      assertTrue(monitoredFiles.contains(new File(confDir, "configurableobject.properties")));
   }
   
   @Test
   public void testPostInit() 
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      
      // should be able to call as many times as a want
      fcsp.postInit();
      fcsp.postInit();
      fcsp.postInit();
      
      fcsp.preDestroy();
   }
   
   @Test(expected = ConfigurationException.class)
   public void testNoConfigurationFile() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      fcsp.getProperties(new ConfigurationSourceIdentifier(new UnconfigurableObject()), new PropertiesBuilderFactory());
   }
   
   @Test(expected = ConfigurationException.class)
   public void testBadConfigurationFile() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      fcsp.getProperties(new ConfigurationSourceIdentifier(getClass(), "baddata.properties"), new PropertiesBuilderFactory());
   }
   
   @Test(expected = ConfigurationException.class)
   public void testMissingConfigurationFile() throws ConfigurationException
   {
      FilesystemConfigurationSourceServiceProvider fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
      fcsp.getProperties(new ConfigurationSourceIdentifier(new TestTwo()), new PropertiesBuilderFactory());
   }
   
   @Configurable(resourceName="test")
   public static class TestOne
   {
      @ConfigurableAttribute(propertyName="value.1")
      private String valueOne;
      
      @ConfigurableAttribute(propertyName="value.2")
      private String valueTwo;
      
      public String getValueOne()
      {
         return this.valueOne;
      }
      
      public void setValueOne(String valueOne)
      {
         this.valueOne = valueOne;
      }
      
      public String getValueTwo()
      {
         return this.valueTwo;
      }
      
      public void setValueTwo(String valueTwo)
      {
         this.valueTwo = valueTwo;
      }
   }

   @Configurable(resourceName = "test2.propertes")
   public static class TestTwo extends TestOne
   {
      // no overrides
   }
   
}
