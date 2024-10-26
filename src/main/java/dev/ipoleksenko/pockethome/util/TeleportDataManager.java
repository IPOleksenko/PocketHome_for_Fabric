package dev.ipoleksenko.pockethome.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class TeleportDataManager {
    private static final String TELEPORT_DATA_FOLDER = "teleport_data";
    private static final Gson GSON = new Gson();

    // Method to initialize an empty JSON file
    private static void initializeJsonFile(File dataFile) {
        JsonObject data = new JsonObject();

        // Initialize all required fields with empty or zero values
        JsonObject fromWorldJson = new JsonObject();
        fromWorldJson.addProperty("namespace", "");
        fromWorldJson.addProperty("path", "");
        data.add("fromWorld", fromWorldJson);

        data.addProperty("x", 0.0);
        data.addProperty("y", 0.0);
        data.addProperty("z", 0.0);

        JsonObject pocketWorldJson = new JsonObject();
        pocketWorldJson.addProperty("x", 0.0);
        pocketWorldJson.addProperty("y", 325.0);
        pocketWorldJson.addProperty("z", 0.0);
        data.add("pocketWorld", pocketWorldJson);

        // Write the empty structure to the file
        try (FileWriter writer = new FileWriter(dataFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save teleport data
    public static void saveTeleportData(ServerPlayerEntity player, World fromWorld, double x, double y, double z) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        File folder = server.getSavePath(WorldSavePath.ROOT).resolve(TELEPORT_DATA_FOLDER).toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File dataFile = new File(folder, player.getUuidAsString() + ".json");

        if (!dataFile.exists()) {
            initializeJsonFile(dataFile); // Create a file with empty fields
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);

            // Update world data and coordinates
            JsonObject fromWorldJson = data.getAsJsonObject("fromWorld");
            fromWorldJson.addProperty("namespace", fromWorld.getRegistryKey().getValue().getNamespace());
            fromWorldJson.addProperty("path", fromWorld.getRegistryKey().getValue().getPath());

            data.addProperty("x", x);
            data.addProperty("y", y);
            data.addProperty("z", z);

            // Write the updated data back to the file
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save coordinates in the pocket world
    public static void savePocketCoordinates(ServerPlayerEntity player, double pocketX, double pocketY, double pocketZ) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            System.out.println("Error: Server not found.");
            return;
        }

        File folder = server.getSavePath(WorldSavePath.ROOT).resolve(TELEPORT_DATA_FOLDER).toFile();
        File dataFile = new File(folder, player.getUuidAsString() + ".json");

        if (!dataFile.exists()) {
            initializeJsonFile(dataFile); // Create a file with empty fields
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);

            // Update pocket world coordinates
            JsonObject pocketWorldJson = data.has("pocketWorld") ? data.getAsJsonObject("pocketWorld") : new JsonObject();

            // Add the world name as `{player_name}` and nested coordinates
            pocketWorldJson.addProperty("worldPath", "dimensions/pocket/" + player.getName().getString());
            pocketWorldJson.addProperty("x", pocketX);
            pocketWorldJson.addProperty("y", pocketY);
            pocketWorldJson.addProperty("z", pocketZ);

            // Save the updated JSON to the root object
            data.add("pocketWorld", pocketWorldJson);

            // Write the updated data back to the file
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(data, writer);
                System.out.println("PocketWorld coordinates successfully saved for player " +
                        player.getName().getString() + ": x=" + pocketX + ", y=" + pocketY + ", z=" + pocketZ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load teleport data
    public static TeleportData loadTeleportData(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        File folder = server.getSavePath(WorldSavePath.ROOT).resolve(TELEPORT_DATA_FOLDER).toFile();
        File dataFile = new File(folder, player.getUuidAsString() + ".json");

        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                JsonObject data = GSON.fromJson(reader, JsonObject.class);

                JsonObject fromWorldJson = data.getAsJsonObject("fromWorld");
                String namespace = fromWorldJson.get("namespace").getAsString();
                String path = fromWorldJson.get("path").getAsString();
                Identifier fromWorld = new Identifier(namespace, path);

                double x = data.get("x").getAsDouble();
                double y = data.get("y").getAsDouble();
                double z = data.get("z").getAsDouble();

                JsonObject pocketWorldJson = data.getAsJsonObject("pocketWorld");
                double pocketX = pocketWorldJson.get("x").getAsDouble();
                double pocketY = pocketWorldJson.get("y").getAsDouble();
                double pocketZ = pocketWorldJson.get("z").getAsDouble();

                return new TeleportData(fromWorld, x, y, z, pocketX, pocketY, pocketZ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Helper class to store teleport data
    public static class TeleportData {
        private final Identifier fromWorld;
        private final double x, y, z;
        private final double pocketX, pocketY, pocketZ;

        public TeleportData(Identifier fromWorld, double x, double y, double z, double pocketX, double pocketY, double pocketZ) {
            this.fromWorld = fromWorld;
            this.x = x;
            this.y = y;
            this.z = z;
            this.pocketX = pocketX;
            this.pocketY = pocketY;
            this.pocketZ = pocketZ;
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

        public double getPocketX() {
            return pocketX;
        }

        public double getPocketY() {
            return pocketY;
        }

        public double getPocketZ() {
            return pocketZ;
        }
    }
}
