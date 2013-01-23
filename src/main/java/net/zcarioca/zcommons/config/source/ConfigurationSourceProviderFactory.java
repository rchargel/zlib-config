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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import net.zcarioca.zcommons.config.ConfigurationConstants;
import net.zcarioca.zcommons.config.source.spi.DefaultConfigSourceServiceProvider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a factory class used to load the {@link ConfigurationSourceProvider}
 * implementations available to the ClassLoader.
 * 
 * 
 * @author zcarioca
 */
public class ConfigurationSourceProviderFactory
{
   private static ConfigurationSourceProviderFactory spiFactory;
   private static final Logger logger = LoggerFactory.getLogger(ConfigurationSourceProviderFactory.class);

   private Map<String, ConfigurationSourceProvider> providerMap;
   private Map<ConfigurationSourceIdentifier, String> identifierMap;

   /**
    * Gets access to the singleton instance.
    * 
    * @return Returns a reference to the singleton.
    */
   public static ConfigurationSourceProviderFactory getInstance()
   {
      if (spiFactory == null)
      {
         spiFactory = new ConfigurationSourceProviderFactory();
      }
      return spiFactory;
   }
   
   /**
    * Gets the configuration source provider for a given source identifier.
    * @param configurationSourceIdentifier
    * @return Returns the source provider for an identifier.
    */
   public ConfigurationSourceProvider getConfigurationSourceProvider(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      mapConfigurationSourceIdentifier(configurationSourceIdentifier);
      String providerId = identifierMap.get(configurationSourceIdentifier);
      return getConfigurationSourceProvider(providerId);
   }

   /**
    * Gets the service provider for the specified Provider ID.
    * 
    * @param providerId
    *           The Provider ID.
    * 
    * @return Returns either the requested provider, or the default provider if
    *         the requested provider could not be found.
    */
   ConfigurationSourceProvider getConfigurationSourceProvider(String providerId)
   {
      if (StringUtils.isBlank(providerId))
      {
         providerId = ConfigurationConstants.DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;
      }

      if (providerMap.containsKey(providerId))
      {
         return providerMap.get(providerId);
      }

      return providerMap.get(ConfigurationConstants.DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER);
   }
   
   /**
    * Determines whether the inserted provider ID is available.
    * 
    * @param configurationSourceProviderID The provider ID to test.
    * 
    * @return Returns true if the provider ID is available; false otherwise.
    */
   public boolean isConfigurationSourceProviderAvailable(String configurationSourceProviderID)
   {
      return providerMap.containsKey(configurationSourceProviderID);
   }
   
   /**
    * Overrides the provider map.
    * @param providerMap The provider map to override.
    */
   public void setProviderMap(Map<String, ConfigurationSourceProvider> providerMap) 
   {
      this.providerMap.putAll(providerMap);
   }
   
   public void clearAssociations()
   {
      identifierMap.clear();
   }
   
   public void clearProviders()
   {
       for (String providerId : providerMap.keySet())
       {
           providerMap.get(providerId).preDestroy();
       }
   }
   
   protected void mapConfigurationSourceIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      if (StringUtils.isNotBlank(identifierMap.get(configurationSourceIdentifier))) 
      {
         return;
      }
      Iterator<ConfigurationSourceProvider> providers = getConfigurationSourceProviders();
      
      ConfigurationSourceProvider chosenProvider = new DefaultConfigSourceServiceProvider();
      while (providers.hasNext())
      {
         ConfigurationSourceProvider provider = providers.next();
         if (provider.supportsIdentifier(configurationSourceIdentifier)
               && chosenProvider.getPriorityLevel().ordinal() < provider.getPriorityLevel().ordinal())
         {
            chosenProvider = provider;
         }
      }
      identifierMap.put(configurationSourceIdentifier, chosenProvider.getProviderID());
   }

   /**
    * Gets an iterator for each service loader available.
    * 
    * @return Returns an iterator to the service loaders.
    */
   protected Iterator<ConfigurationSourceProvider> getConfigurationSourceProviders()
   {
      ServiceLoader<ConfigurationSourceProvider> serviceLoader = ServiceLoader.load(ConfigurationSourceProvider.class);
      return serviceLoader.iterator();
   }
   
   private ConfigurationSourceProviderFactory()
   {
      providerMap = new HashMap<String, ConfigurationSourceProvider>();
      identifierMap = new HashMap<ConfigurationSourceIdentifier, String>();
      
      Iterator<ConfigurationSourceProvider> iterator = getConfigurationSourceProviders();
      while (iterator.hasNext())
      {
         ConfigurationSourceProvider spi = iterator.next();
         
         if (logger.isDebugEnabled()) 
         {
            logger.debug(String.format("Setting up provider: %s", spi.getProviderID()));
         }
         
         try
         {
            spi.postInit();
         
            providerMap.put(spi.getProviderID(), spi);
         }
         catch (IllegalArgumentException exc)
         {
            logger.warn(String.format("Could not add configuration source provider %s: %s", spi.getProviderID(), exc.getMessage()), exc);
         }
      }
   }
}
