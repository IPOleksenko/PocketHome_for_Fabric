package dev.ipoleksenko.pockethome.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class PocketWorld extends RuntimeWorld {
	private static final int POCKET_PLATFORM_RADIUS = 16;

	public static @NotNull BlockPos POCKET_SPAWN_POS(int i) {
		return new BlockPos(0, i, 0);
	}

	public PocketWorld(MinecraftServer server, RegistryKey<World> registryKey, RuntimeWorldConfig config, Style style) {
		super(server, registryKey, config, style);
	}

	private static void generateEnderChestPlatform(ServerWorld world) {
		BlockPos.iterate(new BlockPos(0, 10, 1),
						new BlockPos(0, 10, 1))
				.forEach(pos -> world.setBlockState(pos, Blocks.ENDER_CHEST.getDefaultState()));
	}
	private static void generateBedrockPlatform(ServerWorld world) {
		final BlockPos start = POCKET_SPAWN_POS(1).down().north(POCKET_PLATFORM_RADIUS).west(POCKET_PLATFORM_RADIUS);
		final BlockPos end = POCKET_SPAWN_POS(1).down().south(POCKET_PLATFORM_RADIUS).east(POCKET_PLATFORM_RADIUS);
		BlockPos.iterate(start, end).forEach(pos -> world.setBlockState(pos, Blocks.BEDROCK.getDefaultState()));
	}

	private static void generateDirtPlatform(ServerWorld world) {
		final BlockPos start = POCKET_SPAWN_POS(2).down().north(POCKET_PLATFORM_RADIUS).west(POCKET_PLATFORM_RADIUS);
		final BlockPos end = POCKET_SPAWN_POS(9).down().south(POCKET_PLATFORM_RADIUS).east(POCKET_PLATFORM_RADIUS);
		BlockPos.iterate(start, end).forEach(pos -> world.setBlockState(pos, Blocks.DIRT.getDefaultState()));
	}

	private static void generateGrassBlockPlatform(ServerWorld world) {
		final BlockPos start = POCKET_SPAWN_POS(10).down().north(POCKET_PLATFORM_RADIUS).west(POCKET_PLATFORM_RADIUS);
		final BlockPos end = POCKET_SPAWN_POS(10).down().south(POCKET_PLATFORM_RADIUS).east(POCKET_PLATFORM_RADIUS);
		BlockPos.iterate(start, end).forEach(pos -> world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState()));
	}

	private boolean isWorldAlreadyCreated(ServerWorld world) {
		BlockPos markerPos = POCKET_SPAWN_POS(1).down();
		Block blockAtMarker = world.getBlockState(markerPos).getBlock();
		return blockAtMarker == Blocks.BEDROCK;
	}

	private static void setGamerules(ServerWorld world) {
		GameRules rules = world.getGameRules();

		rules.get(GameRules.DO_FIRE_TICK).set(false, world.getServer());
		rules.get(GameRules.DO_MOB_GRIEFING).set(false, world.getServer());
		rules.get(GameRules.KEEP_INVENTORY).set(true, world.getServer());
		rules.get(GameRules.DO_MOB_SPAWNING).set(false, world.getServer());
		rules.get(GameRules.DO_INSOMNIA).set(false, world.getServer());
		rules.get(GameRules.DROWNING_DAMAGE).set(false, world.getServer());
		rules.get(GameRules.FALL_DAMAGE).set(false, world.getServer());
		rules.get(GameRules.FIRE_DAMAGE).set(false, world.getServer());
	}

	public static void setWorldBorder(ServerWorld world) {
		WorldBorder border = world.getWorldBorder();
		border.setCenter(0, 0);
		border.setSize(40);
	}

	@Override
	public void onPlayerChangeDimension(ServerPlayerEntity player) {
		super.onPlayerChangeDimension(player);

		MinecraftServer server = player.getServer();
		if (server != null) {
			ServerWorld world = this;
			if (!isWorldAlreadyCreated(world)) {
				generateDirtPlatform(world);
				generateGrassBlockPlatform(world);
				generateEnderChestPlatform(this);
				setGamerules(world);
				setWorldBorder(world);
			}
			generateBedrockPlatform(world);
		}
	}
}
