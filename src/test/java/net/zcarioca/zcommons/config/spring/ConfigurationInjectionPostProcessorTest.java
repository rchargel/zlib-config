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

import static org.junit.Assert.assertEquals;
import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.util.ConfigurableObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.Ordered;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests the {@link ConfigurationInjectionPostProcessor}
 *
 *
 * @author zcarioca
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"test-applicationContext.xml"})
public class ConfigurationInjectionPostProcessorTest extends BaseTestCase implements ApplicationContextAware
{
   private ApplicationContext ctx;
   
   @Autowired
   private ConfigurationInjectionPostProcessor proc;
   
   @Autowired
   private ConfigurableObject obj;
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationContext(ApplicationContext ctx) throws BeansException
   {
      this.ctx = ctx;
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)}.
    */
   @Test
   public void testPostProcessAfterInitialization()
   {
      Double v = new Double(5);
      assertEquals(v, this.proc.postProcessAfterInitialization(v, "name"));
   }
   
   /**
    * Test method for {@link net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)}.
    */
   @Test
   public void testProstProcessBeforeInitialization()
   {
      this.proc.postProcessBeforeInitialization(this.obj, "configuredObject");
      this.proc.postProcessBeforeInitialization(this.obj, "configuredObject");
   }
   
   @Test(expected = InvalidPropertyException.class)
   public void testPostProcessBeforeInitializationInvalid()
   {
      InvalidConfigurableObject invalid = new InvalidConfigurableObject();
      this.proc.postProcessBeforeInitialization(invalid, "invalidObject");
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor#getOrder()}.
    */
   @Test
   public void testGetOrder()
   {
      assertEquals(Ordered.HIGHEST_PRECEDENCE, this.proc.getOrder());
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor#onApplicationEvent(org.springframework.context.ApplicationEvent)}.
    */
   @Test
   public void testOnApplicationEvent()
   {
      this.ctx.publishEvent(new ContextStartedEvent(this.ctx));
      this.ctx.publishEvent(new ContextRefreshedEvent(this.ctx));
   }

   /**
    * Test method for {@link net.zcarioca.zcommons.config.spring.ConfigurationInjectionPostProcessor#setApplicationContext(ApplicationContext)}.
    */
   @Test
   public void testSetApplicationContext()
   {
      this.proc.setApplicationContext(null);
      this.proc.setApplicationContext(this.ctx);
   }
   
}
