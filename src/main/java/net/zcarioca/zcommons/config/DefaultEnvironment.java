/*
 * Project: zlib-config
 * 
 * Copyright (C) 2012 zcarioca.net
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

import java.util.Map;

public class DefaultEnvironment implements Environment 
{
   @Override
   public Map<String, String> getAllEnvProperties() 
   {
      return System.getenv();
   }
   
   @Override
   public String getEnvVariable(String envVar) 
   {
      return getEnvVariable(envVar, null);
   }

   @Override
   public String getEnvVariable(String envVar, String defaultValue) 
   {
      String value = System.getenv(envVar);
      if (value == null)
      {
          value = defaultValue;
      }
      return value;
   }

}
