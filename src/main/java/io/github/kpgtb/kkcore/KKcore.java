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

package io.github.kpgtb.kkcore;

import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.command.CommandManager;
import io.github.kpgtb.kkcore.manager.listener.ListenerManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KKcore extends JavaPlugin {

    private MessageUtil messageUtil;
    private LanguageManager languageManager;
    private CommandManager commandManager;
    private ListenerManager listenerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        messageUtil = new MessageUtil("&2KKcore&r");

        messageUtil.sendInfoToConsole("Loading KKcore plugin by KPG-TB.");

        languageManager = new LanguageManager(
            "KKcore",
            getConfig().getString("language"),
            getDataFolder().getAbsolutePath(),
            getResource("en.yml"),
            messageUtil
        );
        languageManager.reloadMessages();

        commandManager = new CommandManager(getFile(),"KKcore",messageUtil, languageManager);
        commandManager.registerCommands("io.github.kpgtb.kkcore.command");

        listenerManager = new ListenerManager(messageUtil,languageManager,getFile(),this);
        listenerManager.registerListeners("io.github.kpgtb.kkcore.listener");
    }

    @Override
    public void onDisable() {
        messageUtil.sendInfoToConsole("Disabling KKcore plugin by KPG-TB.");
    }
}
