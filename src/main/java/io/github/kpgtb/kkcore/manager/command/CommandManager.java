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

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkcore.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, CommandInfo> commands = new HashMap<>();

    private final UsefulObjects usefulObjects;
    private final MessageUtil messageUtil;
    private final String pluginTag;
    private final File jarFile;

    public CommandManager(File jarfile, String pluginTag, UsefulObjects usefulObjects) {
        this.usefulObjects = usefulObjects;
        this.messageUtil = usefulObjects.getMessageUtil();
        this.jarFile = jarfile;
        this.pluginTag = pluginTag;
    }

    public void registerCommands(String commandPackage) {
        for(Class<?> clazz : ReflectionUtil.getAllClassesInPackage(jarFile, commandPackage, KKcommand.class)) {
            try {
                KKcommand command = (KKcommand) clazz.getDeclaredConstructor(UsefulObjects.class)
                        .newInstance(usefulObjects);

                // Register command
                Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
                commandMap.register(pluginTag, command);

                commands.put(command.getCommandInfo().name(), command.getCommandInfo());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | NoSuchFieldException e) {
                messageUtil.sendErrorToConsole("Error while registering command from class " + clazz.getName());
                throw new RuntimeException(e);
            }
        }
    }

    public HashMap<String, CommandInfo> getCommands() {
        return commands;
    }
}
