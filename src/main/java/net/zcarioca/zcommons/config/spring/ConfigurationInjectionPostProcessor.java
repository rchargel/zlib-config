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
package net.zcarioca.zcommons.config.spring;

import net.zcarioca.zcommons.config.Configurable;
import net.zcarioca.zcommons.config.ConfigurationProcessListener;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.HashSet;
import java.util.Set;

/**
 * This is the Spring Post Processor which implements the configuration
 * injection. It can be easily inserted into your Application Context as
 * follows:
 * <p/>
 * <pre>
 * &lt;bean class="net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor">
 *   &lt;property name="reconfigureOnUpdateEnabled" value="true"/>
 * &lt;/bean>
 * </pre>
 * <p/>
 * This Post Processor is priority ordered, so injected properties will be
 * available for use before any &#64;PostConstruct are called by the
 * CommonAnnotationBeanPostProcessor.
 *
 * @author zcarioca
 */
public class ConfigurationInjectionPostProcessor implements BeanPostProcessor, PriorityOrdered, ConfigurationProcessListener, ApplicationListener, ApplicationContextAware
{
   private static final Logger logger = LoggerFactory.getLogger(ConfigurationInjectionPostProcessor.class);

   private final Set<Object> processedBeans;
   private final ConfigurationUtilities configurationUtilities;

   public ConfigurationInjectionPostProcessor()
   {
      this.processedBeans = new HashSet<Object>();
      this.configurationUtilities = ConfigurationUtilities.getInstance();
      this.configurationUtilities.addConfigurationProcessListener(this);
   }

   /**
    * No after-initialization processing required. This method is not implemented.
    */
   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
   {
      return bean;
   }

   /**
    * Processes the bean and initializes its configuration attributes. This method
    * will allow for spring to manage the PostConstruct calls.
    *
    * @param bean     The bean to process.
    * @param beanName The name of the bean.
    * @return Returns the processed bean.
    * @see BeanPostProcessor#postProcessBeforeInitialization(Object, String)
    */
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
   {
      if (bean.getClass().isAnnotationPresent(Configurable.class)) {
         if (logger.isDebugEnabled())
            logger.debug(String.format("Processing bean %s of type %s", bean, bean.getClass()));

         synchronized (processedBeans) {
            if (!this.processedBeans.contains(bean)) {
               try {
                  this.configurationUtilities.configureBean(bean);
               } catch (ConfigurationException exc) {
                  logger.warn(String.format("Could not configure bean %s: %s", bean, exc.getMessage()));
                  if (logger.isTraceEnabled())
                     logger.trace(exc.getMessage(), exc);

                  throw new InvalidPropertyException(bean.getClass(), "unknown",
                        "Error occurred while performing bean configuration", exc);
               }
            }
         }
      }
      return bean;
   }

   /**
    * Tells the spring framework that this post-processor takes the highest precedence.
    *
    * @return Returns the order in which this post-processor should be used.
    */
   public int getOrder()
   {
      return Ordered.HIGHEST_PRECEDENCE;
   }

   /**
    * Tells the {@link ConfigurationInjectionPostProcessor} that it is not necessary
    * to process the supplied bean again.
    *
    * @param bean The bean that has been processed.
    */
   public void completedConfiguration(Object bean)
   {
      synchronized (processedBeans) {
         this.processedBeans.add(bean);
      }
   }

   public void startingConfiguration(Object bean)
   { /* not implemented */ }

   /**
    * If a {@link ContextRefreshedEvent} is received, this system will reset the list
    * of preconfigured beans.
    *
    * @param event The received event.
    */
   public void onApplicationEvent(ApplicationEvent event)
   {
      synchronized (processedBeans) {
         if (event instanceof ContextRefreshedEvent) {
            this.processedBeans.clear();
         }
      }
   }

   /**
    * Adds this class as an application listener.
    *
    * @param applicationContext The application context.
    */
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
   {
      if (applicationContext instanceof ConfigurableApplicationContext) {
         ((ConfigurableApplicationContext) applicationContext).addApplicationListener(this);
      }
   }
}
