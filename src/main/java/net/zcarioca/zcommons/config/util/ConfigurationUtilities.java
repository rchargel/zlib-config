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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.ConfigurationProcessListener;
import net.zcarioca.zcommons.config.ConfigurationUpdateListener;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProviderFactory;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProviderMapper;

/**
 * This singleton works as the configuration injection engine.
 * 
 * @author zcarioca
 */
public class ConfigurationUtilities
{
   private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtilities.class);
   private static ConfigurationUtilities configUtil;
   
   private MultiValueMap beanSourceMap;
   private Set<ConfigurationProcessListener> procListeners;
   private Set<ConfigurationUpdateListener> updateListeners;
   private PropertiesBuilderFactory propertiesBuilderFactory = new PropertiesBuilderFactory();
   
   private boolean reconfigureOnUpdateEnabled;

   /**
    * Gets an instance of this singleton.
    * 
    * @return Returns an instance of the singleton.
    */
   public static ConfigurationUtilities getInstance()
   {
      if(configUtil == null)
      {
         configUtil = new ConfigurationUtilities();
      }
      return configUtil;
   }

   /**
    * Configures the supplied bean.
    * 
    * @param bean
    *           The bean to configure.
    */
   public void configureBean(Object bean) throws ConfigurationException
   {
      configureBean(bean, false);
   }

   /**
    * Configures the supplied bean.
    * 
    * @param bean
    *           The bean to configure.
    * @param invokePostConstruct
    *           Whether to invoke any &#64;PostConstruct methods.
    */
   public void configureBean(Object bean, boolean invokePostConstruct) throws ConfigurationException
   {
      if(bean == null)
      {
         logger.warn("Bean is NULL");
         throw new IllegalArgumentException("The bean is NULL");
      }
      invokeStartConfig(bean);

      configureBeanObject(bean);
      if(invokePostConstruct)
      {
         invokePostConstruct(bean);
      }

      invokeCompleteConfig(bean);
   }
   
   /**
    * Forces all of the beans that inherited their configuration from the given {@link ConfigurationSourceIdentifier} to be reconfigured.
    * Messages are all sent to any registered {@link ConfigurationUpdateListener}.
    * @param sourceId The {@link ConfigurationSourceIdentifier}.
    * 
    * @throws ConfigurationException if there is an error configuring any of the associated beans.
    */
   @SuppressWarnings("unchecked")
   public void runReconfiguration(ConfigurationSourceIdentifier sourceId) throws ConfigurationException
   {
      if (isReconfigureOnUpdateEnabled()) {
         synchronized (this.beanSourceMap) 
         {
            Collection<Object> beans = (Collection<Object>)this.beanSourceMap.remove(sourceId);
            for (Object bean : beans)
            {
               invokeStartUpdate(bean);
               configureBeanObject(bean);
               invokeCompleteUpdate(bean);
            }
         }
      }
   }
   
   /**
    * Gets the {@link PropertiesBuilderFactory} for configuring the system.
    * @return Returns the {@link PropertiesBuilderFactory} for this configuration.
    */
   public PropertiesBuilderFactory getPropertiesBuilderFactory()
   {
      return this.propertiesBuilderFactory;
   }
   
   /**
    * Sets the {@link PropertiesBuilderFactory} for the configuration.
    * @param propertiesBuilderFactory The {@link PropertiesBuilderFactory} to use.
    */
   public void setPropertiesBuilderFactory(PropertiesBuilderFactory propertiesBuilderFactory)
   {
      this.propertiesBuilderFactory = propertiesBuilderFactory;
   }

   /**
    * Loads a properties map for a class and resource location.
    * 
    * @param referenceClass
    *           The reference class.
    * @param resourceLocation
    *           The resource file to load.
    * 
    * @return Returns a properties map.
    * 
    * @throws ConfigurationException
    *            if the resource file cannot be found, read, or parsed.
    */
   public Properties loadProperties(Class<?> referenceClass, String resourceLocation) throws ConfigurationException
   {
      try
      {
         ConfigurationSourceIdentifier sourceId = new ConfigurationSourceIdentifier(referenceClass, resourceLocation);
         String providerId = ConfigurationSourceProviderMapper.getInstance().getProviderID(sourceId.getReferenceClass());
         ConfigurationSourceProvider provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider(providerId);

         return provider.getProperties(sourceId, getPropertiesBuilderFactory());
      }
      catch(Throwable t)
      {
         throw new ConfigurationException("Could not load the properties", t, referenceClass);
      }
   }

   /**
    * Sets the properties of a bean with the given properties map;
    * 
    * @param bean
    *           The bean to configure.
    * @param properties
    *           The properties to set.
    */
   public void setProperties(Object bean, Properties properties) throws ConfigurationException
   {
      Collection<PropertySetter> setters = getPropertySettersForBean(bean);

      for(PropertySetter setter: setters)
      {
         setter.setProperty(bean, properties);
      }
   }
   
   @PreDestroy
   public void invokePreDestroyAll() throws ConfigurationException
   {
       ConfigurationSourceProviderFactory.getInstance().clearProviders();
   }

   /**
    * Searches the bean for any methods annotated with &#64;PostConstruct for
    * immediate invocation.
    * 
    * @param bean
    *           The bean to process
    */
   public void invokePostConstruct(Object bean) throws ConfigurationException
   {
      Class<?> beanClass = bean.getClass();

      try
      {
         do
         {
            for(Method method: beanClass.getDeclaredMethods())
            {
               if(method.isAnnotationPresent(PostConstruct.class))
               {
                  Type[] types = method.getGenericParameterTypes();
                  if(types.length == 0)
                  {
                     // cannot be called on method that takes arguments
                     try
                     {
                        method.invoke(bean, (Object[])null);
                     }
                     catch(Exception exc)
                     {
                        throw new ConfigurationException(
                              String.format("Could not call method %s on the class %s", method.getName(), bean.getClass().getName()), exc, bean.getClass());
                     }
                  }
               }
            }
         }
         while((beanClass = beanClass.getSuperclass()) != null);
      }
      catch(SecurityException exc)
      {
         throw new ConfigurationException("A security manager has blocked access to the private fields in the class: " + bean.getClass(), exc);
      }
   }

   /**
    * Adds a {@link ConfigurationProcessListener} to the listener queue.
    * 
    * @param listener
    *           The listener to add.
    */
   public void addConfigurationProcessListener(ConfigurationProcessListener listener)
   {
      synchronized(procListeners)
      {
         procListeners.add(listener);
      }
   }

   /**
    * Adds a {@link ConfigurationUpdateListener} to the listener queue.
    * 
    * @param listener
    *           The listener to add.
    */
   public void addConfigurationUpdateListener(ConfigurationUpdateListener listener)
   {
      synchronized(updateListeners)
      {
         updateListeners.add(listener);
      }
   }

   /**
    * Removes a {@link ConfigurationProcessListener} from the listener queue.
    * 
    * @param listener
    *           The listener to remove.
    * 
    * @return Returns true if the listener was removed, false otherwise.
    */
   public boolean removeConfigurationProcessListener(ConfigurationProcessListener listener)
   {
      boolean removed = false;
      synchronized(procListeners)
      {
         removed = this.procListeners.remove(listener);
      }
      return removed;
   }

   /**
    * Removes a {@link ConfigurationUpdateListener} from the listener queue.
    * 
    * @param listener
    *           The listener to remove.
    * 
    * @return Returns true if the listener was removed, false otherwise.
    */
   public boolean removeConfigurationUpdateListener(ConfigurationUpdateListener listener)
   {
      boolean removed = false;
      synchronized(updateListeners)
      {
         removed = this.updateListeners.remove(listener);
      }
      return removed;
   }

   /**
    * Gets a collection of configuration identifiers that have already been used.
    * 
    * @return Returns a collection of {@link ConfigurationSourceIdentifier} that have already been loaded.
    */
   @SuppressWarnings("unchecked")
   public Collection<ConfigurationSourceIdentifier> getConfiguredSourceIdentifiers()
   {
      return this.beanSourceMap.keySet();
   }
   
   public void setReconfigureOnUpdateEnabled(boolean reconfigureOnUpdateEnabled)
   {
      this.reconfigureOnUpdateEnabled = reconfigureOnUpdateEnabled;
   }
   
   public boolean isReconfigureOnUpdateEnabled()
   {
      return this.reconfigureOnUpdateEnabled;
   }

   static Collection<PropertySetter> getPropertySettersForBean(Object bean) throws ConfigurationException
   {
      List<PropertySetter> setters = new LinkedList<PropertySetter>();

      Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();
      Class<?> beanClass = bean.getClass();
      BeanInfo beanInfo = null;
      try
      {
         beanInfo = Introspector.getBeanInfo(beanClass);
         for(PropertyDescriptor desc: beanInfo.getPropertyDescriptors())
         {
            String name = desc.getName();
            Method reader = desc.getReadMethod();
            Method writer = desc.getWriteMethod();

            if(writer != null)
            {
               if(getAnnotation(writer) != null)
               {
                  setters.add(new DescriptorPropertySetter(desc, getAnnotation(writer)));
               }
               else if(getAnnotation(reader) != null)
               {
                  setters.add(new DescriptorPropertySetter(desc, getAnnotation(reader)));
               }
               else
               {
                  descriptors.put(name, desc);
               }
            }
            else if(getAnnotation(reader) != null)
            {
               descriptors.put(name, desc);
            }
         }
      }
      catch(Throwable t)
      {
         throw new ConfigurationException("Could not introspect bean class", t, beanClass);
      }
      do
      {
         Field[] fields = beanClass.getDeclaredFields();
         for(Field field: fields)
         {
            if(field.isAnnotationPresent(ConfigurableAttribute.class))
            {
               if(descriptors.containsKey(field.getName()))
               {
                  PropertyDescriptor desc = descriptors.get(field.getName());
                  setters.add(new DescriptorPropertySetter(desc, field.getAnnotation(ConfigurableAttribute.class)));
               }
               else
               {
                  setters.add(new FieldPropertySetter(field, field.getAnnotation(ConfigurableAttribute.class)));
               }
            }
            else if(descriptors.containsKey(field.getName()))
            {
               // the annotation may have been set on the getter, not the field
               PropertyDescriptor desc = descriptors.get(field.getName());
               if(getAnnotation(desc.getReadMethod()) != null)
               {
                  setters.add(new FieldPropertySetter(field, getAnnotation(desc.getReadMethod())));
               }
            }
         }
      }
      while((beanClass = beanClass.getSuperclass()) != null);
      return setters;
   }

   /**
    * Converts the supplied string value and transforms it to the desired type.
    * 
    * @param type
    *           The type requested.
    * @param valueAsString
    *           The value as a string.
    * @return Returns the value as the desired type.
    */
   static Object convertValueType(Class<?> type, String valueAsString) throws ConfigurationException
   {
      if(valueAsString == null)
         return null;

      try
      {
         if(type.isArray())
         {
            Class<?> componentType = type.getComponentType();
            List<Object> list = new ArrayList<Object>();
            String[] values = valueAsString.split(",");
            for(String val: values)
            {
               list.add(convertValueType(componentType, val.trim()));
            }
            Object array = Array.newInstance(componentType, list.size());
            for(int i = 0; i < list.size(); i++)
            {
               Array.set(array, i, list.get(i));
            }
            return array;
         }
         else if(String.class == type)
         {
            return valueAsString;
         }
         else if(boolean.class == type || Boolean.class == type)
         {
            return Boolean.parseBoolean(valueAsString);
         }
         else if(long.class == type || Long.class == type)
         {
            return Long.parseLong(valueAsString);
         }
         else if(int.class == type || Integer.class == type)
         {
            return Integer.parseInt(valueAsString);
         }
         else if(short.class == type || Short.class == type)
         {
            return Short.parseShort(valueAsString);
         }
         else if(byte.class == type || Byte.class == type)
         {
            return Byte.parseByte(valueAsString);
         }
         else if(char.class == type || Character.class == type)
         {
            return valueAsString.charAt(0);
         }
         else if(float.class == type || Float.class == type)
         {
            return Float.parseFloat(valueAsString);
         }
         else if(double.class == type || Double.class == type)
         {
            return Double.parseDouble(valueAsString);
         }
         else
         {
            Constructor<?>[] constructors = type.getConstructors();
            for(Constructor<?> c: constructors)
            {
               Class<?>[] types = c.getParameterTypes();
               if(types.length == 1 && String.class.equals(types[0]))
               {
                  return c.newInstance(valueAsString);
               }
            }
            throw new IllegalArgumentException("No valid constructors for type: " + type);
         }
      }
      catch(Throwable t)
      {
         throw new ConfigurationException("Could not convert value for type: " + type, t);
      }
   }

   private void configureBeanObject(Object bean) throws ConfigurationException
   {
      ConfigurationSourceIdentifier sourceId = new ConfigurationSourceIdentifier(bean);
      String providerId = ConfigurationSourceProviderMapper.getInstance().getProviderID(sourceId.getReferenceClass());
      ConfigurationSourceProvider provider = ConfigurationSourceProviderFactory.getInstance().getConfigurationSourceProvider(providerId);

      Properties props = provider.getProperties(sourceId, getPropertiesBuilderFactory());
      setProperties(bean, props);
      beanSourceMap.put(sourceId, bean);
   }

   private void invokeStartConfig(Object bean)
   {
      if (logger.isDebugEnabled()) 
      {
         logger.debug(String.format("Configuration of bean '%s' started", bean.toString()));
      }
      synchronized(this.procListeners)
      {
         for(ConfigurationProcessListener listener: this.procListeners)
         {
            listener.startingConfiguration(bean);
         }
      }
   }

   private void invokeCompleteConfig(Object bean)
   {
      if (logger.isDebugEnabled()) 
      {
         logger.debug(String.format("Configuration of bean '%s' completed", bean.toString()));
      }
      synchronized(this.procListeners)
      {
         for(ConfigurationProcessListener listener: this.procListeners)
         {
            listener.completedConfiguration(bean);
         }
      }
   }

   private void invokeStartUpdate(Object bean)
   {
      if (logger.isDebugEnabled()) 
      {
         logger.debug(String.format("Update of bean '%s' started", bean.toString()));
      }
      synchronized(updateListeners)
      {
         for(ConfigurationUpdateListener listener: updateListeners)
         {
            listener.startingBeanUpdate(bean);
         }
      }
   }

   private void invokeCompleteUpdate(Object bean)
   {
      if (logger.isDebugEnabled()) 
      {
         logger.debug(String.format("Update of bean '%s' completed", bean.toString()));
      }
      synchronized(updateListeners)
      {
         for(ConfigurationUpdateListener listener: updateListeners)
         {
            listener.completedBeanUpdate(bean);
         }
      }
   }

   private static ConfigurableAttribute getAnnotation(Method method)
   {
      if(method != null && method.isAnnotationPresent(ConfigurableAttribute.class))
      {
         return method.getAnnotation(ConfigurableAttribute.class);
      }
      return null;
   }

   private static interface PropertySetter
   {
      public void setProperty(Object bean, Properties props) throws ConfigurationException;

      public String getPropertyName();
   }

   private static class DescriptorPropertySetter implements PropertySetter
   {
      private PropertyDescriptor desc;
      private ConfigurableAttribute attr;

      public DescriptorPropertySetter(PropertyDescriptor desc, ConfigurableAttribute attr)
      {
         this.desc = desc;
         this.attr = attr;
      }

      public String getPropertyName()
      {
         String propName = attr.propertyName();
         if(StringUtils.isEmpty(propName))
         {
            propName = desc.getName();
         }
         return propName;
      }

      public void setProperty(Object bean, Properties props) throws ConfigurationException
      {
         String propName = getPropertyName();
         String defaultVal = attr.defaultValue();
         
         if (logger.isTraceEnabled()) 
         {
            logger.trace(String.format("Setting property '%s' with value '%s' for bean '%s'", propName, defaultVal, bean.toString()));
         }

         if(StringUtils.isEmpty(defaultVal))
         {
            defaultVal = null;
         }

         try
         {
            desc.getWriteMethod().setAccessible(true);
            Object beanVal = convertValueType(desc.getPropertyType(), props.getProperty(propName, defaultVal));
            desc.getWriteMethod().invoke(bean, beanVal);
         }
         catch(Exception exc)
         {
            ConfigurationException ce = new ConfigurationException("Could not write property to bean", exc, bean.getClass());
            ce.setPropertyName(propName);
            throw ce;
         }
      }
   }

   private static class FieldPropertySetter implements PropertySetter
   {
      private Field field;
      private ConfigurableAttribute attr;

      public FieldPropertySetter(Field field, ConfigurableAttribute attr)
      {
         this.field = field;
         this.attr = attr;
      }

      public String getPropertyName()
      {
         String propName = attr.propertyName();
         if(StringUtils.isEmpty(propName))
         {
            propName = field.getName();
         }
         return propName;
      }

      public void setProperty(Object bean, Properties props) throws ConfigurationException
      {
         String propName = getPropertyName();
         String defaultVal = attr.defaultValue();
         
         if (logger.isTraceEnabled()) 
         {
            logger.trace(String.format("Setting property '%s' with value '%s' for bean '%s'", propName, defaultVal, bean.toString()));
         }

         if(StringUtils.isEmpty(defaultVal))
         {
            defaultVal = null;
         }

         try
         {
            field.setAccessible(true);
            Object beanVal = convertValueType(field.getType(), props.getProperty(propName, defaultVal));
            field.set(bean, beanVal);
         }
         catch(Exception exc)
         {
            ConfigurationException ce = new ConfigurationException("Could not write property to bean", exc, bean.getClass());
            ce.setPropertyName(propName);
            throw ce;
         }
      }
   }
   
   static void resetConfigurationUtilities()
   {
      configUtil = new ConfigurationUtilities();
   }

   protected ConfigurationUtilities()
   {
      this.procListeners = new HashSet<ConfigurationProcessListener>();
      this.updateListeners = new HashSet<ConfigurationUpdateListener>();
      this.beanSourceMap = new MultiValueMap();
   }
}
