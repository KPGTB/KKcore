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

package io.github.kpgtb.kkcore.manager.listener;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkcore.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ListenerManager {
    private final UsefulObjects usefulObjects;
    private final MessageUtil messageUtil;
    private final File jarFile;
    private final JavaPlugin plugin;

    public ListenerManager(File jarFile, JavaPlugin plugin,UsefulObjects usefulObjects) {
        this.usefulObjects = usefulObjects;
        this.messageUtil = usefulObjects.getMessageUtil();
        this.jarFile = jarFile;
        this.plugin = plugin;
    }

    public void registerListeners(String listenerPackage) {
        for(Class<?> clazz : ReflectionUtil.getAllClassesInPackage(jarFile, listenerPackage)) {
            if(Arrays.asList(clazz.getInterfaces()).contains(Listener.class)) {
                try {
                    Listener listener = (Listener) clazz.getDeclaredConstructor(UsefulObjects.class)
                            .newInstance(usefulObjects);
                    Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    messageUtil.sendErrorToConsole("Error while registering listener from class " + clazz.getName());
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
