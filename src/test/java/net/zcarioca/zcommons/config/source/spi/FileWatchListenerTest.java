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
package net.zcarioca.zcommons.config.source.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Collection;

import net.zcarioca.zcommons.config.BaseTestCase;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.FileWatchListener;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link FileWatchListener}. 
 * 
 * @author zcarioca
 */
public class FileWatchListenerTest extends BaseTestCase
{
   private ConfigurationUtilitiesMock configurationUtilities;
   
   @Before
   public void setUp() throws Exception
   {
      configurationUtilities = new ConfigurationUtilitiesMock();
   }
   
   /**
    * Test method for {@link net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.FileWatchListener#addFile(java.io.File, net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier)}.
    */
   @Test
   public void testAddFile()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.addFile(new File("file1.txt"), new ConfigurationSourceIdentifier(this));
      listener.addFile(new File("file2.txt"), new ConfigurationSourceIdentifier(this));
      
      Collection<File> mappedFiles = listener.getMappedFiles();
      assertEquals(2, mappedFiles.size());
      assertTrue(mappedFiles.contains(new File("file1.txt")));
      assertTrue(mappedFiles.contains(new File("file2.txt")));
   }
   
   /**
    * Test method for {@link net.zcarioca.zcommons.config.source.spi.FilesystemConfigurationSourceServiceProvider.FileWatchListener#getMappedFiles()}.
    */
   @Test(expected = UnsupportedOperationException.class)
   public void testImmutableMappedFiles()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.addFile(new File("file1.txt"), new ConfigurationSourceIdentifier(this));
      listener.addFile(new File("file2.txt"), new ConfigurationSourceIdentifier(this));
      
      Collection<File> mappedFiles = listener.getMappedFiles();
      mappedFiles.add(new File("file3.txt"));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddFileNullFile()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.addFile(null, new ConfigurationSourceIdentifier(this));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testAddFileNullCSI()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.addFile(new File("file1.txt"), null);
   }
   
   @Test
   public void testOnFileChanged()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.onFileChange(new File("file1.txt"));
      assertFalse(configurationUtilities.isRanReconfiguration());
      
      listener.addFile(new File("file1.txt"), new ConfigurationSourceIdentifier(this));
      listener.onFileChange(new File("file1.txt"));
      assertTrue(configurationUtilities.isRanReconfiguration());
   }
   
   @Test
   public void testOnFileDeleted()
   {
      FileWatchListener listener = new FileWatchListener(configurationUtilities);
      listener.addFile(new File("file1.txt"), new ConfigurationSourceIdentifier(this));
      listener.addFile(new File("file2.txt"), new ConfigurationSourceIdentifier(this));
      assertEquals(2, listener.getMappedFiles().size());
      
      listener.onFileDelete(new File("file1.txt"));
      assertEquals(1, listener.getMappedFiles().size());
      
      listener.onFileDelete(new File("file1.txt"));
      assertEquals(1, listener.getMappedFiles().size());
   }
   
   private static class ConfigurationUtilitiesMock extends ConfigurationUtilities
   {

      private boolean ranReconfiguration = false;
      
      public ConfigurationUtilitiesMock()
      {
         super();
      }
      
      public boolean isRanReconfiguration()
      {
         return this.ranReconfiguration;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void runReconfiguration(ConfigurationSourceIdentifier sourceId) throws ConfigurationException
      {
         ranReconfiguration = true;
      }
   }
}