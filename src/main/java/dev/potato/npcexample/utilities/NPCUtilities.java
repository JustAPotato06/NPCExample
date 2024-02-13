package dev.potato.npcexample.utilities;

import dev.potato.npcexample.models.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NPCUtilities {
    private static NPCUtilities manager = new NPCUtilities();
    private List<NPC> registeredNPCs = new ArrayList<>();

    public static NPCUtilities getManager() {
        return manager;
    }

    public List<NPC> getRegisteredNPCs() {
        return registeredNPCs;
    }

    public static void showNPC(NPC npc, Player player, JavaPlugin plugin) {
        npc.show(player, plugin);
    }

    public static void showNPCToAll(NPC npc, JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            npc.show(player, plugin);
        }
    }

    public static NPC createNewNPC(Player player, String name, JavaPlugin plugin) {
        NPC npc = new NPC(player.getLocation(), ChatColor.translateAlternateColorCodes('&', name));
        npc.spawn(player);
        NPCUtilities.showNPCToAll(npc, plugin);
        NPCUtilities.getManager().getRegisteredNPCs().add(npc);
        return npc;
    }
}