package dev.potato.npcexample.commands;

import dev.potato.npcexample.NPCExample;
import dev.potato.npcexample.models.NPC;
import dev.potato.npcexample.utilities.NPCUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateNPCCommand implements TabExecutor {
    private NPCExample plugin = NPCExample.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 1) {
                NPC npc = NPCUtilities.createNewNPC(p, "&c" + args[0], plugin);
                p.sendMessage(ChatColor.GREEN + "[NPC Example] New NPC created with the name: " + args[0]);
            } else if (args.length == 0) {
                NPC npc = NPCUtilities.createNewNPC(p, "&c" + p.getName(), plugin);
                p.sendMessage(ChatColor.GREEN + "[NPC Example] New NPC created with the name: " + p.getName());
            } else {
                p.sendMessage(ChatColor.RED + "[NPC Example] Invalid number of arguments provided! Example: /create [NPC Name]");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("[NPC Name]");
        }
        return completions;
    }
}