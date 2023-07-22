package xyz.dashnetwork.skyblockores;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dashnetwork.skyblockores.listeners.BlockListener;

public class SkyblockOres extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    }
}