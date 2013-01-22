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

import java.util.Properties;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;

/**
 * An interface that defines the configuration source service. This interface
 * should be implemented by a service class provider.
 * 
 * @author zcarioca
 */
public interface ConfigurationSourceProvider
{
   /**
    * This is the Provider ID, which is used to determine which Provider to use.
    * This ID should be unique; as such, it is recommended that custom providers
    * include the package name in the ID.
    * 
    * @return Returns a unique Provider ID.
    */
   public String getProviderID();

   /**
    * Runs this method immediately after initializing this class.
    */
   public void postInit();

   /**
    * Runs this method immediately before destroying this class. Note: this
    * method is not guaranteed to run.
    */
   public void preDestroy();

   /**
    * Uses the {@link ConfigurationSourceIdentifier} to locate and parse the
    * data into a properties map.
    * 
    * @param configurationSourceIdentifier The source of the configuration data.
    * 
    * @param propertiesBuilderFactory The properties builder factory used to
    *        create the properties object.
    * 
    * @return Returns a {@link Configuration} object.
    */
   public Properties getProperties(ConfigurationSourceIdentifier configurationSourceIdentifier, PropertiesBuilderFactory propertiesBuilderFactory)
         throws ConfigurationException;

   /**
    * Determines whether this source provider will support the configuration
    * source identifier.
    * 
    * @param configurationSourceIdentifier The source of the configuration data.
    * @return Returns true if this provider supports the provided identifier.
    */
   public boolean supportsIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier);
}
