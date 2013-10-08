ZLIB-CONFIG
===========

* [Purpose](#purpose)
* [Usage](#usage)
    * [Installation](#installation)
    * [Basic Configuration](#basic_configuration)
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

In the above example, the configuration injector will automatically find the Properties associated to that class, and insert a String with the property name ```objectId``` and a URL with the property name ```serverUrl```.

HISTORY
=======

This library was inspired by James Carroll's Configuration Framework for Spring, which is a proprietary library. While I have based the fundamental concept for this library on his, I have added a few features and changed some of the mechanisms used to retrieve configuration details.