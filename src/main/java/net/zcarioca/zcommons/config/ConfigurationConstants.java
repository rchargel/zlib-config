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
package net.zcarioca.zcommons.config;

import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;

/**
 * A set of constants for the configuration service provider.
 *
 * @author zcarioca
 */
public interface ConfigurationConstants
{
   /**
    * This is the ID of the default provider for the
    * {@link ConfigurationSourceProvider} service. This provider will always be used,
    * if either no ID was specified in the provider-bean mapping, if the
    * requested provider is unavailable, or unless the default provider was overridden.
    */
   public static String DEFAULT_CONFIGURATION_SOURCE_SERVICE_PROVIDER = "net.zcarioca.zcommons.config.default";

   /**
    * This is the ID of the file-system based provider for the
    * {@link ConfigurationSourceProvider} service.
    */
   public static String FILESYSTEM_CONFIGURATION_SOURCE_SERVICE_PROVIDER = "net.zcarioca.zcommons.config.filesystem";

   /**
    * If the {@link Configurable} annotation has its resourceName set to
    * this value, the {@link ConfigurationSourceProvider} implementation should use
    * whatever it considers as the default resource name.
    */
   public static String DEFAULT_RESOURCE_NAME = "$default";

   /**
    * If the {@link Configurable} annotation has its referenceClass set to this
    * value, the {@link ConfigurationSourceProvider} implementation should consider
    * the bean's class as the reference class.
    */
   public static Class<?> DEFAULT_REFERENCE_CLASS = Configurable.class;
}
