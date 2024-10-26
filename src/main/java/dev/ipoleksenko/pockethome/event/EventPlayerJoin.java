package dev.ipoleksenko.pockethome.event;

import net.minecraft.entity.Entity;
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
    private static final String messageFirstJoin = """
            Hello, %s
            I'm a PocketHome
            ---------------------------------------------
            To join your pocket home,
            use an ender chest while sneaking.
            """;

    public static void send(ServerPlayNetworkHandler handler) {
        ServerPlayerEntity player = handler.getPlayer();
        if (handler.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
            TeleportDataManager.savePocketCoordinates(player, player.getX(), player.getY(), player.getZ());
            TeleportDataManager.savePocketCoordinates(player, 0, 325, 0);
            handler.player.sendMessage(Text.of(String.format(messageFirstJoin, handler.player.getEntityName())));
        }
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            TeleportData data = TeleportDataManager.loadTeleportData(player);

            if (data != null) {
                // Get the world where the player was previously
                RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, data.getFromWorld());

                // Check if the player was in a custom world
                if (PocketHomeMod.getPocketId(player).getNamespace().equals(worldKey.getValue().getNamespace())) {
                    ServerWorld pocketWorld = PocketHomeMod.getPocket(PocketHomeMod.getPocketId(player));

                    if (pocketWorld != null) {
                        // Teleport to the custom world
                        player.teleport(pocketWorld, data.getPocketX(), data.getPocketY(), data.getPocketZ(), player.getYaw(), player.getPitch());
                        return;
                    }
                }

                // If the player was not in a custom world, load the saved overworld
                ServerWorld targetWorld = server.getWorld(worldKey);
                if (targetWorld != null) {
                    double targetX = data.getX();
                    double targetY = data.getY();
                    double targetZ = data.getZ();

                    // Teleport the player to the saved coordinates in the overworld
                    player.teleport(targetWorld, targetX, targetY, targetZ, player.getYaw(), player.getPitch());
                }
            }
        });
    }
}
