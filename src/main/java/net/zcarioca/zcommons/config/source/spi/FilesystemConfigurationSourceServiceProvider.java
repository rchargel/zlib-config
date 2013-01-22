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

import static java.lang.String.format;
import static net.zcarioca.zcommons.config.ConfigurationConstants.FILESYSTEM_CONFIGURATION_SOURCE_SERVICE_PROVIDER;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.zcarioca.zcommons.config.DefaultEnvironment;
import net.zcarioca.zcommons.config.Environment;
import net.zcarioca.zcommons.config.exceptions.ConfigurationException;
import net.zcarioca.zcommons.config.source.ConfigurationSourceIdentifier;
import net.zcarioca.zcommons.config.source.ConfigurationSourceProvider;
import net.zcarioca.zcommons.config.util.ConfigurationUtilities;
import net.zcarioca.zcommons.config.util.PropertiesBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * A {@link ConfigurationSourceProvider} tasked with pulling configuration
 * information out of the file system.
 * 
 * @author zcarioca
 */
@ManagedResource(objectName = "Configuration:group=Configuration,name=FilesystemConfigurationSourceServiceProvider")
public class FilesystemConfigurationSourceServiceProvider extends AbstractConfigurationSourceServiceProvider
{
   private static final Logger logger = LoggerFactory.getLogger(FilesystemConfigurationSourceServiceProvider.class);

   public static final String DEFAULT_ROOT_DIR_ENV_VAR = "APP_ROOT";
   public static final String DEFAULT_CONF_DIR = "conf";

   public static final String ROOT_DIR_ENV_OVERRIDE = "config.file.rootDirEnvVar";
   public static final String ROOT_DIR_OVERRIDE = "config.file.rootDir";
   public static final String CONF_DIR_OVERRIDE = "config.file.confDir";

   private final FilesystemConfiguration filesystemConfiguration;

   private final FileWatchListener fileWatchListener;
   private FileAlterationMonitor fileAlterationMonitor;

   public FilesystemConfigurationSourceServiceProvider()
   {
      this(new DefaultEnvironment());
   }
   
   FilesystemConfigurationSourceServiceProvider(Environment environment)
   {
      this.filesystemConfiguration = new FilesystemConfiguration(environment);
      this.fileWatchListener = new FileWatchListener(ConfigurationUtilities.getInstance());
   }
   
   public FilesystemConfiguration getFilesystemConfiguration()
   {
      return this.filesystemConfiguration;
   }
   
