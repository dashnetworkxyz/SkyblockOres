package xyz.dashnetwork.skyblockores.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import xyz.dashnetwork.skyblockores.SkyblockOres;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BlockListener implements Listener {

    private Material defaultBlock = null;
    private Map<Material, Double> blockChances = new HashMap<>();

    public BlockListener(SkyblockOres plugin) {
        FileConfiguration config = plugin.getConfig();
        Logger logger = plugin.getLogger();

        if (config.contains("Default"))
            defaultBlock = Material.getMaterial(config.getString("Default"));

        if (defaultBlock == null) {
            logger.warning("Failed to read default cobblestone replacement block. Using cobblestone.");
            defaultBlock = Material.COBBLESTONE;
        }

        if (!config.isConfigurationSection("Chances")) {
            logger.warning("Failed to read custom block chances. None will appear.");
            return;
        }

        ConfigurationSection chancesSection = config.getConfigurationSection("Chances");
        Map<String, Object> sectionMap = chancesSection.getValues(true);

        for (Map.Entry<String, Object> sectionEntry : sectionMap.entrySet()) {
            Material blockType = Material.getMaterial(sectionEntry.getKey());
            double chance;

            if (blockType == null || !blockType.isBlock())
                continue;

            try {
                chance = Double.parseDouble(sectionEntry.getValue().toString());
            } catch (NumberFormatException exception) {
                logger.warning("Could not parse number '" + sectionEntry.getValue() + "' for material " + blockType.name() + ". Skipping...");
                continue;
            }

            blockChances.put(blockType, chance);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        BlockState newState = event.getNewState();

        if (event.getBlock().getType() != Material.LAVA || newState.getType() != Material.COBBLESTONE)
            return;

        newState.setType(defaultBlock);

        for (Map.Entry<Material, Double> chanceEntry : blockChances.entrySet()) {
            double random = Math.random() * 100.0D;

            if (random <= chanceEntry.getValue())
                newState.setType(chanceEntry.getKey());
        }
    }
}