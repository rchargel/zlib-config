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

import static net.zcarioca.zcommons.config.ConfigurationConstants.DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * This is a mapper which can be used to associate bean classes with
 * implementations of the {@link ConfigurationSourceProvider} interface. This works
 * by associating the package names with known provider IDs. For instance, the
 * package name "net.zcarioca.zcommons.config.impl" could be associated with
 * provider "XYZ", while the package "net.zcarioca.zcommons.config.impl.test"
 * could be associated to the provider "ABC".
 * 
 * 
 * @author zcarioca
 */
@ManagedResource(objectName = "Configuration:group=Configuration,name=ConfigurationSourceProviderMapper")
public class ConfigurationSourceProviderMapper
{
   private static ConfigurationSourceProviderMapper mapper;

   private String defaultConfigurationSourceProviderID = DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER;
   
   private Map<String, String> mapping;

   /**
    * Gets access to the {@link ConfigurationSourceProviderMapper}.
    * 
    * @return Returns a reference to this singleton bean.
    */
   public static ConfigurationSourceProviderMapper getInstance()
   {
      if (mapper == null)
      {
         mapper = new ConfigurationSourceProviderMapper();
      }
      return mapper;
   }
   
   @ManagedAttribute(description = "The default configuration source provider ID")
   public String getDefaultConfigurationSourceProviderID()
   {
      return this.defaultConfigurationSourceProviderID;
   }

   /**
    * Sets a new default configuration source provider.
    * 
    * @param defaultConfigurationSourceProviderID The ID of the configuration source provider.
    * @throws ConfigurationException if the supplied provider ID is not available.
    */
   public void setDefaultConfigurationSourceProviderID(String defaultConfigurationSourceProviderID) throws ConfigurationException
   {
      if (ConfigurationSourceProviderFactory.getInstance().isConfigurationSourceProviderAvailable(defaultConfigurationSourceProviderID))
      {
         this.defaultConfigurationSourceProviderID = defaultConfigurationSourceProviderID;
      }
      else
      {
         throw new ConfigurationException(String.format("The configuration source provider '%s' is not available", defaultConfigurationSourceProviderID));
      }
   }

   @ManagedAttribute(description = "The current package - provider assocations")
   public String getCurrentProviderAssocations()
   {
      List<String> assocations = new ArrayList<String>(this.mapping.size());
      synchronized (this.mapping)
      {
         for (String key : this.mapping.keySet())
         {
            assocations.add(key + " = " + this.mapping.get(key));
         }
      }
      return StringUtils.join(assocations, '\n');
   }

   /**
    * Associates a package with a Provider ID.
    * 
    * @param packageName
    *           The package to associate to the provider.
    * @param providerId
    *           The Provider ID.
    */
   @ManagedOperation(description = "Associates a Package name to a Provider ID")
   @ManagedOperationParameters( { @ManagedOperationParameter(name = "packageName", description = "The name of the package"),
      @ManagedOperationParameter(name = "priverId", description = "The ID of the Provider") 
   })
   public void addAssociation(String packageName, String providerId)
   {
      if (StringUtils.isEmpty(packageName))
      {
         throw new IllegalArgumentException("The package name is NULL or empty");
      }
      if (StringUtils.isEmpty(providerId))
      {
         throw new IllegalArgumentException("The provider ID is NULL or empty");
      }
      synchronized (this.mapping)
      {
         this.mapping.put(packageName, providerId);
      }
   }

   /**
    * Associates a Package with a Provider ID.
    * 
    * @param beanPackage
    *           A package.
    * @param providerId
    *           The Provider ID.
    */
   public void addAssociation(Package beanPackage, String providerId)
   {
      if (beanPackage == null)
      {
         throw new IllegalArgumentException("The package  is NULL");
      }
      addAssociation(beanPackage.getName(), providerId);
   }

   /**
    * Removes an old association.
    * 
    * @param packageName The package name to remove.
    */
   @ManagedOperation(description = "Removes an old association")
   @ManagedOperationParameters({
      @ManagedOperationParameter(name = "packageName", description = "The package name to remove")
   })
   public void removeAssociation(String packageName)
   {
      synchronized (this.mapping)
      {
         this.mapping.remove(packageName);
      }
   }

   /**
    * Removes an old association.
    * 
    * @param beanPackage The package to remove.
    */
   public void removeAssociation(Package beanPackage)
   {
      if (beanPackage != null)
      {
         removeAssociation(beanPackage.getName());
      }
   }
   
   /**
    * Clears all available associations.
    */
   public void clearAssociations()
   {
      synchronized (this.mapping) 
      {
         this.mapping.clear();
      }
   }

   /**
    * Sets a full set of associations. This method does not clear out old
    * associations.
    * 
    * @param associations
    *           The association map to set.
    */
   public void setAssociations(Map<String, String> associations)
   {
      if (associations == null)
      {
         throw new IllegalArgumentException("The associations mapping is NULL");
      }
      synchronized (this.mapping)
      {
         this.mapping.putAll(associations);
      }
   }

   /**
    * Gets the associated source Provider ID, or the default source Provider ID
    * if an association cannot be found.
    * 
    * @param referenceClass
    *           The reference class.
    * 
    * @return Returns the required Provider ID.
    */
   public String getProviderID(Class<?> referenceClass)
   {
      if (referenceClass == null)
      {
         return getDefaultConfigurationSourceProviderID();
      }
      String providerId = getDefaultConfigurationSourceProviderID();
      
      synchronized (this.mapping)
      {
         String key = referenceClass.getName();

         while (key.lastIndexOf('.') != -1)
         {
            if (this.mapping.containsKey(key))
            {
               providerId = this.mapping.get(key);
               break;
            }
            key = key.substring(0, key.lastIndexOf('.'));
         }
      }
      return providerId;
   }

   private ConfigurationSourceProviderMapper()
   {
      this.mapping = new HashMap<String, String>();
   }
}
