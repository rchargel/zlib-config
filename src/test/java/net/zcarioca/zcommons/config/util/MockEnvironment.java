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
package net.zcarioca.zcommons.config.util;

import net.zcarioca.zcommons.config.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A mock {@link Environment}.
 *
 * @author zcarioca
 */
public class MockEnvironment implements Environment
{
   private final Map<String, String> env = new HashMap<String, String>();
   private final Map<String, String> sys = new HashMap<String, String>();

   public MockEnvironment()
   {
      env.clear();
      env.put("fake-env-prop", "fake env value");
      env.put("APP_ROOT", new File(System.getProperty("java.io.tmpdir"), "app_root").getAbsolutePath());

      sys.clear();
      sys.put("fake.system.property", "fake value");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, String> getAllEnvProperties()
   {
      return env;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, String> getAllSystemProperties()
   {
      return sys;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getEnvVariable(String envVar)
   {
      return getEnvVariable(envVar, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getSystemProperty(String propertyName)
   {
      return getSystemProperty(propertyName, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getEnvVariable(String envVar, String defaultValue)
   {
      if (env.containsKey(envVar)) {
         return env.get(envVar);
      }
      return defaultValue;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getSystemProperty(String propertyName, String defaultValue)
   {
      if (sys.containsKey(propertyName)) 
      {
         return sys.get(propertyName);
      }
      return defaultValue;
   }

}
