/*
 *
 *                         Copyright (c) 2016-2019
 *                SparklingComet @ http://shanerx.org
 *               KillerOfPie @ http://killerofpie.github.io
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *                http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  NOTICE: All modifications made by others to the source code belong
 *  to the respective contributor. No contributor should be held liable for
 *  any damages of any kind, whether be material or moral, which were
 *  caused by their contribution(s) to the project. See the full License for more information.
 *
 */

package org.shanerx.tradeshoparm.enumys;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shanerx.tradeshop.data.config.SettingSection;
import org.shanerx.tradeshoparm.TradeShopARM;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public enum Setting {

    CONFIG_VERSION(SettingSection.NONE, "config-version", 1.0, "", "\n"),

    // Language Options
    MESSAGE_PREFIX(SettingSection.LANGUAGE_OPTIONS, "message-prefix", "&a[&eTradeShop-ARM&a] ", "The prefix the displays before all plugin messages", "\n"),

    // System Options
    ENABLE_DEBUG(SettingSection.SYSTEM_OPTIONS, "enable-debug", 0, "What debug code should be run. this will add significant amounts of spam to the console/log, generally not used unless requested by Devs (must be a whole number)"),
    CHECK_UPDATES(SettingSection.SYSTEM_OPTIONS, "check-updates", true, "Should we check for updates when the server starts"),
    ALLOW_METRICS(SettingSection.SYSTEM_OPTIONS, "allow-metrics", true, "Allow us to connect anonymous metrics so we can see how our plugin is being used to better develop it");


    private static TradeShopARM plugin = (TradeShopARM) Bukkit.getPluginManager().getPlugin("TradeShop-ARM");
    private static File file = new File(plugin.getDataFolder(), "config.yml");
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    private final String key;
    private final String path;
    private final Object defaultValue;
    private final SettingSection sectionKey;
    private String preComment = "";
    private String postComment = "";

    Setting(SettingSection sectionKey, String path, Object defaultValue) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
    }

    Setting(SettingSection sectionKey, String path, Object defaultValue, String preComment) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
        this.preComment = preComment;
    }

    Setting(SettingSection sectionKey, String path, Object defaultValue, String preComment, String postComment) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
        this.preComment = preComment;
        this.postComment = postComment;
    }
    public static Setting findSetting(String search) {
        return valueOf(search.toUpperCase().replace("-", "_"));
    }

    private static void setDefaults() {
        config = YamlConfiguration.loadConfiguration(file);

        for (Setting set : Setting.values()) {
            addSetting(set.path, set.defaultValue);
        }

        save();
    }

    private static void addSetting(String node, Object value) {
        if (config.get(node) == null) {
            config.set(node, value);
        }
    }

    private static void save() {
        Validate.notNull(file, "File cannot be null");

        if (config != null)
            try {
                Files.createParentDirs(file);

                StringBuilder data = new StringBuilder();

                data.append("##########################\n").append("#    TradeShopARM Config    #\n").append("##########################\n");
                Set<SettingSection> settingSectionKeys = Sets.newHashSet(SettingSection.values());

                for (Setting setting : values()) {
                    if (settingSectionKeys.contains(setting.sectionKey)) {
                        data.append(setting.sectionKey.getSectionHeader());
                        settingSectionKeys.remove(setting.sectionKey);
                    }

                    if (!setting.preComment.isEmpty()) {
                        data.append(setting.sectionKey.getLineLead()).append("# ").append(setting.preComment).append("\n");
                    }

                    data.append(setting.sectionKey.getLineLead()).append(setting.key).append(": ").append(new Yaml().dump(setting.getSetting()));

                    if (!setting.postComment.isEmpty()) {
                        data.append(setting.postComment).append("\n");
                    }
                }

                Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);

                try {
                    writer.write(data.toString());
                } finally {
                    writer.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void reload() {
        try {
            if (!plugin.getDataFolder().isDirectory()) {
                plugin.getDataFolder().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create Config file! Disabling plugin!", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        fixUp();

        setDefaults();
        config = YamlConfiguration.loadConfiguration(file);
    }

    // Method to fix any values that have changed with updates
    private static void fixUp() {
        boolean changes = false;

        //Add config fixes here...
        
        if (changes)
            save();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public String toPath() {
        return path;
    }

    public void setSetting(Object obj) {
        config.set(toPath(), obj);
    }

    public void clearSetting() {
        config.set(toPath(), null);
    }

    public Object getSetting() {
        return config.get(toPath());
    }

    public String getString() {
        return config.getString(toPath());
    }

    public List<String> getStringList() {
        return config.getStringList(toPath());
    }

    public int getInt() {
        return config.getInt(toPath());
    }

    public double getDouble() {
        return config.getDouble(toPath());
    }

    public boolean getBoolean() {
        return config.getBoolean(toPath());
    }
}