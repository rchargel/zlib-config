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
package net.zcarioca.zcommons.config.spring;

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.util.ConfigurableObject;

/**
 * A fake object that is not configurable.
 * 
 * @author zcarioca
 */
@Configurable(referenceClass=ConfigurableObject.class)
public class InvalidConfigurableObject 
{

   @ConfigurableAttribute(propertyName = "property.message")
   private int propMessage;

   
   public int getPropMessage() 
   {
      return propMessage;
   }
   
   public void setPropMessage(int propMessage) 
   {
      this.propMessage = propMessage;
   }
}
