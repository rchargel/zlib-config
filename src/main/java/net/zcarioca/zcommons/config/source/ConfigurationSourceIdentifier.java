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

import static net.zcarioca.zcommons.config.ConfigurationConstants.*;
import net.zcarioca.zcommons.config.Configurable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a reference class and a resource location to point to a configuration
 * data source. It is the responsibility of the {@link ConfigurationSourceProvider}
 * implementation to actually find and process the configuration.
 * 
 * @author zcarioca
 */
public final class ConfigurationSourceIdentifier
{
   private static final Logger logger = LoggerFactory.getLogger(ConfigurationSourceIdentifier.class);
   private Class<?> referenceClass;
   private String resourceName;
   
   public ConfigurationSourceIdentifier(Object bean)
   {
      Class<?> referenceClass = bean.getClass();
      String resourceName = bean.getClass().getSimpleName().toLowerCase();
      if (bean.getClass().isAnnotationPresent(Configurable.class))
      {
         Configurable conf = bean.getClass().getAnnotation(Configurable.class);
         referenceClass = conf.referenceClass() == DEFAULT_REFERENCE_CLASS ? bean.getClass() : conf.referenceClass();
         resourceName = conf.resourceName().equals(DEFAULT_RESOURCE_NAME) ? referenceClass.getSimpleName().toLowerCase() : conf.resourceName();
      }

      setReferenceClass(referenceClass);
      setResourceName(resourceName);
   }

   public ConfigurationSourceIdentifier(Class<?> referenceClass, String resourceName)
   {
      setReferenceClass(referenceClass);
      setResourceName(resourceName);
   }

   public Class<?> getReferenceClass()
   {
      return this.referenceClass;
   }

   public void setReferenceClass(Class<?> referenceClass)
   {
      if (referenceClass == null)
      {
         logger.warn("The reference class is NULL");
         throw new IllegalArgumentException("The reference class is NULL");
      }
      this.referenceClass = referenceClass;
   }

   public String getResourceName()
   {
      return this.resourceName;
   }

   public void setResourceName(String resourceName)
   {
      if (StringUtils.isEmpty(resourceName))
      {
         logger.warn("The resourceName class is NULL");
         throw new IllegalArgumentException("The resource location is NULL");
      }
      this.resourceName = resourceName;
   }

   @Override
   public final int hashCode()
   {
      return new HashCodeBuilder(17,91)
            .append(referenceClass)
            .append(resourceName).toHashCode();
   }

   @Override
   public final boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (null == obj) return false;
      if (getClass() != obj.getClass()) return false;
      
      ConfigurationSourceIdentifier other = (ConfigurationSourceIdentifier)obj;
      EqualsBuilder equals = new EqualsBuilder();
      equals.append(referenceClass, other.referenceClass);
      equals.append(resourceName, other.resourceName);
      
      return equals.isEquals();
   }
   
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }

}
