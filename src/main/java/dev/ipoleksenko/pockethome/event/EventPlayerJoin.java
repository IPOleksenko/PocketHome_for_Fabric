package dev.ipoleksenko.pockethome.event;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventPlayerJoin {
    private static final String messageFirstJoin = """
            Hello, %s
            I'm a PocketHome
            ---------------------------------------------
            To join your pocket home,
            use an ender chest while sneaking.
            """;

    public static void send(ServerPlayNetworkHandler handler) {
        if (handler.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
            handler.player.sendMessage(Text.of(String.format(messageFirstJoin, handler.player.getEntityName())));
        }
    }
}