/*
 * Project: zlib-config
 * 
 * Copyright (C) 2013 zcarioca.net
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
package net.zcarioca.zcommons.config.data;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.apache.commons.lang.StringUtils;

/**
 * Converter for {@link Character} class.
 * 
 * 
 * @author zcarioca
 */
class CharacterPropertyConverter implements BeanPropertyConverter<Character>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<Character> getSupportedClass()
   {
      return Character.class;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Character convertPropertyValue(String value, BeanPropertyInfo beanPropertyInfo) throws ConfigurationException
   {
      // blank is okay, but empty is not
      return StringUtils.isEmpty(value) ? null : value.charAt(0);
   }

}
