zlib-config
===========

*Note:* See the [project documentation](http://zcarioca.net/projects/zlib-config/1.5/) for more information.

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