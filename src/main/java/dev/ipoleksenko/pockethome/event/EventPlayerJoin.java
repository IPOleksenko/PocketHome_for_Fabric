package dev.ipoleksenko.pockethome.event;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class EventPlayerJoin {
    private static String messageFirstJoin = """
            Hello, %s
            I'm a PocketHome
            ---------------------------------------------
            What would join to your pocket home
            You need to use an ender chest while sneaking
            ---------------------------------------------
            What would swap time in your pocket home
            You need dream in it
            ---------------------------------------------
            What would swap weather in your pocket home
            You need use harem no jutsu!
            """;

    public static void send(ServerPlayNetworkHandler handler) {
        if (handler.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0)
            handler.player.sendMessage(Text.of(String.format(messageFirstJoin, handler.player.getEntityName())));
    }
}