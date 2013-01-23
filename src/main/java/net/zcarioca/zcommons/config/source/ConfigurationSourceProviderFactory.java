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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import net.zcarioca.zcommons.config.source.spi.DefaultConfigSourceServiceProvider;

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

   private Map<ConfigurationSourceIdentifier, ConfigurationSourceProvider> identifierMap;
   private Set<ConfigurationSourceProvider> initializedProviders;

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
      return identifierMap.get(configurationSourceIdentifier);
   }
   
   public void clearAssociations()
   {
      for (ConfigurationSourceProvider provider : identifierMap.values())
      {
         if (initializedProviders.contains(provider))
         {
            provider.preDestroy();
         }
      }
      initializedProviders.clear();
      identifierMap.clear();
   }
   
   protected void mapConfigurationSourceIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      if (identifierMap.get(configurationSourceIdentifier) != null) 
      {
         return;
      }
      if (logger.isTraceEnabled())
         logger.trace(String.format("Mapping provider for %s", configurationSourceIdentifier));
      
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
      identifierMap.put(configurationSourceIdentifier, chosenProvider);
      
      if (!initializedProviders.contains(chosenProvider)) 
      {
         chosenProvider.postInit();
         initializedProviders.add(chosenProvider);
      }
      
      if (logger.isDebugEnabled())
         logger.debug(String.format("Mapped the provider %s to the identifier %s", chosenProvider.getProviderID(), configurationSourceIdentifier));
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
      identifierMap = new HashMap<ConfigurationSourceIdentifier, ConfigurationSourceProvider>();
      initializedProviders = new HashSet<ConfigurationSourceProvider>();
   }
}
