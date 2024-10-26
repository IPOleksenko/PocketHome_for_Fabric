package dev.ipoleksenko.pockethome;

import dev.ipoleksenko.pockethome.event.EventPlayerJoin;
import dev.ipoleksenko.pockethome.util.TeleportDataManager;
import dev.ipoleksenko.pockethome.util.TeleportDataManager.TeleportData;
import dev.ipoleksenko.pockethome.world.PocketWorld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;
import xyz.nucleoid.fantasy.util.VoidChunkGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PocketHomeMod implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(PocketHomeMod.class);
	private static final String WORLD_NAMESPACE = "pocket";
	private static final Map<Identifier, RuntimeWorldHandle> RUNTIME_WORLD_HANDLERS = new ConcurrentHashMap<>();
	public static Fantasy FANTASY;
	private static RuntimeWorldConfig WORLD_CONFIG;

	private static RuntimeWorldConfig getWorldConfig(MinecraftServer server) {
		return new RuntimeWorldConfig().setWorldConstructor(PocketWorld::new)
				.setGenerator(new VoidChunkGenerator(server.getRegistryManager().get(RegistryKeys.BIOME)))
				.setDifficulty(Difficulty.EASY)
				.setFlat(true);
	}

	private static void handleServerStarted(MinecraftServer server) {
		FANTASY = Fantasy.get(server);
		WORLD_CONFIG = getWorldConfig(server);
	}

	private static void handleServerStopping(MinecraftServer server) {
		RUNTIME_WORLD_HANDLERS.forEach((id, handle) -> handle.unload());
	}

	@Contract("_ -> new")
	public static @NotNull Identifier getPocketId(PlayerEntity player) {
		return new Identifier(WORLD_NAMESPACE, player.getEntityName().toLowerCase());
	}

	public static ServerWorld getPocket(Identifier pocketId) {
		final RuntimeWorldHandle worldHandler = FANTASY.getOrOpenPersistentWorld(pocketId, WORLD_CONFIG);
		RUNTIME_WORLD_HANDLERS.putIfAbsent(pocketId, worldHandler);

		return worldHandler.asWorld();
	}

	public static void moveToPocket(ServerPlayerEntity player) {
		final Identifier identifier = getPocketId(player);
		final ServerWorld world = getPocket(identifier);

		// Save the player's current position and world
		TeleportDataManager.saveTeleportData(player, player.getWorld(), player.getX(), player.getY(), player.getZ());

		// Teleport the player to a custom world
		player.moveToWorld(world);
	}

	public static void returnToOverworld(ServerPlayerEntity player) {
		// Load teleportation data
		TeleportDataManager.TeleportData data = TeleportDataManager.loadTeleportData(player);

		if (data != null) {
			// Get the server and the main world (Overworld)
			MinecraftServer server = player.getServer();
			ServerWorld overworld = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, data.getFromWorld()));

			if (overworld != null) {
				double targetX = data.getX();
				double targetY = data.getY();
				double targetZ = data.getZ();

				// Save the player's current coordinates as pocketWorld before teleporting
				TeleportDataManager.savePocketCoordinates(player, player.getX(), player.getY(), player.getZ());


				// Teleport the player to the main world with specified coordinates
				player.teleport(overworld, targetX, targetY, targetZ, player.getYaw(), player.getPitch());
			}
		}
	}

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(PocketHomeMod::handleServerStarted);
		ServerLifecycleEvents.SERVER_STOPPING.register(PocketHomeMod::handleServerStopping);

		EventPlayerJoin.register();

		// TODO 9/14/23 3:01 AM @rvbsm Player will respawn at pocket cords if not teleported
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			final ServerPlayerEntity player = handler.player;
			if (player.getServerWorld() instanceof PocketWorld) {
				final BlockPos spawnPoint = player.getSpawnPointPosition();
				final float spawnAngle = player.getSpawnAngle();
				final RegistryKey<World> spawnDimension;
				final ServerWorld spawnWorld = (spawnDimension = player.getSpawnPointDimension()) != null ? server.getWorld(spawnDimension) : server.getOverworld();
				if (spawnWorld != null && spawnPoint != null) {
					final Vec3d respawnPosition = PlayerEntity.findRespawnPosition(spawnWorld, spawnPoint, spawnAngle, player.isSpawnForced(), true)
							.orElse(spawnWorld.getSpawnPos().toCenterPos());
					if (respawnPosition != null)
						player.teleport(spawnWorld, respawnPosition.x, respawnPosition.y, respawnPosition.z, spawnAngle, 0f);
				}
			}
		});

		ServerPlayConnectionEvents.INIT.register((handler, server) -> EventPlayerJoin.send(handler));

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockState blockState = world.getBlockState(hitResult.getBlockPos());
			if (!world.isClient && player.isSneaking() && blockState.isOf(Blocks.ENDER_CHEST)) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				if (serverPlayer.getWorld() instanceof PocketWorld) {
					// Teleport back to the normal world
					returnToOverworld(serverPlayer);
				} else {
					// Teleport to a custom world
					moveToPocket(serverPlayer);
				}
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});
	}
}
