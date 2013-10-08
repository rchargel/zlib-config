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
package net.zcarioca.zcommons.config.source.spi;

import static net.zcarioca.zcommons.config.ConfigurationConstants.DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;

import java.io.InputStream;
import java.util.Properties;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;
import net.zcarioca.zcommons.config.util.PropertiesBuilder;

import org.apache.commons.io.IOUtils;

/**
 * A classpath based implementation of the {@link ConfigurationSourceProvider}
 * interface.
 * 
 * @author zcarioca
 */
public class DefaultConfigSourceServiceProvider extends AbstractConfigurationSourceServiceProvider implements ConfigurationSourceProvider
{
   public String getProviderID()
   {
      return DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;
   }

   /**
    * This provider is marked as a BACKUP provider.
    * 
    * {@inheritDoc}
    */
   @Override
   public Priority getPriorityLevel()
   {
      return Priority.BACKUP;
   }

   @Override
   protected String getResourceName(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      String resourceName = super.getResourceName(configurationSourceIdentifier);
      if (!resourceName.endsWith(".properties") && !resourceName.endsWith(".xml"))
      {
         resourceName = resourceName + ".properties";
      }
      return resourceName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      return true;
   }

   @Override
   protected Properties buildPropertiesFromValidInputs(Class<?> referenceClass, String resourceName, PropertiesBuilder propertiesBuilder)
         throws ConfigurationException
   {
      InputStream in = null;
      try
      {
         in = getResourceAsStream(referenceClass, resourceName);
         return propertiesBuilder.readAll(in).build();
      }
      catch (Throwable t)
      {
         throw new ConfigurationException(String.format("Could not read configuration for %s using the reference class %s", resourceName, referenceClass), t);
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }
   
   private InputStream getResourceAsStream(Class<?> referenceClass, String resourceName) 
   {
      InputStream in = referenceClass.getResourceAsStream(resourceName);
      if (in == null) 
      {
         in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
      }
      return in;
   }
}
