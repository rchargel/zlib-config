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

import java.io.File;

import javax.annotation.PostConstruct;

import net.zcarioca.zcommons.config.ConfigurableAttribute;

/**
 * A fake object that is configurable.
 *
 * @author zcarioca
 */
public class UnconfigurableObject
{
   @ConfigurableAttribute(propertyName="myFiles")
   private File[] files;
   
   @ConfigurableAttribute(defaultValue="15")
   private int number;
   
   @ConfigurableAttribute
   private Double floatingPointNumber;
   
   private String helloWorld;
   
   private double bigNum = 0;

   public File[] getFiles()
   {
      return this.files;
   }

   public void setFiles(File[] files)
   {
      this.files = files;
   }

   public int getNumber()
   {
      return this.number;
   }

   public void setNumber(int number)
   {
      this.number = number;
   }

   public Double getFloatingPointNumber()
   {
      return this.floatingPointNumber;
   }

   public void setFloatingPointNumber(Double floatingPointNumber)
   {
      this.floatingPointNumber = floatingPointNumber;
   }

   public String getMessage()
   {
      return this.helloWorld;
   }

   @ConfigurableAttribute
   public void setName(String name)
   {
      this.helloWorld = "Hello " + name + "!";
   }
   
   public double getBigNum()
   {
      return this.bigNum;
   }
   
   @PostConstruct
   public void add()
   {
      this.bigNum = this.number + this.floatingPointNumber;
   }
}
