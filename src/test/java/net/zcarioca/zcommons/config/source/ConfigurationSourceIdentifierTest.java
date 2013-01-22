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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;

import org.junit.Test;

/**
 * Tests the {@link ConfigurationSourceIdentifier}.
 * 
 * 
 * @author zcarioca
 */
public class ConfigurationSourceIdentifierTest extends BaseTestCase
{
   @Test
   public void testHashCode()
   {
      ConfigurationSourceIdentifier id1 = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      ConfigurationSourceIdentifier id2 = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      ConfigurationSourceIdentifier id3 = new ConfigurationSourceIdentifier(getClass(), "log4j.properties");
      
      assertTrue(id1.hashCode() == id2.hashCode());
      assertFalse(id2.hashCode() == id3.hashCode());
   }

   @Test
   public void testEqualsObject()
   {
      ConfigurationSourceIdentifier id1 = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      ConfigurationSourceIdentifier id2 = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      ConfigurationSourceIdentifier id3 = new ConfigurationSourceIdentifier(getClass(), "log4j.properties");

      assertTrue(id1.equals(id1));
      assertTrue(id1.equals(id2));
      assertTrue(id2.equals(id1));
      assertFalse(id1 == id2);
      assertFalse(id1.equals(id3));
      assertFalse(id3.equals(id1));
      assertFalse(id3.equals(null));
      assertFalse(id3.equals(ConfigurationUtilities.getInstance()));
   }

   @Test
   public void testToString()
   {
      ConfigurationSourceIdentifier id1 = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      ConfigurationSourceIdentifier id2 = new ConfigurationSourceIdentifier(getClass(), "log4j.properties");

      String one = "ConfigurationSourceIdentifier[referenceClass=class net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifierTest,resourceName=/log4j.properties]";
      String two = "ConfigurationSourceIdentifier[referenceClass=class net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifierTest,resourceName=log4j.properties]";
      
      assertEquals(one, id1.toString());
      assertEquals(two, id2.toString());
   }

   @Test
   public void testConfigurationSourceIdentifierObject()
   {
      ConfigurationSourceIdentifier id = new ConfigurationSourceIdentifier(new NoAnnotationClass());
      
      assertSame(NoAnnotationClass.class, id.getReferenceClass());
      assertEquals("noannotationclass", id.getResourceName());

      id = new ConfigurationSourceIdentifier(new SimpleAnnotationClass());
      
      assertSame(SimpleAnnotationClass.class, id.getReferenceClass());
      assertEquals("simpleannotationclass", id.getResourceName());
      
      id = new ConfigurationSourceIdentifier(new RefClassAnnotationClass());
      
      assertSame(String.class, id.getReferenceClass());
      assertEquals("string", id.getResourceName());

      id = new ConfigurationSourceIdentifier(new ResourceNameAnnotationClass());
      
      assertSame(ResourceNameAnnotationClass.class, id.getReferenceClass());
      assertEquals("/log4j", id.getResourceName());

      id = new ConfigurationSourceIdentifier(new FullAnnotationClass());
      
      assertSame(String.class, id.getReferenceClass());
      assertEquals("/log4j", id.getResourceName());
   }

   @Test
   public void testConfigurationSourceIdentifierClassOfQString() throws Exception
   {
      ConfigurationSourceIdentifier id = new ConfigurationSourceIdentifier(getClass(), "/log4j.properties");
      assertSame(getClass(), id.getReferenceClass());
      assertEquals("/log4j.properties", id.getResourceName());
   }
   
   @Test(expected = IllegalArgumentException.class) 
   public void testConfigurationSourceIdentifierClassOfQStringNullNull() 
   {
      new ConfigurationSourceIdentifier(null, null);
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testConfigurationSourceIdentifierClassOfQStringNull() 
   {
      new ConfigurationSourceIdentifier(getClass(), null);
   }
   
   private static class NoAnnotationClass {}
   
   @Configurable
   private static class SimpleAnnotationClass {}
   
   @Configurable(referenceClass = String.class)
   private static class RefClassAnnotationClass {}
   
   @Configurable(resourceName = "/log4j")
   private static class ResourceNameAnnotationClass {}
   
   @Configurable(referenceClass = String.class, resourceName = "/log4j")
   private static class FullAnnotationClass {}

}
