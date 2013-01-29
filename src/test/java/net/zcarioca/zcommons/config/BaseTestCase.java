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
package net.zcarioca.zcommons.config;

import net.zcarioca.zcommons.config.util.ConfigurationUtilities;
import net.zcarioca.zcommons.config.util.MockEnvironment;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

/**
 * A base for test classes.
 *
 * @author zcarioca
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class BaseTestCase
{
   @BeforeClass
   public static void setupMockEnvironment()
   {
      EnvironmentAccessor.getInstance().setEnvironment(new MockEnvironment());
   }

   @AfterClass
   public static void resetNormalEnvironment()
   {
      ConfigurationUtilities.getInstance().invokePreDestroyAll();
      deleteConfDir();
      EnvironmentAccessor.getInstance().setEnvironment(new EnvironmentAccessor.DefaultEnvironment());
   }

   static File getConfDir()
   {
      String appRoot = EnvironmentAccessor.getInstance().getEnvironment().getEnvVariable("APP_ROOT");
      return new File(System.getProperty("java.io.tmpDir"), String.format("%s/conf", appRoot));
   }

   static void createConfDir()
   {
      getConfDir().mkdirs();
   }

   private static void deleteConfDir()
   {
      File confDir = getConfDir();
      try {
         if (confDir.exists()) {
            FileUtils.deleteDirectory(confDir);
         }
      } catch (IOException exc) {
         //do nothing
      }
   }

}
