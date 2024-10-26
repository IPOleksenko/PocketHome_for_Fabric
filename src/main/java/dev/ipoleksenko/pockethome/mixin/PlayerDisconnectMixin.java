package dev.ipoleksenko.pockethome.mixin;

import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import dev.ipoleksenko.pockethome.world.PocketWorld;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerDisconnectMixin {

    private final TeleportDataManager teleportDataManager = new TeleportDataManager();

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onPlayerDisconnect(CallbackInfo info) {
        ServerPlayNetworkHandler networkHandler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = networkHandler.getPlayer(); 
        World currentWorld = player.getWorld();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (currentWorld instanceof PocketWorld) {
            teleportDataManager.savePocketCoordinates(player, x, y, z);
        } else {
            teleportDataManager.saveTeleportData(player, currentWorld, x, y, z);
        }
    }
}
