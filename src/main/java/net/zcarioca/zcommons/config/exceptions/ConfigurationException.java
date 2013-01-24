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
package net.zcarioca.zcommons.config.exceptions;

/**
 * A simple exception thrown when there is an error with the configuration
 * mechanism.
 * <p>
 * This is a checked exception because it is necessary, when utilizing the API directory, to force the user
 * to catch this exception.
 * </p>
 * 
 * @author zcarioca
 */
public class ConfigurationException extends Exception
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructor for the ConfigurationException.
    * 
    * @param message The error message.
    */
   public ConfigurationException(String message)
   {
      super(message);
   }

   /**
    * Constructor for the ConfigurationException.
    * 
    * @param message The error message.
    * @param cause The cause of the exception.
    */
   public ConfigurationException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
