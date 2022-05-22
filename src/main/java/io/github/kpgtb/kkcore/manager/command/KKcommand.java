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

package io.github.kpgtb.kkcore.manager.command;

import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class KKcommand extends Command {
    private final CommandInfo commandInfo;
    private final MessageUtil messageUtil;
    private final LanguageManager languageManager;

    public KKcommand(MessageUtil messageUtil, LanguageManager languageManager) {
        super("");

        this.messageUtil = messageUtil;
        this.languageManager = languageManager;

        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        if(commandInfo == null) {
            messageUtil.sendErrorToConsole("Commands must have CommandInfo!");
            Bukkit.shutdown();
        }
        setName(commandInfo.name());
        setDescription(commandInfo.description());
        setAliases(Arrays.asList(commandInfo.aliases()));

    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!commandInfo.permission().equalsIgnoreCase("")) {
            if(!sender.hasPermission(commandInfo.permission())) {
                sender.sendMessage(languageManager.getMessage("noPermission", new HashMap<>(){{
                    put("PERMISSION", commandInfo.permission());
                }}));
                return false;
            }
        }

        if(commandInfo.requiredArgs()) {
            if(args.length < commandInfo.argsCount()) {
                sender.sendMessage(languageManager.getMessage("wrongUsage", new HashMap<>(){{
                    put("USAGE", commandInfo.usage());
                }}));
                return false;
            }
        }

        if(commandInfo.requiredPlayer()) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(languageManager.getMessage("onlyPlayer"));
                return false;
            }
            executeCommand((Player) sender, args);
            return true;
        }

        executeCommand(sender, args);
        return true;
    }

    public void executeCommand(CommandSender sender, String[] args) {}
    public void executeCommand(Player player, String[] args) {}

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }
    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

}
