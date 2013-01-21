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

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;

/**
 * A fake object that is configurable.
 * 
 * @author zcarioca
 */
@Configurable
public class ConfigurableObject
{
   @ConfigurableAttribute(propertyName = "myFiles")
   private File[] files;

   @ConfigurableAttribute(defaultValue = "15")
   private int number;

   @ConfigurableAttribute(propertyName = "property.message")
   private String propMessage;

   @ConfigurableAttribute(defaultValue = "1780000")
   long longValue;

   @ConfigurableAttribute(propertyName = "another.long.value", defaultValue = "1000000")
   long anotherLongValue;

   private Double floatingPointNumber;

   @ConfigurableAttribute
   private String fieldMessage;

   @ConfigurableAttribute
   private Boolean trueFalse;

   float anotherFloat;

   private char aCharacter;

   private byte aByte;

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

   @ConfigurableAttribute
   public Double getFloatingPointNumber()
   {
      return this.floatingPointNumber;
   }

   public String getMessage()
   {
      return this.helloWorld;
   }

   public String getPropMessage()
   {
      return this.propMessage;
   }
   
   public void setAnotherLongValue(long anotherLongValue)
   {
      this.anotherLongValue = anotherLongValue;
   }

   @ConfigurableAttribute
   public void setName(String name)
   {
      this.helloWorld = "Hello " + name + "!";
   }

   public String getFieldMessage()
   {
      return this.fieldMessage;
   }

   @ConfigurableAttribute
   public char getaCharacter()
   {
      return this.aCharacter;
   }

   public void setaCharacter(char aCharacter)
   {
      this.aCharacter = aCharacter;
   }

   @ConfigurableAttribute(propertyName = "oneMoreFloat")
   public void setAnotherFloat(float anotherFloat)
   {
      this.anotherFloat = anotherFloat;
   }

   public byte getMyByte()
   {
      return this.aByte;
   }

   @ConfigurableAttribute(propertyName = "aByte", defaultValue = "1")
   public void setMyByte(byte aByte)
   {
      this.aByte = aByte;
   }

   public double getBigNum()
   {
      return this.bigNum;
   }

   public void setTrueFalse(Boolean trueFalse)
   {
      this.trueFalse = trueFalse;
   }

   public Boolean getTrueFalse()
   {
      return this.trueFalse;
   }

   @PostConstruct
   public void add()
   {
      this.bigNum = this.number + this.floatingPointNumber;
   }

   @PostConstruct
   public void wontRun(String message)
   {
      throw new RuntimeException(message);
   }
}
