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

package io.github.kpgtb.kkcore.listener;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class AntiReloadListener implements Listener {
    private final MessageUtil messageUtil;
    private final LanguageManager languageManager;

    public AntiReloadListener(UsefulObjects usefulObjects) {
        this.messageUtil = usefulObjects.getMessageUtil();
        this.languageManager = usefulObjects.getLanguageManager();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        List<String> reloadCommands = new ArrayList<>();

        reloadCommands.add("/rl");
        reloadCommands.add("/reload");

        for(String cmd : reloadCommands) {
            if(command.startsWith(cmd)) {
                player.sendTitle(messageUtil.getPluginTag(), languageManager.getMessage("noReload"));
                break;
            }
        }

    }
}
