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

package io.github.kpgtb.kkcore.command;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkcore.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

@CommandInfo(name="KKcoreInfo", description = "This command shows info about KKcore", aliases = {"kkcinfo"})
public class KKcoreInfoCommand extends KKcommand {

    private final MessageUtil messageUtil;

    public KKcoreInfoCommand(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config) {
        super(messageUtil, languageManager, dataManager, config);

        this.messageUtil = messageUtil;
    }


    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        sender.sendMessage(
            messageUtil.color(
                "&aThis is core of \"KK\" plugins created by KPG-TB"
            )
        );
        sender.sendMessage(
                messageUtil.color(
                        "&aThis plugin is required to work other \"KK\" plugins!"
                )
        );
    }
}
