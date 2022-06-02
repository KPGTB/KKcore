/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkcore.manager;

import com.google.common.io.Files;
import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DataManager {
    private final String pluginName;
    private final DataType type;
    private final String dataFolderPath;
    private final MessageUtil messageUtil;
    private final File jarFile;
    private final String defaultDataFolderName;
    private final File dataDirectory;
    private final JavaPlugin plugin;

    public DataManager(String pluginName, DataType type, String dataFolderPath, MessageUtil messageUtil, File jarFile, String defaultDataFolderName, JavaPlugin plugin) {
        this.pluginName = pluginName;
        this.type = type;
        this.dataFolderPath = dataFolderPath;
        this.messageUtil = messageUtil;
        this.jarFile = jarFile;
        this.defaultDataFolderName = defaultDataFolderName;
        this.plugin = plugin;

        dataDirectory = new File(dataFolderPath + "/data/"+pluginName+"/");

        if(!dataDirectory.exists()) {
            createDefaultFiles();
        }
    }

    private void createDefaultFiles() {
        dataDirectory.mkdirs();

        Set<File> files = getDefaultFiles();

        switch (type) {
           case FLAT:
               files.forEach(f -> {
                   try {
                       if(f.getAbsolutePath().contains("flat")) {
                           String fileDir = f.getAbsolutePath()
                                   .split(defaultDataFolderName)[1]
                                   .replace("\\", "/")
                                   .replace("/flat/", "");
                           String dir = fileDir.replace(f.getName(), "");

                           File directory = new File(dataDirectory.getAbsolutePath() + "/" + dir);
                           if(!directory.exists()) {
                               directory.mkdirs();
                           }

                           File file = new File(dataDirectory.getAbsolutePath() + "/" + fileDir);
                           if(!file.exists()) {
                               file.createNewFile();
                           }

                           // Copy default file content to final file
                           InputStream inputStream = plugin.getResource(defaultDataFolderName + "/flat/" +fileDir);
                           byte[] fileContent = new byte[inputStream.available()];
                           inputStream.read(fileContent);
                           Files.write(fileContent, file);
                       }


                   } catch (Exception e) {
                       messageUtil.sendErrorToConsole("Error while creating file! [" + f.getName() + "]");
                       throw new RuntimeException(e);
                   }
               });
               break;
           case MYSQL:
               break;
           case SQLITE:
               break;
       }
    }

    private Set<File> getDefaultFiles() {
        Set<File> files = new HashSet<>();
        try {
            JarFile file = new JarFile(jarFile);
            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName();
                if(name.startsWith(defaultDataFolderName) && !name.endsWith("/")) {
                    files.add(new File(name));
                }
            }
            file.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
