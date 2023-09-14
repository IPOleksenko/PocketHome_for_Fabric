package dev.ipoleksenko.pockethome.world;

import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class PocketWorld extends RuntimeWorld {

	public static final BlockPos POCKET_SPAWN_POS = new BlockPos(0, 1, 0);
	private static final int POCKET_PLATFORM_RADIUS = 2;

	public PocketWorld(MinecraftServer server, RegistryKey<World> registryKey, RuntimeWorldConfig config, Style style) {
		super(server, registryKey, config, style);
	}

	private static void generateSpawnPlatform(ServerWorld world) {
		final BlockPos start = POCKET_SPAWN_POS.down().north(POCKET_PLATFORM_RADIUS).west(POCKET_PLATFORM_RADIUS);
		final BlockPos end = POCKET_SPAWN_POS.down().south(POCKET_PLATFORM_RADIUS).east(POCKET_PLATFORM_RADIUS);
		BlockPos.iterate(start, end).forEach(pos -> world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState()));
	}

	@Override
	public void onPlayerChangeDimension(ServerPlayerEntity player) {
		super.onPlayerChangeDimension(player);
		generateSpawnPlatform(this);
	}
}
