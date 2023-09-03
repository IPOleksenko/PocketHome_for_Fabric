package dev.ipoleksenko.pockethome;

import dev.ipoleksenko.pockethome.event.EventPlayerJoin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PocketHomeMod implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PocketHomeMod.class);
    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            EventPlayerJoin.send(handler, server);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockState blockState = world.getBlockState(hitResult.getBlockPos());
            if (    !world.isClient
                    && player.isSneaking()
                    && blockState.isOf(Blocks.ENDER_CHEST))
            {
                player.sendMessage(Text.literal("Андрюша сфоткай, андрюша сфоткай меня"));
            }

            return ActionResult.PASS;
        });
    }
}

