package dev.ipoleksenko.pockethome.mixin;

import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import dev.ipoleksenko.pockethome.world.PocketWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public abstract float getYaw(); // Retrieves the entity's yaw (rotation around the Y-axis)

	@Shadow
	public abstract float getPitch(); // Retrieves the entity's pitch (rotation around the X-axis)

	// Injects custom logic at the start of the getTeleportTarget method
	@Inject(method = "getTeleportTarget", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
		// Checks if the entity is a player
		if ((Entity) (Object) this instanceof ServerPlayerEntity player) {
			// Loads the player's teleportation data
			TeleportDataManager.TeleportData data = TeleportDataManager.loadTeleportData(player);

			if (data != null && destination instanceof PocketWorld) {
				// Sets coordinates if defined in the pocketWorld data
				double targetX = data.getPocketX() != 0 ? data.getPocketX() : 0;
				double targetY = data.getPocketY() != 0 ? data.getPocketY() : 325;
				double targetZ = data.getPocketZ() != 0 ? data.getPocketZ() : 0;

				// Creates the target position for teleportation
				Vec3d targetPos = new Vec3d(targetX, targetY, targetZ);
				cir.setReturnValue(new TeleportTarget(targetPos, Vec3d.ZERO, this.getYaw(), this.getPitch()));
			}
		}
	}
}
