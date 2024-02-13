package dev.potato.npcexample.models;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NPC {
    private Location location;
    private String name;
    private MinecraftServer minecraftServer;
    private ServerLevel serverLevel;
    private GameProfile gameProfile;
    private ServerPlayer npc;
    private SynchedEntityData npcData;

    public NPC(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public ServerPlayer getServerPlayer() {
        return npc;
    }

    public void spawn(Player playerToCopy) {
        // Create our NPC
        minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(UUID.randomUUID(), name);
        npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());
        npc.setPos(location.getX(), location.getY(), location.getZ());

        // Set the NPC's skin
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTcwNzg1NTg5NjcwMCwKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==";
        String signature = "Dgwcjk52xMPnICw2Zk4ZahtxkVOTCq8mCODk2vUI0ogFzIzoYuTQtUY+IDmlQDevKc4rymIuwiXqpGCixHaI0hXpGjRTNt8Z31EiYYQDAZUxl3WR0EosqDIBZzJF4kJSQJGFKDRGrvCvGKLj48Igs7ZDbqzvHYtrGpv64M5E/nvqvEQcxxr7d4sgiP4fL1muAKwGE0hmbBDiEH0BwG1B/ZTr1JkeQO4JLDMTl2zUycZbjB3sJS2JHM8XlQiv4/nXpGq1F13DUoCkBfeDmTPDWSn/OpNrvyWWnO7TfwJN0I2pQ0PYucno12ZYmamItPks9I2kKORTiSdBQhT2DEN3Be6cdK4IZkZdhjiNVdCf2yfGas9ayBPU7SKKCyN1wCxU8TeJao+VRx2HP19RhxjvkDt04b+iYj5JMuNc48HjKiuTTw75ni98HT7vAtB1OFbAMj+KP0z1CDqggcfgtTaMGihqKETLA57X7XadmMX3I+lTuS9n8Iw5A8dH4+P3yuqF+nzqGmhoB/Mok6GlNta/8X5RvVyztY2D6GmZ37K0lvhF3a59vBIS9kn9L6LHEqhfVsB9isZf5daQQI1mBYoLAYo2sLGII6NxGrTQk3eYgnbrTKoyRQuUS8hPHdXMSaSP2LtKYJCIxNgsmxRcR/lEzofQla9bXMj9y90FxafShSM=";
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        // Ensure the connection of the NPC is not null
        npcData = npc.getEntityData();
        npcData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        ServerPlayer serverPlayerToCopy = ((CraftPlayer) playerToCopy).getHandle();
        setConnection(npc, "c", serverPlayerToCopy.connection);
    }

    private void setConnection(Object npc, String fieldName, Object connection) {
        try {
            Field field = npc.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(npc, connection);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error establishing a non-null connection to the NPC!");
            exception.printStackTrace();
        }
    }

    public void show(Player player, JavaPlugin plugin) {
        // Show the entity to the player using packets
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        ServerGamePacketListenerImpl connection = sp.connection;
        connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
        connection.send(new ClientboundAddEntityPacket(npc));
        connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npcData.getNonDefaultValues()));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                connection.send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(npc.getUUID())));
            }
        }, 40);
        // Adding equipment to the NPC
        connection.send(new ClientboundSetEquipmentPacket(npc.getBukkitEntity().getEntityId(), List.of(
                new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_AXE))),
                new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.GOLDEN_HELMET))),
                new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_PICKAXE))))));
    }
}