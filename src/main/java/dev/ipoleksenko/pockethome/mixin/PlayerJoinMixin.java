package dev.ipoleksenko.pockethome.mixin;


import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.ipoleksenko.pockethome.PocketHomeMod.*;

@Mixin(ServerPlayerEntity.class)
class PlayerJoinMixin {

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void onPlayerJoin(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        FANTASY.getOrOpenPersistentWorld(getPocketId(player), getWorldConfig(player.getServer()));
        returnToOverworld(player);

    }
}
