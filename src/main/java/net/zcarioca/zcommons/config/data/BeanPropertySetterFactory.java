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
package net.zcarioca.zcommons.config.data;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for creating instances of {@link BeanPropertySetter}.
 * 
 * @author zcarioca
 */
public class BeanPropertySetterFactory
{
   private static final Logger logger = LoggerFactory.getLogger(BeanPropertySetterFactory.class);

   /**
    * Gets a collection of {@link BeanPropertySetter} to configure the bean.
    * 
    * @param bean The bean to configure.
    * @return Returns a collection of {@link BeanPropertySetter}.
    * @throws ConfigurationException
    */
   public Collection<BeanPropertySetter> getPropertySettersForBean(Object bean) throws ConfigurationException
   {
      List<BeanPropertySetter> setters = new ArrayList<BeanPropertySetter>();
      Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();
      
      Class<?> beanClass = bean.getClass();

      try
      {
         BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
         for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors())
         {
            Method reader = desc.getReadMethod();
            Method writer = desc.getWriteMethod();
            Field field = getField(beanClass, desc);
            
            if (reader != null)
            {
               descriptors.put(desc.getDisplayName(), desc);
            }
            
            if (writer != null)
            {
               if (writer.isAnnotationPresent(ConfigurableAttribute.class))
               {
                  setters.add(new WriterBeanPropertySetter(bean, desc, field, writer.getAnnotation(ConfigurableAttribute.class)));
                  descriptors.remove(desc.getDisplayName());
               }
               if (reader != null && reader.isAnnotationPresent(ConfigurableAttribute.class))
               {
                  setters.add(new WriterBeanPropertySetter(bean, desc, field, reader.getAnnotation(ConfigurableAttribute.class)));
                  descriptors.remove(desc.getDisplayName());
               }
            }
         }
      }
      catch(Throwable t)
      {
         throw new ConfigurationException("Could not introspect bean class", t);
      }
      do
      {
         Field[] fields = beanClass.getDeclaredFields();
         for(Field field: fields)
         {
            if(field.isAnnotationPresent(ConfigurableAttribute.class))
            {
               if(descriptors.containsKey(field.getName()) && descriptors.get(field.getName()).getWriteMethod() != null)
               {
                  PropertyDescriptor desc = descriptors.get(field.getName());
                  setters.add(new WriterBeanPropertySetter(bean, desc, field, field.getAnnotation(ConfigurableAttribute.class)));
               }
               else
               {
                  setters.add(new FieldBeanPropertySetter(bean, null, field, field.getAnnotation(ConfigurableAttribute.class)));
               }
            }
            else if(descriptors.containsKey(field.getName()))
            {
               // the annotation may have been set on the getter, not the field
               PropertyDescriptor desc = descriptors.get(field.getName());
               if(desc.getReadMethod().isAnnotationPresent(ConfigurableAttribute.class)) 
               {
                  setters.add(new FieldBeanPropertySetter(bean, desc, field, desc.getReadMethod().getAnnotation(ConfigurableAttribute.class)));
               }
            }
         }
      }
      while((beanClass = beanClass.getSuperclass()) != null);
      return setters;
   }
   
   static Object getDefaultValue(Class<?> primitiveType)
   {
      if (boolean.class == primitiveType)
         return Boolean.FALSE;
      if (char.class == primitiveType)
         return '\u0000';
      if (float.class == primitiveType)
         return 0f;
      if (double.class == primitiveType)
         return 0d;
      if (byte.class == primitiveType)
         return (byte)0;
      if (short.class == primitiveType)
         return (short)0;
      if (int.class == primitiveType)
         return 0;
      return 0l;
   }
   
   private Field getField(Class<?> beanClass, PropertyDescriptor descriptor)
   {
      do
      {
         try
         {
            return beanClass.getDeclaredField(descriptor.getName());
         }
         catch (NoSuchFieldException exc)
         {
            // ignore, move on
         }
      }
      while ((beanClass = beanClass.getSuperclass()) != null);
      
      return null;
   }
   
   private static Collection<Annotation> getPropertyAnnotations(Field field, PropertyDescriptor descriptor)
   {
      Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();
      
      if (descriptor != null)
      {
         if (descriptor.getWriteMethod() != null)
         {
            addAnnotationsToMap(annotations, descriptor.getWriteMethod().getAnnotations());
         }
         if (descriptor.getReadMethod() != null)
         {
            addAnnotationsToMap(annotations, descriptor.getReadMethod().getAnnotations());
         }
      }
      if (field != null)
      {
         addAnnotationsToMap(annotations, field.getAnnotations());
      }
      return annotations.values();
   }
   
   private static Collection<Annotation> getBeanAnnotations(Class<?> beanClass)
   {
      return Arrays.asList(beanClass.getAnnotations());
   }
   
   private static void addAnnotationsToMap(Map<Class<? extends Annotation>, Annotation> annotationMap, Annotation[] annotations)
   {
      if (ArrayUtils.isNotEmpty(annotations))
      {
         for (Annotation annotation : annotations)
         {
            if (!annotationMap.containsKey(annotation.annotationType()))
            {
               annotationMap.put(annotation.annotationType(), annotation);
            }
         }
      }
   }
   
   private static abstract class AbstractBeanPropertySetter implements BeanPropertySetter
   {
      protected final Object bean;
      protected final PropertyDescriptor descriptor;
      protected final Field field;
      protected final BeanPropertyInfo beanPropertyInfo;
      protected final ConfigurableAttribute attr;
      
      public AbstractBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, ConfigurableAttribute attr)
      {
         this.bean = bean;
         this.descriptor = descriptor;
         this.field = field;
         this.attr = attr;
         
         Class<?> propertyType = field != null ? field.getType() : descriptor.getPropertyType();
         String propertyName = field != null ? field.getName() : descriptor.getName();
         
         this.beanPropertyInfo = new BeanPropertyInfoImpl(bean.getClass(), propertyType, propertyName, 
               getBeanAnnotations(bean.getClass()), getPropertyAnnotations(field, descriptor));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public BeanPropertyInfo getBeanPropertyInfo()
      {
         return this.beanPropertyInfo;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public String getPropertyKey()
      {
         String propName = attr.propertyName();
         if(StringUtils.isEmpty(propName))
         {
            propName = beanPropertyInfo.getPropertyName();
         }
         return propName;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      @SuppressWarnings("rawtypes")
      public void setProperty(Properties properties) throws ConfigurationException
      {
         String propName = getPropertyKey();
         String defaultVal = attr.defaultValue();
         
         if (logger.isTraceEnabled()) 
            logger.trace(String.format("Setting property '%s' with value '%s' for bean '%s'", propName, defaultVal, bean.toString()));

         try
         {
            BeanPropertyConverter converter = BeanPropertyConverterRegistry.getRegistry().getPropertyConverter(getRawType());
            Object beanVal = converter.convertPropertyValue(properties.getProperty(propName, defaultVal), beanPropertyInfo);
            
            if (beanVal == null)
            {
               if (!beanPropertyInfo.isArray() && beanPropertyInfo.isPrimitive())
               {
                  beanVal = getDefaultValue(beanPropertyInfo.getPropertyType());
               }
            }
            
            writeValue(beanVal);
         }
         catch(Exception exc)
         {
            throw new ConfigurationException("Could not write property to bean", exc);
         }
      }
      
      public abstract Class<?> getRawType();
      
      public abstract void writeValue(Object beanVal) throws IllegalAccessException, InvocationTargetException;
   }
   
   private static final class FieldBeanPropertySetter extends AbstractBeanPropertySetter implements BeanPropertySetter
   {
      public FieldBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, ConfigurableAttribute attr)
      {
         super(bean, descriptor, field, attr);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void writeValue(Object beanVal) throws IllegalAccessException
      {
         field.setAccessible(true);
         field.set(bean, beanVal);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Class<?> getRawType()
      {
         return field.getType();
      }
   }
   
   private static final class WriterBeanPropertySetter extends AbstractBeanPropertySetter implements BeanPropertySetter
   {
      public WriterBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, ConfigurableAttribute attr)
      {
         super(bean, descriptor, field, attr);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void writeValue(Object beanVal) throws IllegalAccessException, InvocationTargetException
      {
         descriptor.getWriteMethod().setAccessible(true);
         descriptor.getWriteMethod().invoke(bean, beanVal);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Class<?> getRawType()
      {
         return descriptor.getPropertyType();
      }
   }
   
   private static final class BeanPropertyInfoImpl implements BeanPropertyInfo
   {
      private final Class<?> beanType;
      private final Class<?> propertyType;
      private final String propertyName;
      private final Collection<Annotation> beanAnnotations;
      private final Collection<Annotation> propertyAnnotations;
      
      public BeanPropertyInfoImpl(Class<?> beanType, Class<?> propertyType, String propertyName, Collection<Annotation> beanAnnotations, Collection<Annotation> propertyAnnotations)
      {
         this.beanType = beanType;
         this.propertyType = propertyType;
         this.propertyName = propertyName;
         
         this.propertyAnnotations = Collections.unmodifiableCollection(propertyAnnotations);
         this.beanAnnotations = Collections.unmodifiableCollection(beanAnnotations);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Collection<Annotation> getBeanAnnotations()
      {
         return beanAnnotations;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Class<?> getBeanType()
      {
         return beanType;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Collection<Annotation> getPropertyAnnotations()
      {
         return propertyAnnotations;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public String getPropertyName()
      {
         return propertyName;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Class<?> getPropertyType()
      {
         return isArray() ? propertyType.getComponentType() : propertyType;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean isArray()
      {
         return propertyType.isArray();
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean isPrimitive()
      {
         return getPropertyType().isPrimitive();
      }
   }
}