   public FileWatchListener getFileWatchListener()
   {
      return this.fileWatchListener;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getProviderID()
   {
      return FILESYSTEM_CONFIGURATION_SOURCE_SERVICE_PROVIDER;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String getResourceName(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      String resourceName = super.getResourceName(configurationSourceIdentifier);
      if (!resourceName.endsWith(".properties") && !resourceName.endsWith(".xml"))
      {
         resourceName += ".properties";
      }

      return resourceName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void runPostProcessAction(ConfigurationSourceIdentifier configurationSourceIdentifier) 
   {
      super.runPostProcessAction(configurationSourceIdentifier);

      Class<?> referenceClass = configurationSourceIdentifier.getReferenceClass();
      String resourceName = getResourceName(configurationSourceIdentifier);

      try
      {
         File confFile = getConfigurationFile(referenceClass, resourceName);
         this.fileWatchListener.addFile(confFile, configurationSourceIdentifier);
      }
      catch (ConfigurationException exc)
      {
         logger.warn("Could not add file to watch list", exc);
      }
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsIdentifier(ConfigurationSourceIdentifier configurationSourceIdentifier)
   {
      try
      {
         File file = getConfigurationFile(configurationSourceIdentifier.getReferenceClass(), getResourceName(configurationSourceIdentifier));
         return file.exists();
      }
      catch(ConfigurationException exc)
      {
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Properties buildPropertiesFromValidInputs(Class<?> referenceClass, String resourceName, PropertiesBuilder propertiesBuilder)
         throws ConfigurationException
   {
      File file = getConfigurationFile(referenceClass, resourceName);
      InputStream in = null;
      try
      {
         in = new FileInputStream(file);
         Properties props = new Properties();
         props.load(in);
         propertiesBuilder.addAll(props);

         return propertiesBuilder.build();
      }
      catch (Throwable t)
      {
         throw new ConfigurationException(format("Could not read configuration for %s using reference class %s", resourceName, referenceClass), t);
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }

   public void postInit()
   {
      if (this.fileAlterationMonitor != null)
      {
         preDestroy();
      }
      this.fileAlterationMonitor = new FileAlterationMonitor();
      File confDir = getFilesystemConfiguration().getConfigurationDirectory();
      FileAlterationObserver observer = new FileAlterationObserver(confDir);

      observer.addListener(this.fileWatchListener);
      this.fileAlterationMonitor.addObserver(observer);

      try
      {
         this.fileAlterationMonitor.start();
      }
      catch (Exception exc)
      {
         logger.error("Could not stop file monitor", exc);
      }
   }

   public void preDestroy()
   {
      try
      {
         this.fileAlterationMonitor.stop();
      }
      catch (Exception exc)
      {
         logger.error("Could not stop file monitor", exc);
      }
   }
   
   @ManagedAttribute(description = "The set of monitored files")
   public Collection<File> getMonitoredFiles()
   {
      return getFileWatchListener().getMappedFiles();
   }
   
   @ManagedAttribute(description = "The directory containing the application's configuration files")
   public String getMonitoredConfigurationDirectory() 
   {
      try 
      {
         return getFilesystemConfiguration().getConfigurationDirectory().getAbsolutePath();
      }
      catch (IllegalArgumentException exc) 
      {
         logger.warn(format("Could not determine configuration directory: %s", exc.getMessage()), exc);
      }
      return null;
   }

   private File getConfigurationFile(Class<?> referenceClass, String resourceName) throws ConfigurationException
   {
      File file = null;
      try
      {
         File confDir = getFilesystemConfiguration().getConfigurationDirectory();
         file = getConfigurationFileInSubDirs(confDir, referenceClass, resourceName);

         if (file == null)
         {
            file = getConfigurationFileInBaseDir(confDir, resourceName);
         }
         if (file == null)
         {
            throw new ConfigurationException(format("Could not find file for %s:%s", referenceClass, resourceName));
         }
      }
      catch (IllegalArgumentException exc) 
      {
         throw new ConfigurationException(format("Could not find file for %s:%s", referenceClass, resourceName), exc);
      }
      return file;
   }

   private File getConfigurationFileInBaseDir(File confDir, String resourceName)
   {
      File file = new File(confDir, resourceName);
      if (!file.exists())
      {
         if (logger.isDebugEnabled()) logger.debug(format("Could not find file %s", file));
         return null;
      }
      return file;
   }

   private File getConfigurationFileInSubDirs(File confDir, Class<?> referenceClass, String resourceName)
   {
      String path = referenceClass.getPackage().getName().replaceAll("\\.", File.separator);

      File filePath = new File(confDir, path);
      if (!filePath.isDirectory())
      {
         if (logger.isDebugEnabled()) logger.debug(format("Could not find directory %s", filePath));
         return null;
      }

      File file = new File(filePath, resourceName);
      if (!file.exists())
      {
         if (logger.isDebugEnabled()) logger.debug(format("Could not find file %s", file));
         return null;
      }
      return file;
   }
   
   public static class FilesystemConfiguration
   {
      private final Environment environment;

      private final String confDir;
      private final String rootDirEnvironmentVar;
      private final String rootDir;
      
      FilesystemConfiguration(Environment environment)
      {
         this.environment = environment;
         this.confDir = System.getProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR);
         this.rootDirEnvironmentVar = System.getProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR);
         this.rootDir = System.getProperty(ROOT_DIR_OVERRIDE, null);
      }
      
      /**
       * Gets the current environment.
       * 
       * @return Returns the environment.
       */
      Environment getEnvironment()
      {
         return this.environment;
      }

      /**
       * Gets the root directory. Overrides the value set by the environment
       * variable.
       * 
       * @return Returns the root directory, or null.
       */
      String getRootDir()
      {
         return this.rootDir;
      }

      /**
       * Gets the root directory environment variable. Defaults to 'APP_DIR'.
       * 
       * @return Returns the root directory environment variable.
       */
      String getRootDirEnvironmentVar()
      {
         return this.rootDirEnvironmentVar;
      }

      /**
       * Gets the configuration sub-directory. Defaults to 'conf'.
       * 
       * @return Returns the configuration sub-directory.
       */
      String getConfDir()
      {
         return this.confDir;
      }

      /**
       * Gets the directory containing the configuration files.
       * 
       * @return Returns the directory containing the configuration files.
       */
      public File getConfigurationDirectory()
      {
         File rootDir = null;
         if (StringUtils.isNotBlank(getRootDir()))
         {
            rootDir = new File(getRootDir());
         }
         else if (StringUtils.isNotBlank(getRootDirEnvironmentVar()))
         {
            String rootDirEnvVar = getEnvironment().getEnvVariable(getRootDirEnvironmentVar());
            if (StringUtils.isBlank(rootDirEnvVar))
            {
               throw new IllegalArgumentException(format("There is no value for the environment variable '%s'.", getRootDirEnvironmentVar()));
            }
            rootDir = new File(rootDirEnvVar);
         }
         else
         {
            throw new IllegalArgumentException(format("There is neither an value set for the environment variable '%s', nor has a root directory been set via the system override.",
                  getRootDirEnvironmentVar()));
         }

         File confDir = rootDir;
         if (StringUtils.isNotBlank(getConfDir()))
         {
            confDir = new File(rootDir, getConfDir());
         }

         if (!confDir.exists())
         {
            throw new IllegalArgumentException(format("Cannot find the directory '%s'.", confDir));
         }
         return confDir;
      }
   }

   public static class FileWatchListener implements FileAlterationListener
   {
      private final Map<File, ConfigurationSourceIdentifier> mapper;
      private final ConfigurationUtilities configurationUtilities;
      
      FileWatchListener(ConfigurationUtilities configurationUtilities)
      {
         this.mapper = new HashMap<File, ConfigurationSourceIdentifier>();
         this.configurationUtilities = configurationUtilities;
      }

      public void addFile(File file, ConfigurationSourceIdentifier configurationSourceIdentifier)
      {
         if (file == null) throw new IllegalArgumentException("There was no file provided to the file watch listener");
         if (configurationSourceIdentifier == null) throw new IllegalArgumentException(format("The file %s was not provided a configuration source identifier", file.getAbsoluteFile()));
         
         synchronized(this.mapper) 
         {
            this.mapper.put(file, configurationSourceIdentifier);
         }
      }
      
      /**
       * Gets an immutable collection of the files in this map.
       * @return Returns an immutable collection.
       */
      @SuppressWarnings("unchecked")
      public Collection<File> getMappedFiles()
      {
         synchronized(this.mapper)
         {
            return CollectionUtils.unmodifiableCollection(this.mapper.keySet());
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onFileChange(File file)
      {
         resetProperties(file);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onFileDelete(File file)
      {
         if (this.mapper.containsKey(file))
         {
            logger.info(format("The file '%s' has been deleted", file));
            this.mapper.remove(file);
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onFileCreate(File file) { /* ignore */ }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onDirectoryChange(File file) { /* ignore */ }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onDirectoryDelete(File file) { /* ignore */ }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onDirectoryCreate(File file) { /* ignore */ }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onStart(FileAlterationObserver fileAlterationObserver) { /* ignore */ }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onStop(FileAlterationObserver fileAlterationObserver) { /* ignore */ }

      private void resetProperties(File file)
      {
         if (this.mapper.containsKey(file))
         {
            try
            {
               this.configurationUtilities.runReconfiguration(this.mapper.get(file));
            }
            catch (ConfigurationException exc)
            {
               logger.warn(format("Could not reset properties on the file %s", file), exc);
            }
         }
      }
   }
}