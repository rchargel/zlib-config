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
package net.zcarioca.zcommons.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines the number format used for integer type properties.  
 * If not specified, the format is assumed to by of type {@link NumberFormat#DECIMAL}.
 * 
 * @author zcarioca
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface ConfigurableNumberEncoding
{
   public static enum NumberFormat
   {
      BINARY(2),
      OCTAL(8),
      DECIMAL(10),
      HEXIDECIMAL(16);
      
      private int radix;
      
      private NumberFormat(int radix)
      {
         this.radix = radix;
      }
      
      public int radix()
      {
         return this.radix;
      }
   }
   
   NumberFormat value() default NumberFormat.DECIMAL;
}
