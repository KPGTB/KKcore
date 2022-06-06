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

import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class UsefulObjects {
    MessageUtil messageUtil;
    LanguageManager languageManager;
    DataManager dataManager;
    FileConfiguration config;

    public UsefulObjects(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config) {
        this.messageUtil = messageUtil;
        this.languageManager = languageManager;
        this.dataManager = dataManager;
        this.config = config;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
