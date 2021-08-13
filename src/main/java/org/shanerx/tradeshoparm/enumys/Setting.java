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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shanerx.tradeshoparm.TradeShopARM;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public enum Setting {

    ALLOW_METRICS("allow-metrics"),
    ENABLE_DEBUG("enable-debug");

    private static TradeShopARM plugin = (TradeShopARM) Bukkit.getPluginManager().getPlugin("TradeShopARM");
    private static File file = new File(plugin.getDataFolder(), "config.yml");
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    String path;

    Setting(String path) {
        this.path = path;
    }

    public static Setting findSetting(String search) {
        return valueOf(search.toUpperCase().replace("-", "_"));
    }

    private static void setDefaults() {
        config = YamlConfiguration.loadConfiguration(file);

        addSetting(ALLOW_METRICS.path, true);
        addSetting(ENABLE_DEBUG.path, false);

        save();
    }

    private static void addSetting(String node, Object value) {
        if (config.get(node) == null) {
            config.set(node, value);
        }
    }

    private static void save() {
        if (config != null)
            try {
                config.save(file);
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

        setDefaults();
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public String toPath() {
        return path;
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
