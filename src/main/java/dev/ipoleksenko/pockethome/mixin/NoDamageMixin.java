package dev.ipoleksenko.pockethome.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class NoDamageMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        World world = player.getWorld();
        Identifier pocketWorldId = new Identifier("pocket", player.getEntityName().toLowerCase());

        // Checking that the player is in his custom world
        if (world.getRegistryKey().getValue().equals(pocketWorldId)) {
            cir.setReturnValue(false);
        }
    }
}
