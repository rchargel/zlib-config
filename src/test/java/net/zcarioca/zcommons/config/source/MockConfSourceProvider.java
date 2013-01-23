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

import java.util.Properties;

import net.zcarioca.zcommons.config.util.MockConfigurableObject;
import net.zcarioca.zcommons.config.util.PropertiesBuilderFactory;

/**
 * A mock configuration source provider.
 * 
 * 
 * @author zcarioca
 */
public class MockConfSourceProvider implements ConfigurationSourceProvider
{
   public static final String ID = "MOCK-PROVIDER";
   
   public Properties getProperties(ConfigurationSourceIdentifier configurationSourceIdentifier, PropertiesBuilderFactory propertiesBuilderFactory)
   {
      Properties props = new Properties();
      props.setProperty("myFiles", "/tmp/file.txt,/tmp/text.txt");
      props.setProperty("number", "22");
      props.setProperty("floatingPointNumber", "123.56");
      props.setProperty("trueFalse", "true");
      props.setProperty("name", "Z Carioca");
      props.setProperty("oneMoreFloat", "0.34");
      props.setProperty("aByte", "120");
      props.setProperty("fieldMessage", "This is a simple message");
      props.setProperty("aCharacter", "S");
      props.setProperty("property.message", "This is a simple property message");
      props.setProperty("another.long.value", "500");
      
      return props;
   }

   public String getProviderID()
   {
      return ID;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public Priority getPriorityLevel()
   {
      return Priority.HIGH;
   }
   
   public boolean supportsIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      return configurationSourceIdentifier.equals(new ConfigurationSourceIdentifier(new MockConfigurableObject()));
   }
   
   public void postInit() { }
   
   public void preDestroy() { }
}
