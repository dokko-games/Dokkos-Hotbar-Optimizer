package dev.dokko.ho;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class ServerDatabase {
    public static boolean shouldBlock = false;
    public static boolean shouldFlag = false;

    public static void reset() {
        shouldBlock = false;
        shouldFlag = false;
    }
    public static final List<String> serversThatBlock = new ArrayList<>();
    public static final List<String> serversThatFlag = new ArrayList<>();
    public static void init(){
        serversThatFlag.add("mcpvp.club");
    }
    public static boolean isIn(String server){
        return MinecraftClient.getInstance().getCurrentServerEntry().address.contains(server);
    }
}
