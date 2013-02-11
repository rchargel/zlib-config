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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurableAttribute;
import net.zcarioca.zcommons.config.ConfigurableDateFormat;
import net.zcarioca.zcommons.config.ConfigurableNumberEncoding;
import net.zcarioca.zcommons.config.ConfigurableNumberEncoding.NumberFormat;

import org.junit.Before;

/**
 * A base class for converter tests.
 * 
 * @author zcarioca
 */
public abstract class BaseConverterTestCase
{
   BeanPropertyInfo beanPropertyInfo;

   @Before
   public void init() throws Exception
   {
      beanPropertyInfo = mock(BeanPropertyInfo.class);
      setBeanAnnotations(mock(Configurable.class));
      setPropertyAnnotations(mock(ConfigurableAttribute.class));
      setupBeanPropertyInfo();
   }

   void setupBeanPropertyInfo()
   {
      // override
   }

   void setBeanAnnotations(Annotation... annotations)
   {
      when(beanPropertyInfo.getBeanAnnotations()).thenReturn(toCollection(annotations));
   }

   void setPropertyAnnotations(Annotation... annotations)
   {
      when(beanPropertyInfo.getPropertyAnnotations()).thenReturn(toCollection(annotations));
   }

   Collection<Annotation> toCollection(Annotation... annotations)
   {
      return Arrays.asList(annotations);
   }

   <T extends Annotation> T mockAnnotation(Class<T> annotationType)
   {
      return mock(annotationType);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   void setPropertyType(Class type)
   {
      when(beanPropertyInfo.getPropertyType()).thenReturn(type);
   }

   ConfigurableNumberEncoding mockNumberFormatAnnotation(NumberFormat numberFormat)
   {
      ConfigurableNumberEncoding configurableNumberEncoding = mockAnnotation(ConfigurableNumberEncoding.class);

      when(configurableNumberEncoding.value()).thenReturn(numberFormat != null ? numberFormat : NumberFormat.DECIMAL);

      return configurableNumberEncoding;
   }

   ConfigurableDateFormat mockConfigurableDateFormat(String format)
   {
      ConfigurableDateFormat configurableDateFormat = mockAnnotation(ConfigurableDateFormat.class);

      when(configurableDateFormat.value()).thenReturn(format);

      return configurableDateFormat;
   }

}
