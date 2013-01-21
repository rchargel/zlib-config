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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link PropertiesBuilder} class.
 * 
 * @author zcarioca
 */
public class PropertiesBuilderTest {

   private PropertiesBuilder builder;
   private String environmentProperty;
   private String systemProperty;
   private String fakeEnvProperty = "fakeEnvPropertyXYZ";
   private String fakeSysProperty = "fakeSysPropertyXYZ";
   
   @Before
   public void setUp()
   {
      this.builder = new PropertiesBuilder();
      for (String envProp : System.getenv().keySet())
      {
         if (StringUtils.isNotEmpty(System.getenv(envProp)))
         {
            this.environmentProperty = envProp;
            break;
         }
      }
      for (Object sysPropObj : System.getProperties().keySet())
      {
         String sysProp = sysPropObj.toString();
         if (StringUtils.isNotEmpty(System.getProperty(sysProp)))
         {
            this.systemProperty = sysProp;
            break;
         }
      }
   }
   
   @Test
   public void testGetProperty()
   {
      assertEquals(0, builder.size());
      assertNull(builder.getProperty("test"));
      assertEquals("test", builder.addProperty("test", "test").getProperty("test"));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addProperty("property", "value"));
      assertEquals("value", builder.getProperty("property"));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddPropertyNullValue()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addProperty("property", null));
      assertEquals("", builder.getProperty("property"));
      assertEquals("", builder.getFilteredValue("property"));
      assertEquals(1, builder.size());
   }
   
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddPropertyNullPropertyName()
   {
      assertEquals(0, builder.size());
      builder.addProperty(null, "value");
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddPropertyEmptyPropertyName()
   {
      assertEquals(0, builder.size());
      builder.addProperty("", "value");
   }
   
   @Test
   public void testAddEnvironmentProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addEnvironmentProperty(environmentProperty));
      assertEquals(System.getenv(environmentProperty), builder.getProperty(environmentProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddEnvironmentPropertyFakeProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addEnvironmentProperty(fakeEnvProperty));
      assertEquals("", builder.getProperty(fakeEnvProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddEnvironmentPropertyWithDefault()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addEnvironmentProperty(environmentProperty, "defaultValue"));
      assertEquals(System.getenv(environmentProperty), builder.getProperty(environmentProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddEnvironmentPropertyFakePropertyWithDefault()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addEnvironmentProperty(fakeEnvProperty, "defaultValue"));
      assertEquals("defaultValue", builder.getProperty(fakeEnvProperty));
      assertEquals(1, builder.size());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddEnvironmentPropertyNullProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addEnvironmentProperty("", "defaultValue"));
   }
   
   @Test
   public void testAddSystemProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addSystemProperty(systemProperty));
      assertEquals(System.getProperty(systemProperty), builder.getProperty(systemProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddSystemPropertyFakeProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addSystemProperty(fakeSysProperty));
      assertEquals("", builder.getProperty(fakeSysProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddSystemPropertyWithDefault()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addSystemProperty(systemProperty, "defaultValue"));
      assertEquals(System.getProperty(systemProperty), builder.getProperty(systemProperty));
      assertEquals(1, builder.size());
   }
   
   @Test
   public void testAddSystemPropertyFakePropertyWithDefault()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addSystemProperty(fakeSysProperty, "defaultValue"));
      assertEquals("defaultValue", builder.getProperty(fakeSysProperty));
      assertEquals(1, builder.size());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddSystemPropertyNullProperty()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addSystemProperty("", "defaultValue"));
   }
   
   @Test
   public void testAddAll()
   {
      Properties props = new Properties();
      props.setProperty("p1", "v1");
      props.setProperty("p2", "v2");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(2, builder.size());
      assertEquals("v1", builder.getProperty("p1"));
      assertEquals("v2", builder.getProperty("p2"));
   }
   
   @Test
   public void testAddAllMap()
   {
      Map<String, String> props = new HashMap<String, String>();
      props.put("p1", "v1");
      props.put("p2", "v2");
      props.put("p3", null);
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      assertEquals("v1", builder.getProperty("p1"));
      assertEquals("v2", builder.getProperty("p2"));
      assertEquals("", builder.getProperty("p3"));
   }
   
   @Test
   public void testAddAllEnvironmentProperties()
   {
      Properties props = new Properties();
      props.setProperty("p1", "v1");
      props.setProperty("p2", "v2");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(this.builder, builder.addAllEnvironmentProperties());
      assertEquals(2 + System.getenv().size(), builder.size());
      assertEquals("v1", builder.getProperty("p1"));
      assertEquals("v2", builder.getProperty("p2"));
      assertEquals(System.getenv(environmentProperty), builder.getProperty(environmentProperty));
   }
   
   @Test
   public void testAddAllSystemProperties()
   {
      Properties props = new Properties();
      props.setProperty("p1", "v1");
      props.setProperty("p2", "v2");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(this.builder, builder.addAllSystemProperties());
      assertEquals(2 + System.getProperties().size(), builder.size());
      assertEquals("v1", builder.getProperty("p1"));
      assertEquals("v2", builder.getProperty("p2"));
      assertEquals(System.getProperty(systemProperty), builder.getProperty(systemProperty));
   }
   
   @Test
   public void testGetFilteredValue()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addProperty("greeting", "Hello"));
      assertEquals(this.builder, builder.addProperty("message", "${greeting} World!"));
      assertEquals(2, builder.size());
      assertEquals("${greeting} World!", builder.getProperty("message"));
      assertEquals("Hello World!", builder.getFilteredValue("message"));
      // no change to original values
      assertEquals("${greeting} World!", builder.getProperty("message"));
      assertEquals("Hello", builder.getProperty("greeting"));
      assertEquals("Hello", builder.getFilteredValue("greeting"));
   }
   
   @Test
   public void testGetFilteredValueNullValue()
   {
      assertEquals(0, builder.size());
      assertEquals("", builder.getFilteredValue("test"));
   }
   
   @Test
   public void testGetFilteredValueNested()
   {
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addProperty("greeting", "Hello"));
      assertEquals(this.builder, builder.addProperty("message", "${greeting} World!"));
      assertEquals(this.builder, builder.addProperty("test.message", "Testing the '${message}' message"));
      assertEquals(3, builder.size());
      assertEquals("${greeting} World!", builder.getProperty("message"));
      assertEquals("Hello World!", builder.getFilteredValue("message"));
      assertEquals("Testing the '${message}' message", builder.getProperty("test.message"));
      assertEquals("Testing the 'Hello World!' message", builder.getFilteredValue("test.message"));
      
      // no change to original values
      assertEquals("Testing the '${message}' message", builder.getProperty("test.message"));
      assertEquals("${greeting} World!", builder.getProperty("message"));
      assertEquals("Hello", builder.getProperty("greeting"));
      assertEquals("Hello", builder.getFilteredValue("greeting"));
   }
   
   @Test
   public void testBuild()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      Properties realValues = new Properties();
      realValues.setProperty("greeting", "Hello");
      realValues.setProperty("message", "Hello World!");
      realValues.setProperty("test.message", "Testing the 'Hello World!' message");
      
      Properties actualValues = builder.build();
      
      assertPropertiesNotEquals(props, actualValues);
      assertPropertiesEquals(realValues, actualValues);
   }
   
   @Test(expected = IllegalStateException.class)
   public void testBuildAndBuildAgain()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      builder.build();
      builder.build();
   }
   
   @Test(expected = IllegalStateException.class)
   public void testBuildAndAddProperty()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      builder.build();
      builder.addProperty("test", "test");
   }
   
   @Test(expected = IllegalStateException.class)
   public void testBuildAndAddAll()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      builder.build();
      builder.addAll(props);
   }
   
   @Test(expected = IllegalStateException.class)
   public void testBuildAndSize()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      builder.build();
      builder.size();
   }
   
   @Test(expected = IllegalStateException.class)
   public void testBuildAndGetProperty()
   {
      Properties props = new Properties();
      props.setProperty("greeting", "Hello");
      props.setProperty("message", "${greeting} World!");
      props.setProperty("test.message", "Testing the '${message}' message");
      
      assertEquals(0, builder.size());
      assertEquals(this.builder, builder.addAll(props));
      assertEquals(3, builder.size());
      
      builder.build();
      builder.getProperty("test");
   }
   
   public static void assertPropertiesNotEquals(Properties expected, Properties actual)
   {
      try
      {
         assertPropertiesEquals(expected, actual);
      } 
      catch (Throwable error)
      {
         return;
      }
      fail("Properties were equal");
   }
   
   public static void assertPropertiesEquals(Properties expected, Properties actual)
   {
      assertEquals("Properties have unequal number of rows", expected.size(), actual.size());
      for (Object key : expected.keySet())
      {
         assertTrue("Missing key: " + key.toString(), actual.containsKey(key));
         assertEquals(expected.get(key), actual.get(key));
      }
   }

}
