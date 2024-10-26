package dev.ipoleksenko.pockethome.mixin;

import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import dev.ipoleksenko.pockethome.util.TeleportDataManager.TeleportData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerFallMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = player.getServerWorld();
        Identifier pocketWorldId = new Identifier("pocket", player.getEntityName().toLowerCase());

        // Checking that the player is in a custom world and has fallen below a certain height
        if (world.getRegistryKey().getValue().equals(pocketWorldId) && player.getY() < -5) {
            TeleportData data = TeleportDataManager.loadTeleportData(player);

            if (data != null) {
                // Getting the world where you want to teleport the player from saved data
                ServerWorld targetWorld = player.getServer().getWorld(World.OVERWORLD);
                if (data.getFromWorld() != null) {
                    targetWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, data.getFromWorld()));
                }

                if (targetWorld != null) {
                    // Teleporting a player to saved coordinates
                    player.teleport(targetWorld, data.getX(), data.getY(), data.getZ(), player.getYaw(), player.getPitch());
                    TeleportDataManager.savePocketCoordinates(player, 0, 325, 0);
                    player.fallDistance = 0.0F;
                }
            }
        }
    }
}
