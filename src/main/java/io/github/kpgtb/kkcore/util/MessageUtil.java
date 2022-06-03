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

package io.github.kpgtb.kkcore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    private final String pluginTag;
    private final ConsoleCommandSender console;

    public MessageUtil(String pluginTag) {
        this.pluginTag = color(pluginTag);
        this.console = Bukkit.getConsoleSender();
    }

    public String color(String message) {
        String result = ChatColor.translateAlternateColorCodes('&', message);

        if(Integer.parseInt(
                Bukkit.getBukkitVersion()
                        .split("-")[0] // ex. 1.17
                        .split("\\.")[1] // ex. 17
        ) >= 16) {

            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(result);

            while(matcher.find()) {
                String color = matcher.group();
                result = result.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            }

        }

        return result;
    }

    public void sendInfoToConsole(String info) {
        console.sendMessage(color("&e------------&7[&r" + pluginTag + "&7]&e------------"));
        console.sendMessage(color("&7[&6INFO&7]&r &e" + info));
        console.sendMessage(color("&e------------&7[&r" + pluginTag + "&7]&e------------"));
    }
    public void sendErrorToConsole(String error) {
        console.sendMessage(color("&c------------&7[&r" + pluginTag + "&7]&c------------"));
        console.sendMessage(color("&7[&4ERROR&7]&r &c" + error));
        console.sendMessage(color("&c------------&7[&r" + pluginTag + "&7]&c------------"));
    }

    public String getPluginTag() {
        return pluginTag;
    }
}
