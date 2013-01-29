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
package net.zcarioca.zcommons.config.source.spi;

import java.util.Properties;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;
import net.zcarioca.zcommons.config.util.PropertiesBuilder;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction for ease of development.
 *
 * @author zcarioca
 */
public abstract class AbstractConfigurationSourceServiceProvider implements ConfigurationSourceProvider
{
   private static final Logger logger = LoggerFactory.getLogger(AbstractConfigurationSourceServiceProvider.class);

   @Override
   public Properties getProperties(ConfigurationSourceIdentifier configurationSourceIdentifier, PropertiesBuilderFactory propertiesBuilderFactory)
         throws ConfigurationException
   {
      validateConfigurationSourceIdentifier(configurationSourceIdentifier);
      PropertiesBuilder builder = getPropertiesBuilder(propertiesBuilderFactory);

      Class<?> referenceClass = configurationSourceIdentifier.getReferenceClass();
      String resourceName = getResourceName(configurationSourceIdentifier);

      try {
         runPreProcessAction(configurationSourceIdentifier);

         return buildPropertiesFromValidInputs(referenceClass, resourceName, builder);
      } finally {
         runPostProcessAction(configurationSourceIdentifier);
      }
   }

   public void postInit()
   {
      //do nothing
   }

   public void preDestroy()
   {
      // do nothing
   }

   /**
    * Not implemented. Runs before reading the properties file.
    *
    * @param configurationSourceIdentifier The configuration source identifier.
    */
   public void runPreProcessAction(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      if (logger.isDebugEnabled())
         logger.debug(String.format("%s.runPreProcessAction(%s)", getClass(), configurationSourceIdentifier));
   }

   /**
    * Not implemented. Runs after reading the properties file.
    *
    * @param configurationSourceIdentifier The configuration source identifier.
    */
   public void runPostProcessAction(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      if (logger.isDebugEnabled())
         logger.debug(String.format("%s.runPostProcessAction(%s)", getClass(), configurationSourceIdentifier));
   }

   /**
    * Validates the configuration source identifier.
    *
    * @param configurationSourceIdentifier The {@link ConfigurationSourceIdentifier}.
    * @throws IllegalArgumentException when the configuration source identifier is null.
    */
   protected void validateConfigurationSourceIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
         throws IllegalArgumentException
   {
      if (configurationSourceIdentifier == null) {
         throw new IllegalArgumentException("NULL Configuration Source Identifier");
      }
   }

   /**
    * Gets a properties builder from the PropertiesBuilderFactory.
    *
    * @param propertiesBuilderFactory The properties builder factory.
    * @return Returns a {@link PropertiesBuilder} from the factory, or a new properties builder if the factory is null.
    */
   protected PropertiesBuilder getPropertiesBuilder(PropertiesBuilderFactory propertiesBuilderFactory)
   {
      if (propertiesBuilderFactory == null) {
         propertiesBuilderFactory = new PropertiesBuilderFactory();
      }
      return propertiesBuilderFactory.newPropertiesBuilder();
   }

   /**
    * Gets the resource name from the configuration source identifier.  This method may be overwritten if the
    * resource name requires an extension such as '.properties' or '.xml'.
    *
    * @param configurationSourceIdentifier The configuration source identifier.
    * @return Returns the resource name.
    */
   protected String getResourceName(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      return configurationSourceIdentifier.getResourceName();
   }

   /**
    * Uses previously validated inputs to build configuration properties.
    *
    * @param referenceClass    The reference class.
    * @param resourceName      The resource name.
    * @param propertiesBuilder The properties builder object.
    * @return Returns a Properties object.
    */
   protected abstract Properties buildPropertiesFromValidInputs(Class<?> referenceClass, String resourceName, PropertiesBuilder propertiesBuilder) throws ConfigurationException;

}
