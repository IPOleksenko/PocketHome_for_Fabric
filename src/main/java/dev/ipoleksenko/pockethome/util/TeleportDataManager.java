package dev.ipoleksenko.pockethome.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;

public class TeleportDataManager {
    private static final String TELEPORT_DATA_FOLDER = "teleport_data";

    public static void saveTeleportData(ServerPlayerEntity player, World fromWorld, double x, double y, double z) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        File folder = server.getSavePath(WorldSavePath.ROOT).resolve(TELEPORT_DATA_FOLDER).toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File dataFile = new File(folder, player.getUuidAsString() + ".dat");

        NbtCompound data = new NbtCompound();
        data.putString("fromWorld", fromWorld.getRegistryKey().getValue().toString());
        data.putDouble("x", x);
        data.putDouble("y", y);
        data.putDouble("z", z);

        try {
            NbtIo.write(data, dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TeleportData loadTeleportData(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        File folder = server.getSavePath(WorldSavePath.ROOT).resolve(TELEPORT_DATA_FOLDER).toFile();
        File dataFile = new File(folder, player.getUuidAsString() + ".dat");

        if (dataFile.exists()) {
            try {
                NbtCompound data = NbtIo.read(dataFile);
                if (data != null) {
                    String fromWorld = data.getString("fromWorld");
                    double x = data.getDouble("x");
                    double y = data.getDouble("y");
                    double z = data.getDouble("z");
                    return new TeleportData(new Identifier(fromWorld), x, y, z);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class TeleportData {
        private final Identifier fromWorld;
        private final double x, y, z;

        public TeleportData(Identifier fromWorld, double x, double y, double z) {
            this.fromWorld = fromWorld;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Identifier getFromWorld() {
            return fromWorld;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
