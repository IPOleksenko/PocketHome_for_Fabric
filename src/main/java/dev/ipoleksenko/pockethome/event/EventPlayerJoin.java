package dev.ipoleksenko.pockethome.event;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import dev.ipoleksenko.pockethome.PocketHomeMod;
import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import dev.ipoleksenko.pockethome.util.TeleportDataManager.TeleportData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventPlayerJoin {
    private static String messageFirstJoin = """
            Hello, %s
            I'm a PocketHome
            ---------------------------------------------
            What would join to your pocket home
            You need to use an ender chest while sneaking
            """;

    public static void send(ServerPlayNetworkHandler handler) {
        if (handler.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0)
            handler.player.sendMessage(Text.of(String.format(messageFirstJoin, handler.player.getEntityName())));
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            TeleportData data = TeleportDataManager.loadTeleportData(player);

            if (data != null) {
                // Checking if the player was in a custom world before exiting
                RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, data.getFromWorld());

                if (PocketHomeMod.getPocketId(player).getNamespace().equals(worldKey.getValue().getNamespace())) {
                    // Load the custom world if there was one
                    ServerWorld pocketWorld = PocketHomeMod.getPocket(PocketHomeMod.getPocketId(player));

                    if (pocketWorld != null) {
                        // Moving the player to the custom world to saved coordinates
                        player.teleport(pocketWorld, data.getX(), data.getY(), data.getZ(), player.getYaw(), player.getPitch());
                        return;
                    }
                }

                // If the player was not in a custom world, then load the saved normal world
                ServerWorld targetWorld = server.getWorld(worldKey);
                if (targetWorld != null) {
                    player.teleport(targetWorld, data.getX(), data.getY(), data.getZ(), player.getYaw(), player.getPitch());
                }
            }
        });
    }
}