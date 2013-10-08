ZLIB-CONFIG
===========

* [Purpose](#purpose)
* [Usage](#usage)
    * [Installation](#installation)
    * [Basic Configuration](#basic-configuration)
    * [Annotations](#annotations)
        * [@Configurable](#configurable)
        * [@ConfigurableAttribute](#configurableattribute)
        * [@ConfigurableDateFormat](#configurabledateformat)
        * [@ConfigurableNumberEncoding](#configurablenumberencoding)
    * [Standard Configuration Source Providers](#standard-configuration-source-providers)
        * [Default Configuration Source Provider](#default-configuration-source-provider)
        * [Filesystem Configuration Source Provider](#filesystem-configuration-source-provider)
* [Custom Providers](#custom-providers)
* [History](#history)

PURPOSE
===========

The zlib-config library is used to simplify the development of applications by removing the common boiler-plate code used to read and initialize project properties. Rather than use the standard method of initializing your classes with properties the old way:

```java
public class MyClass 
{
   private String myField;
   
   private int myInt;
   
   private long myLong;
   
   public MyClass() throws IOException
   {
      Properties props = new Properties();
      InputStream in = getClass().getResourceAsStream("proj.properties");
      props.load(in);
      in.close();

      this.myField = props.getProperty("my.field");
      try
      {
         this.myInt = Integer.parseInt(props.getProperty("my.int", "1"));
      }
      catch (java.lang.Throwable th) 
      {
         // propery was not a number
         th.printStackTrace();
         this.myInt = 1;
      }
      
      try
      {
         this.myLong = Long.parseLong(props.getProperty("my.long"));
      }
      catch (java.lang.Throwable th) 
      {
         // propery was not a number
         th.printStackTrace();
         this.myLong = 50000;
      }
   }
}
```

You can now use simple Java annotations to inform Spring that your class is configurable and which attributes require configuration.

```java
@Configurable
public class MyClass()
{
   @ConfigurableAttribute(propertyName = "my.field")
   private String myField;
   
   @ConfigurableAttribute(propertyName = "my.int", defaultValue = "1")
   private int myInt;
   
   @ConfigurableAttribute(propertyName = "my.long", defaultValue = "50000")
   private long myLong;
   
   @ConfigurableAttribute
   @ConfigurableDateFormat("yyyy-MM-dd HH:mm:ss")
   private Calendar endDate;
}
```

USAGE
=====

Installation
------------

Adding this library to your maven project is as simple as adding the following to your dependencies set:

```xml
<dependencies>
  <dependency>
    <groupId>net.zcarioca.zcommons</groupId>
    <artifactId>zlib-config</artifactId>
    <version>1.1</version>
  </dependency>
</dependencies>
```

It is also recommended that you provide either Spring 2.5 or later. This library will work with both Spring 2.5 and Spring 3.x. The following are the list of suggested dependencies to allow autowiring and automatic configuration with Spring.

```xml
...
   <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>${spring.version}</version>
   </dependency>
   <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>${spring.version}</version>
   </dependency>
   <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
   </dependency>
   <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-aspects</artifactId>
      <version>${spring.version}</version>
   </dependency>
...
```

Basic Configuration
-------------------

The first step is to initialize the ConfigurationInjectionPostProcessor in Spring's application context file. The simplest possible configuration is as follows:

```xml
<import resource="classpath:/zlib-config-default-context.xml"/>
```

The next step is to simply annotate your classes and fields. To do this there are two basic annotations, @Configurable and @ConfigurableAttribute. The first of these tells the post processor that this class has been marked for configuration. The second, @ConfigurableAttribute, marks a field for configuration and which property to inject. Additionally, this annotation can be used to define default values for the fields. Here is a simple example:

```java
package my.application;

@Configurable
public class MyConfigurableClass 
{
   @ConfigurableAttribute
   private String objectId;
   
   @ConfigurableAttribute
   private URL serverUrl;
}
```

In the above example, the configuration injector will automatically find the Properties associated to that class, and insert a String with the property name `objectId` and a URL with the property name `serverUrl`.

*NOTE:* Spring will not automatically wire your classes annotated with `@Configurable` as Spring Beans, unless Spring is expressly told to do so. For instance the following class would be wired up as a bean and pre-configured by Spring.

```java
package my.application;

@Component
@Configurable
public class MyConfigurableClass 
{
   @ConfigurableAttribute
   private String objectId;
   
   @ConfigurableAttribute
   private URL serverUrl;
}
```

Annotations
-----------

### @Configurable

The @Configurable annotation may take two parameters, a referenceClass and a resourceName. If either of these two values are not supplied, the default referenceClass is the class which was annotated, and the default reference implementation is lowercase name of the reference class. These two values are used by the ConfigurationSourceProviders to locate the configuration data.

### @ConfigurableAttribute

The @ConfigurableAttribute can also take two additional parameters, propertyName and defaultValue. The propertyName will default to the name of the property being annotated (defined by the POJO design pattern). This value of this parameter tells the configuration injector which property maps to the annotated property. This can be used if the property does not match the configured field. The defaultValue will be used only if the property's value cannot be found in the data source. This will not be used if there is a problem injecting the value into the property.

The @ConfigurableAttribute annotation can be placed on either the field, or its getter or setter. Regardless of where the annotation is placed, the configuration injector will always use the setter, if the setter is available (even if the setter is private). In this way it is possible to modify the content of the property, or take further action in relation to the property. If no setter is available the value will be injected directly into the field value.

### @ConfigurableDateFormat

The @ConfigurableDateFormat can be coupled with the @ConfigurableAttribute annotation in order provided the rules to parse properties for java.util.Date and java.util.Calendar objects. This annotation must be provided for either of these two property types, or a ConfigurationException will be thrown.

This annotation can also be placed at the top of the configurable class, if all date properties are to use the same parsing rules. If placed at the top of the class, and directly on a property, the property annotation will override the class annotation.

The date format uses the same formatting rules provided by the SimpleDateFormat.

### @ConfigurableNumberEncoding

Similar to the @ConfigurableDateFormat, the @ConfigurableNumberEncoding annotation is used as a hint to inform the parser whether the numerical value has been encoded in binary, octal, decimal, or hexidecimal. It will have no effect on floating point numbers. Unlike the date formatting annotation, this annotation is not required, and if not provided, it is assumed that the numbers are encoded as decimal digits.

Also like the date format annotation, this annotation can be placed at the class level and will be overridden by the property level annotations.

At this point, there is no plan to allow for custom base encodings.

Standard Configuration Source Providers
---------------------------------------

As stated in the previous section, there are two configuration source providers that are packaged with this library. There is a default provider, which pulls configuration data out of the classpath, and a filesystem configuration source provider, which pulls the configuration data from files on the server.

To learn about providing custom configuration source providers, for example a provider that pulls its configuration data from database tables, or from a network storage system, see the [custom provider tutorial](#custom-providers).

### Default Configuration Source Provider

The default configuration source provider loads the configuration data from a properties file located in the classpath. To understand exactly how configuration will be retrieved, let's assume the following configurable class:

```java
package com.my.application;

@Configurable
public class MyClass {
   ...
}
```
        
The @Configurable annotation will provide a reference class of code.my.application.MyClass, and a resource name of myclass. As a result, the default configuration source provider will attempt to locate the configuration file at classpath:/com/my/package/myclass.properties. If it cannot locate the file, a Configuration Exception will be thrown.

With this implementation it is also possible to provide relative paths to the configuration data. For instance the following could have been provided, @Configurable(resourceName = "/log4j"). In this case, the properties file would be located at classpath:/log4j.properties.

### Filesystem Configuration Source Provider

The filesystem configuration source provider will load the properties file out of the file system, by looking in the directory ${APP_ROOT}/conf, where APP_ROOT is defined by a configurable environment variable. Given the same class, the file-system provider will search for a file matching the resource name (case-insensitive) in the following locations:

* ${APP_ROOT}/conf/com/my/application/
* ${APP_ROOT}/conf/

It will also look for files with no extension, or a .properties and .xml file extension. The first file the provider sees that matches its criteria will be used. Again, if no file can be found, a ConfigurationException will be thrown.

Also, if both the default and filesystem configuration source providers can configure the same class, the filesystem provider will take priority. For more on priorities see the custom [configuration source provider tutorial](#custom-providers).

The following system properties can be used to override the default settings of this provider.

CUSTOM PROVIDERS
================



HISTORY
=======

This library was inspired by James Carroll's Configuration Framework for Spring, which is a proprietary library. While I have based the fundamental concept for this library on his, I have added a few features and changed some of the mechanisms used to retrieve configuration details.