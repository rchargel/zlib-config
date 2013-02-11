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
package net.zcarioca.zcommons.config.util;

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;

/**
 * A fake object that is configurable.
 * 
 * @author zcarioca
 */
@Configurable(resourceName = "test")
public class AnotherConfigurableObject
{
   @ConfigurableAttribute(propertyName = "value.1")
   private String firstValue;

   @ConfigurableAttribute(propertyName = "value.2")
   private String secondValue;

   public String getFirstValue()
   {
      return this.firstValue;
   }

   public String getSecondValue()
   {
      return this.secondValue;
   }
}