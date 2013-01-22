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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

/**
 * Used to create {@link Environment} objects.
 * 
 * @author zcarioca
 */
public class EnvironmentAccessor
{
   private static EnvironmentAccessor environmentAccessor;

   private Environment environment;

   public static EnvironmentAccessor getInstance()
   {
      if (environmentAccessor == null)
      {
         environmentAccessor = new EnvironmentAccessor();
      }
      return environmentAccessor;
   }

   public Environment getEnvironment()
   {
      return this.environment;
   }

   public void setEnvironment(Environment environment)
   {
      this.environment = environment;
   }

   private EnvironmentAccessor()
   {
      this.environment = new DefaultEnvironment();
   }

   static class DefaultEnvironment implements Environment
   {
      @Override
      public Map<String, String> getAllEnvProperties()
      {
         return Collections.unmodifiableMap(System.getenv());
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Map<String, String> getAllSystemProperties()
      {
         Map<String, String> props = new HashMap<String, String>();
         for (Entry<Object, Object> entry : System.getProperties().entrySet())
         {
            props.put(entry.getKey().toString(), entry.getValue().toString());
         }
         return Collections.unmodifiableMap(props);
      }

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
         return System.getProperty(propertyName);
      }

      @Override
      public String getEnvVariable(String envVar, String defaultValue)
      {
         String value = getAllEnvProperties().get(envVar);
         if (StringUtils.isBlank(value))
         {
            value = defaultValue;
         }
         return value;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public String getSystemProperty(String propertyName, String defaultValue)
      {
         return System.getProperty(propertyName, defaultValue);
      }
   }
}
