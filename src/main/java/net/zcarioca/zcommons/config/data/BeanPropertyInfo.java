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

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Passed to a property converter to set a property on an object.
 * 
 * @author zcarioca
 */
public interface BeanPropertyInfo
{
   /**
    * Gets the property name.
    * 
    * @return Returns the property name.
    */
   public String getPropertyName();
   
   /**
    * Gets the type of the bean.
    * 
    * @return Returns the bean's class.
    */
   public Class<?> getBeanType();

   /**
    * Gets the property type, or the component type if it is an array.
    * 
    * @return Returns the property type.
    */
   public Class<?> getPropertyType();
   
   /**
    * Returns true if the property is a primitive, or an array of primitives.
    * 
    * @return Returns true if the property is a primitive or an array of primitives.
    */
   public boolean isPrimitive();
   
   /**
    * Returns true if the property is an array.
    * 
    * @return Returns true if the property is an array.
    */
   public boolean isArray();

   /**
    * Gets an immutable collection of annotations applied to this property.
    * These annotations may be on the field, the getter or the setter method of
    * the property.
    * 
    * @return Returns the property annotations.
    */
   public Collection<Annotation> getPropertyAnnotations();

   /**
    * Gets an immutable collection of the bean annotations.
    * 
    * @return Returns the annotations on the class of the bean, or any of its
    *         super classes.
    */
   public Collection<Annotation> getBeanAnnotations();
}
