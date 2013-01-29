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

import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.Environment;
import net.zcarioca.zcommons.config.EnvironmentAccessor;
import net.zcarioca.zcommons.config.util.MockEnvironment;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link FilesystemConfiguration}.
 *
 * @author zcarioca
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FilesystemConfigurationTest extends BaseTestCase
{
   private static final Logger logger = LoggerFactory.getLogger(FilesystemConfigurationTest.class);

   // basic configuration
   private static File rootDir1;

   // override env var
   private static File rootDir2;

   // direct set root
   private static File rootDir3;

   private Environment environment;

   @BeforeClass
   public static void setup()
   {
      File tmpDir = new File(System.getProperty("java.io.tmpdir"));
      rootDir1 = new File(tmpDir, "app_root");
      rootDir2 = new File(tmpDir, "my_app_root");
      rootDir3 = new File(tmpDir, "set_app_root");

      rootDir1.mkdirs();
      rootDir2.mkdirs();
      rootDir3.mkdirs();
   }

   private static File makeConfDir(File rootDir, String path)
   {
      return makeConfDir(rootDir, path, true);
   }

   private static File makeConfDir(File rootDir, String path, boolean makeIt)
   {
      File confDir = new File(rootDir, path);
      if (!confDir.exists() && makeIt) {
         confDir.mkdirs();
      }

      return confDir;
   }

   @AfterClass
   public static void resetFS()
   {
      try {
         FileUtils.deleteDirectory(rootDir1);
         FileUtils.deleteDirectory(rootDir2);
         FileUtils.deleteDirectory(rootDir3);
      } catch (Exception exc) {
         logger.error(exc.getMessage(), exc);
      }
   }

   @Before
   public void setupMock()
   {
      environment = mock(Environment.class);
      when(environment.getEnvVariable("APP_ROOT")).thenReturn(rootDir1.getAbsolutePath());
      when(environment.getEnvVariable("MY_APP_ROOT")).thenReturn(rootDir2.getAbsolutePath());
      when(environment.getEnvVariable("BAD_ROOT")).thenReturn(null);
   }

   @After
   public void resetEnvironment()
   {
      EnvironmentAccessor.getInstance().setEnvironment(new MockEnvironment());
   }

   /*
    * TEST CONFIGURATION SETUP
    */
   @Test
   public void testNoOverrides()
   {
      // nochange
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn(DEFAULT_CONF_DIR);
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNull(conf.getRootDir());
      assertEquals(makeConfDir(rootDir1, "conf"), conf.getConfigurationDirectory());
   }

   @Test
   public void testEnvVarOverride()
   {
      // same as -Dconfig.file.rootDirEnvVar=MY_APP_ROOT
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn("MY_APP_ROOT");

      //nochange
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn(DEFAULT_CONF_DIR);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNull(conf.getRootDir());
      assertEquals("MY_APP_ROOT", conf.getRootDirEnvironmentVar());

      assertEquals(makeConfDir(rootDir2, "conf"), conf.getConfigurationDirectory());
   }

   @Test
   public void testRootDirOverride()
   {
      // same as -Dconfig.file.rootDir=/tmp
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(rootDir3.getAbsolutePath());

      // nochange
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn(DEFAULT_CONF_DIR);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNotNull(conf.getRootDir());
      assertEquals("APP_ROOT", conf.getRootDirEnvironmentVar());

      assertEquals(makeConfDir(rootDir3, "conf"), conf.getConfigurationDirectory());
   }

   @Test
   public void testConfDirOverride()
   {
      // same as -Dconfig.file.confDir=configuration/dir
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn("configuration/dir");

      // nochange
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNull(conf.getRootDir());
      assertEquals("APP_ROOT", conf.getRootDirEnvironmentVar());
      assertEquals("configuration/dir", conf.getConfDir());

      assertEquals(makeConfDir(rootDir1, "configuration/dir"), conf.getConfigurationDirectory());
   }

   @Test
   public void testConfDirRootOverride()
   {
      // same as -Dconfig.file.confDir=/
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn("/");

      // nochange
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNull(conf.getRootDir());
      assertEquals("APP_ROOT", conf.getRootDirEnvironmentVar());
      assertEquals("/", conf.getConfDir());

      assertEquals(makeConfDir(rootDir1, "/", false), conf.getConfigurationDirectory());
   }

   @Test
   public void testConfDirEmptyOverride()
   {
      // same as -Dconfig.file.confDir=""
      when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn("");

      // nochange
      when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
      when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);

      FilesystemConfiguration conf = new FilesystemConfiguration(environment);
      assertNull(conf.getRootDir());
      assertEquals("APP_ROOT", conf.getRootDirEnvironmentVar());
      assertEquals("", conf.getConfDir());

      assertEquals(rootDir1, conf.getConfigurationDirectory());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBadEnvRootVar()
   {
      System.setProperty(ROOT_DIR_ENV_OVERRIDE, "");
      FilesystemConfiguration conf = new FilesystemConfiguration(environment);

      conf.getConfigurationDirectory();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBadEnvRootValue()
   {
      System.setProperty(ROOT_DIR_ENV_OVERRIDE, "BAD_ROOT");
      FilesystemConfiguration conf = new FilesystemConfiguration(environment);

      conf.getConfigurationDirectory();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBadConfValue()
   {
      System.setProperty(CONF_DIR_OVERRIDE, "this_conf_file_does_not_exist");
      FilesystemConfiguration conf = new FilesystemConfiguration(environment);

      conf.getConfigurationDirectory();
   }
}