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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DataManager {
    private final DataType type;
    private final MessageUtil messageUtil;
    private final File jarFile;
    private final String defaultFlatDataFolderName;
    private final Reader defaultSqlDataFile;
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private final File dataDirectory;
    private Connection connection;

    public DataManager(String pluginName, DataType type, String dataFolderPath, MessageUtil messageUtil, File jarFile, String defaultDataFolderName,Reader defaultSqlDataFile, JavaPlugin plugin, FileConfiguration coreConfig) {
        this.type = type;
        this.messageUtil = messageUtil;
        this.jarFile = jarFile;
        this.defaultFlatDataFolderName = defaultDataFolderName;
        this.defaultSqlDataFile = defaultSqlDataFile;
        this.plugin = plugin;
        this.config = coreConfig;

        dataDirectory = new File(dataFolderPath + "/data/"+pluginName+"/");

       if(!dataDirectory.exists()) {
           dataDirectory.mkdirs();
       }

        if(type.equals(DataType.MYSQL)) {
            initMySQL();
        }
        if(type.equals(DataType.SQLITE)) {
            initSQLite();
        }

        createDefaultFiles();
        try {
            defaultSqlDataFile.close();
        } catch (IOException e) {}
    }

    private void initMySQL() {
        String host = config.getString("data.mysql-host");
        String user = config.getString("data.mysql-user");
        String password = config.getString("data.mysql-password");
        String database = config.getString("data.mysql-database");
        int port = config.getInt("data.mysql-port");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database, user, password);
        } catch (SQLException e) {
            messageUtil.sendErrorToConsole("Error while connecting to MySQL!");
            throw new RuntimeException(e);
        }


    }
    private void initSQLite() {
        File sqliteFile = new File(dataDirectory.getAbsolutePath() + "/data.sql");
        if(!sqliteFile.exists()) {
            try {
                sqliteFile.createNewFile();
            } catch (IOException e) {
                messageUtil.sendErrorToConsole("Error while creating SQLite file!");
                throw new RuntimeException(e);
            }
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+sqliteFile.getAbsolutePath());
        } catch (SQLException e) {
            messageUtil.sendErrorToConsole("Error while connecting to SQLite!");
            throw new RuntimeException(e);
        }
    }

    private void createDefaultFiles() {

        switch (type) {
           case FLAT:
               Set<File> files = getDefaultFiles();
               files.forEach(f -> {
                   try {
                       String fileDir = f.getAbsolutePath()
                               .replace("\\","/")
                               .split(defaultFlatDataFolderName)[1];
                       String dir = fileDir.replace(f.getName(), "");
                       File directory = new File(dataDirectory.getAbsolutePath() + "/" + dir);
                       if(!directory.exists()) {
                           directory.mkdirs();
                       }

                       File file = new File(dataDirectory.getAbsolutePath() + "/" + fileDir);
                       if(!file.exists()) {
                           file.createNewFile();

                           // Copy default file content to final file
                           InputStream inputStream = plugin.getResource(defaultFlatDataFolderName +fileDir);
                           assert inputStream != null;
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
           case SQLITE:
           case MYSQL:
               BufferedReader reader= new BufferedReader(defaultSqlDataFile);

               reader.lines().forEach(line -> {
                   try {
                       if(!connection.isClosed()) {
                           connection.prepareStatement(line).execute();
                       }
                   } catch (SQLException e) {
                       messageUtil.sendErrorToConsole("Error while preparing database!");
                       throw new RuntimeException(e);
                   }
               });
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
                if(name.startsWith(defaultFlatDataFolderName) && !name.endsWith("/")) {
                    files.add(new File(name));
                }
            }
            file.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public Object get(String localization, Object key, Object value) {
        switch(type) {
            case FLAT:

                File file = new File(dataDirectory.getAbsolutePath()  + "/" + localization + ".yml");
                if(!file.exists()) {
                    messageUtil.sendErrorToConsole("File not found! ["+localization+"]");
                    break;
                }

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                return configuration.get(key.toString() + "." + value.toString());
            case SQLITE:
            case MYSQL:

                try {
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT "+value+ " FROM "+localization.replace("/", "_")+" WHERE  id="+key
                    );

                    ResultSet resultSet = statement.executeQuery();
                    boolean hasNext = resultSet.next();

                    if(!hasNext) {
                        break;
                    }

                    Object result = resultSet.getObject(value.toString());
                    resultSet.close();

                     return result;
                } catch (SQLException e) {
                    messageUtil.sendErrorToConsole("Error while getting data from database! [localization: "+localization+" | key: "+key+" | value: " + value + "]");
                    break;
                }
        }

        return null;
    }
    public Set<Object> getKeys(String localization) {
        Set<Object> keys = new HashSet<>();

        switch (type) {
            case FLAT:
                File file = new File(dataDirectory.getAbsolutePath()  + "/" + localization + ".yml");
                if(!file.exists()) {
                    messageUtil.sendErrorToConsole("File not found! ["+localization+"]");
                    break;
                }

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                keys.addAll(configuration.getKeys(false));
                break;
            case MYSQL:
            case SQLITE:
                try {
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT id FROM "+localization.replace("/", "_")
                    );

                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        keys.add(resultSet.getObject("id"));
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    messageUtil.sendErrorToConsole("Error while getting keys from database! [localization: "+localization+"]");
                    break;
                }
                break;
        }

        return keys;
    }

    public boolean set(String localization, Object key, Object value, Object newValue) {
        switch(type){
            case FLAT:

                File file = new File(dataDirectory.getAbsolutePath()  + "/" + localization + ".yml");
                if(!file.exists()) {
                    messageUtil.sendErrorToConsole("File not found! ["+localization+"]");
                    break;
                }

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                configuration.set(key + "." + value, newValue);
                try {
                    configuration.save(file);
                    return true;
                } catch (IOException e) {
                    messageUtil.sendErrorToConsole("Error while saving file! ["+localization+"]");
                    e.printStackTrace();
                }
                break;
            case SQLITE:
            case MYSQL:

                //Check if key exists
                boolean check = getKeys(localization).contains(key);

                try {
                    if (!check) {
                        PreparedStatement addKeyStatement = connection.prepareStatement(
                                "INSERT INTO " + localization.replace("/", "_") + " (id) VALUES (" + key + ")"
                        );
                        addKeyStatement.execute();
                    }

                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE " + localization.replace("/", "_") + " SET " + value + "=" + newValue + " WHERE id=" + key
                    );
                    statement.execute();
                    return true;
                } catch(SQLException e) {
                    messageUtil.sendErrorToConsole("Error while inserting data to database! [localization: "+localization+" | key: "+key+" | value: " + value + " | new value: "+newValue+"]");
                    e.printStackTrace();
                }
                break;
        }

        return false;
    }

    public void closeConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            messageUtil.sendErrorToConsole("Error while closing connection!");
            throw new RuntimeException(e);
        }
    }
}
