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
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager {

    private final String pluginName;
    private final String language;
    private final String dataFolderPath;
    private final MessageUtil messageUtil;
    private final InputStream defaultMessagesFile;

    private final HashMap<String, String> messages = new HashMap<>();

    public LanguageManager(String pluginName, String language, String dataFolderPath, InputStream defaultMessagesFile, MessageUtil messageUtil) {
        this.pluginName = pluginName;
        this.language = language;
        this.dataFolderPath = dataFolderPath;
        this.messageUtil = messageUtil;
        this.defaultMessagesFile = defaultMessagesFile;
    }

    public void reloadMessages() {
        File languageDir = new File(dataFolderPath + "/languages/"+pluginName+"/");

        if(!languageDir.exists()) {
            languageDir.mkdirs();
        }

        // Create file with messages if not exists
        File languageFile = new File(dataFolderPath + "/languages/"+pluginName+"/"+language+".yml");

        if(!languageFile.exists()) {
            try {
                languageFile.createNewFile();

                // Setup messages
                byte[] buffer = new byte[defaultMessagesFile.available()];
                defaultMessagesFile.read(buffer);
                Files.write(buffer, languageFile);

                messageUtil.sendInfoToConsole("Created new language file! [/"+pluginName+"/"+language+".yml]");
            } catch (IOException e) {
                messageUtil.sendErrorToConsole("Error while creating language file. ["+language+"]");
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(languageFile);

        // Load messages

        for(String messageCode : yaml.getKeys(false)) {

            Object message = yaml.get(messageCode);

            if(message instanceof String) {
                messages.put(messageCode, messageUtil.color((String) message));
                continue;
            }

            if(message instanceof List<?>) {
                StringBuilder finalMessage = new StringBuilder();

                for (String m : (List<String>) message) {
                    if(finalMessage.toString().equalsIgnoreCase("")) {
                        finalMessage = new StringBuilder(m);
                        continue;
                    }
                    finalMessage.append("\n").append(m);
                }

                messages.put(messageCode, messageUtil.color(finalMessage.toString()));
            }

        }
    }

    public String getMessage(String messageCode) {
        return messages.containsKey(messageCode) ? messages.get(messageCode) : messageUtil.color("&cTranslate not found! ["+language+"] "+messageCode);
    }
    public String getMessage(String messageCode, Map<String, String> replaces) {
        if(!messages.containsKey(messageCode)) {
            return messageUtil.color("&cTranslate not found! ["+language+"] "+messageCode);
        }

        String finalMessage = messages.get(messageCode);

        for(String toReplace : replaces.keySet()) {
            finalMessage = finalMessage.replace("{"+toReplace+"}", replaces.get(toReplace));
        }

        return finalMessage;
    }
    public String getMessage(String messageCode, Player player, Map<String, String> replaces) {
        if(!messages.containsKey(messageCode)) {
            return messageUtil.color("&cTranslate not found! ["+language+"] "+messageCode);
        }

        String finalMessage = messages.get(messageCode);

        for(String toReplace : replaces.keySet()) {
            finalMessage = finalMessage.replace("{"+toReplace+"}", replaces.get(toReplace));
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            finalMessage = PlaceholderAPI.setPlaceholders(player, finalMessage);
        }

        return finalMessage;
    }
}
