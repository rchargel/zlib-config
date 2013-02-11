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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.util.AnotherConfigurableObject;
import net.zcarioca.zcommons.config.util.ConfigurableObject;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;
import net.zcarioca.zcommons.config.util.MockConfigurableObject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is an end-to-end test.
 * 
 * @author zcarioca
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/net/zcarioca/zcommons/config/spring/test-applicationContext.xml" })
public class SpringEndToEndTest extends BaseTestCase
{
   @Autowired
   ConfigurationUtilities configurationUtilities;

   @Autowired(required = false)
   ConfigurableObject configurableObject;

   @Autowired(required = false)
   MockConfigurableObject mockConfigurableObject;

   @Autowired(required = false)
   AnotherConfigurableObject anotherConfigurableObject;

   @BeforeClass
   public static void setupFilesystem()
   {
      setupMockEnvironment();
      createConfDir();
      Properties props = new Properties();
      props.put("value.1", "This is the first value");
      props.put("value.2", "This is the second value");

      try
      {
         OutputStream out = new FileOutputStream(new File(getConfDir(), "test.properties"));

         props.store(out, "this is a comment");
      }
      catch (IOException exc)
      {
         // do nothing
      }
   }

   @Test
   public void testAutowiring()
   {
      assertNotNull("ConfigurableObject was not injected", configurableObject);
      assertNotNull("MockConfigurableObject was not injected", mockConfigurableObject);
      // calling something configurable does not mean it is automatically
      // injectable.
      assertNull("AnotherConfigurableObject was injected, even when not a spring bean", anotherConfigurableObject);
   }

   @Test
   public void testConfigurationOfConfigurableObject()
   {
      /*
       * This data is stored in MockConfSourceProvider
       * 
       * myFiles=/tmp/file.txt, /tmp/text.txt number=23
       * floatingPointNumber=123.56 trueFalse=false name=Z-Carioca
       * oneMoreFloat=1.34 aByte=120 fieldMessage=This is a simple message
       * aCharacter=s property.message=This is a simple property message
       * another.long.value=500
       */
      assertEquals(2, mockConfigurableObject.getFiles().length);
      assertEquals(23, mockConfigurableObject.getNumber());
      assertEquals(123.56, mockConfigurableObject.getFloatingPointNumber(), 0);
      assertFalse(mockConfigurableObject.getTrueFalse());
      assertEquals("Hello Z-Carioca!", mockConfigurableObject.getMessage());
      assertEquals((byte) 120, mockConfigurableObject.getMyByte());
      assertEquals('s', mockConfigurableObject.getaCharacter());
      assertEquals(146.56, mockConfigurableObject.getBigNum(), 0);
      assertEquals("This is a simple property message", mockConfigurableObject.getPropMessage());
   }

   @Test
   public void testMockConfigurationOfConfigurableObject()
   {
      /*
       * This data is stored in the classpath.
       * 
       * myFiles=/tmp/file.txt,/tmp/text.txt number=22
       * floatingPointNumber=123.56 trueFalse=true name=Z Carioca
       * oneMoreFloat=0.34 aByte=120 fieldMessage=This is a simple message
       * aCharacter=S property.message=There is a field which states:
       * ${fieldMessage} - ${oneMoreFloat} ${along} another.long.value=500
       */
      assertEquals(2, configurableObject.getFiles().length);
      assertEquals(22, configurableObject.getNumber());
      assertEquals(123.56, configurableObject.getFloatingPointNumber(), 0);
      assertTrue(configurableObject.getTrueFalse());
      assertEquals("Hello Z Carioca!", configurableObject.getMessage());
      assertEquals((byte) 120, configurableObject.getMyByte());
      assertEquals('S', configurableObject.getaCharacter());
      assertEquals(145.56, configurableObject.getBigNum(), 0);
      assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", configurableObject.getPropMessage());
   }

   @Test
   public void testAnotherConfigurationOfConfigurableObject() throws ConfigurationException
   {
      /*
       * There are two potential sources of this information.
       * 
       * There is a file in the classpath test.properties:
       * 
       * value.1=Value One value.2=Value Two
       * 
       * There is also a file on the file system test.properties:
       * 
       * value.1=This is the first value value.2=This is the second value
       * 
       * The file system values should be used instead of the classpath values.
       */
      anotherConfigurableObject = new AnotherConfigurableObject();
      configurationUtilities.configureBean(anotherConfigurableObject);

      assertEquals("This is the first value", anotherConfigurableObject.getFirstValue());
      assertEquals("This is the second value", anotherConfigurableObject.getSecondValue());
   }

}
