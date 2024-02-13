package dev.potato.npcexample.listeners;

import dev.potato.npcexample.NPCExample;
import dev.potato.npcexample.models.NPC;
import dev.potato.npcexample.utilities.NPCUtilities;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class NPCListeners implements Listener {
    private NPCExample plugin = NPCExample.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        List<NPC> registeredNPCs = NPCUtilities.getManager().getRegisteredNPCs();
        for (NPC npc : registeredNPCs) {
            npc.show(p, plugin);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
        Location playerLocation = p.getLocation();
        List<NPC> registeredNPCs = NPCUtilities.getManager().getRegisteredNPCs();

        for (NPC npc : registeredNPCs) {
            // Check if NPC is within range
            if (p.getLocation().distance(npc.getLocation()) > 5) continue;

            // Prepare some variables >:)
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            ServerPlayer npcServerPlayer = npc.getServerPlayer();
            Location npcLocation = npc.getLocation();
            npcLocation.setDirection(playerLocation.subtract(npcLocation).toVector());
            float yaw = npcLocation.getYaw();
            float pitch = npcLocation.getPitch();
            byte yawByte = (byte) ((yaw % 360) * 256 / 360);
            byte pitchByte = (byte) ((pitch % 360) * 256 / 360);

            // Send packets
            // Rotate head packet - Horizontal head movement
            connection.send(new ClientboundRotateHeadPacket(npcServerPlayer, yawByte));
            // Move entity packet - Vertical head movement & Body Rotation
            connection.send(new ClientboundMoveEntityPacket.Rot(npcServerPlayer.getBukkitEntity().getEntityId(), yawByte, pitchByte, false));
        }
    }
}