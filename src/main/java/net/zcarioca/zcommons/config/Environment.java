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

/**
 * An abstraction of the system environment.
 * 
 * @author zcarioca
 *
 */
public interface Environment 
{
   /**
    * Gets a map of all the environment variables.
    * @return Returns a map of every environment variable.
    */
   public Map<String, String> getAllEnvProperties();
   
   /**
    * Gets a value out of the system environment.
    * @param envVar The environment variable.
    * @return Returns the value as a string, or NULL if none can be found.
    */
   public String getEnvVariable(String envVar);
   
   /**
    * Gets a value out of the system environment. 
    * @param envVar The environment variable.
    * @param defaultValue The default value to return if none can be found.
    * @return Returns the value of the variable or the default value.
    */
   public String getEnvVariable(String envVar, String defaultValue);
}
