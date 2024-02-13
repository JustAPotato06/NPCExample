package dev.potato.npcexample;

import dev.potato.npcexample.commands.CreateNPCCommand;
import dev.potato.npcexample.listeners.NPCListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCExample extends JavaPlugin {
    private static NPCExample plugin;

    public static NPCExample getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Initialization
        plugin = this;

        // Commands
        getCommand("create").setExecutor(new CreateNPCCommand());

        // Listeners
        getServer().getPluginManager().registerEvents(new NPCListeners(), this);
    }
}