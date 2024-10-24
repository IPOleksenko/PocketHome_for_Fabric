package dev.ipoleksenko.pockethome.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class NoHungerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void preventHunger(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        World world = player.getWorld();
        Identifier pocketWorldId = new Identifier("pocket", player.getEntityName().toLowerCase());

        // Checking that the player is in his custom world
        if (world.getRegistryKey().getValue().equals(pocketWorldId)) {
            player.getHungerManager().setExhaustion(0.0F);
        }
    }
}
