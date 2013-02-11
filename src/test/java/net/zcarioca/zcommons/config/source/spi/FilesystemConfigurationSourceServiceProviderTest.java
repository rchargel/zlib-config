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

import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.CONF_DIR_OVERRIDE;
import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.DEFAULT_CONF_DIR;
import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.DEFAULT_ROOT_DIR_ENV_VAR;
import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.ROOT_DIR_ENV_OVERRIDE;
import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.ROOT_DIR_OVERRIDE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.Environment;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.util.ConfigurableObject;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;
import net.zcarioca.zcommons.config.util.UnconfigurableObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link FilesystemConfigurationSourceServiceProvider}.
 * 
 * @author zcarioca
 */
public class FilesystemConfigurationSourceServiceProviderTest extends BaseTestCase
{
   private static File appDir;
   private static File confDir;

   private Environment environment;
   private FilesystemConfigurationSourceServiceProvider fcsp;

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
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn(DEFAULT_CONF_DIR);

      fcsp = new FilesystemConfigurationSourceServiceProvider(environment);
   }

   @After
   public void cleanup() throws Exception
   {
      fcsp.preDestroy();
   }

   @Test
   public void testGetResourceName()
   {
      assertEquals("filesystemconfigurationsourceserviceprovidertest", fcsp.getResourceName(new ConfigurationSourceIdentifier(this)));
      assertEquals("configuration.properties", fcsp.getResourceName(new ConfigurationSourceIdentifier(getClass(), "configuration.properties")));
      assertEquals("configuration.xml", fcsp.getResourceName(new ConfigurationSourceIdentifier(getClass(), "configuration.xml")));
   }

   @Test
   public void testGetProperties() throws ConfigurationException
   {
      assertNotNull(fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory(false, false)));
   }

   @Test
   public void testGetPropertiesNested() throws ConfigurationException
   {
      assertNotNull(fcsp.getProperties(new ConfigurationSourceIdentifier(new TestOne()), new PropertiesBuilderFactory(false, false)));
   }

   @Test
   public void testGetMonitoredConfigurationDirectory()
   {
      assertEquals(confDir.getAbsolutePath(), fcsp.getMonitoredConfigurationDirectory());
   }

   @Test
   public void testBadMonitoredConfigurationDirectory()
   {
      when(environment.getEnvVariable("APP_ROOT")).thenReturn(null);
      assertNull(fcsp.getMonitoredConfigurationDirectory());
   }

   @Test(expected = ConfigurationException.class)
   public void testBadGetProperties() throws ConfigurationException
   {
      when(environment.getEnvVariable("APP_ROOT")).thenReturn(null);
      fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory());
   }

   @Test
   public void testGetMonitoredFiles() throws ConfigurationException
   {
      fcsp.getProperties(new ConfigurationSourceIdentifier(new ConfigurableObject()), new PropertiesBuilderFactory(false, false));
      Collection<File> monitoredFiles = fcsp.getMonitoredFiles();

      assertEquals(1, monitoredFiles.size());
      assertTrue(monitoredFiles.contains(new File(confDir, "configurableobject.properties")));
   }

   @Test
   public void testPostInit()
   {
      // should be able to call as many times as a want
      fcsp.postInit();
      fcsp.postInit();
      fcsp.postInit();
   }

   @Test(expected = ConfigurationException.class)
   public void testNoConfigurationFile() throws ConfigurationException
   {
      fcsp.getProperties(new ConfigurationSourceIdentifier(new UnconfigurableObject()), new PropertiesBuilderFactory());
   }

   @Test(expected = ConfigurationException.class)
   public void testBadConfigurationFile() throws ConfigurationException
   {
      fcsp.getProperties(new ConfigurationSourceIdentifier(getClass(), "baddata.properties"), new PropertiesBuilderFactory());
   }

   @Test(expected = ConfigurationException.class)
   public void testMissingConfigurationFile() throws ConfigurationException
   {
      fcsp.getProperties(new ConfigurationSourceIdentifier(new TestTwo()), new PropertiesBuilderFactory());
   }

   @Configurable(resourceName = "test")
   public static class TestOne
   {
      @ConfigurableAttribute(propertyName = "value.1")
      private String valueOne;

      @ConfigurableAttribute(propertyName = "value.2")
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

   @Configurable(resourceName = "test2.properties")
   private static class TestTwo extends TestOne
   {
      // no overrides
   }

}
