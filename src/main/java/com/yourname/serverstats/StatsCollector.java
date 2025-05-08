package com.yourname.serverstats;

import org.bukkit.Bukkit;
import org.bukkit.World;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

public class StatsCollector {
    
    private final ServerStats plugin;
    private long totalPlayTime = 0;
    private int maxPlayersOnline = 0;
    private final long startTime;
    
    public StatsCollector(ServerStats plugin) {
        this.plugin = plugin;
        this.startTime = System.currentTimeMillis();
    }
    
    public Map<String, Object> collectStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Server info
        stats.put("serverVersion", Bukkit.getVersion());
        stats.put("onlinePlayers", Bukkit.getOnlinePlayers().size());
        stats.put("maxPlayers", Bukkit.getMaxPlayers());
        stats.put("uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000);
        stats.put("tps", calculateTPS());
        
        // Performance metrics
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxMemory  = memoryBean.getHeapMemoryUsage().getMax()  / (1024 * 1024);
        
        stats.put("memoryUsedMB", usedMemory);
        stats.put("memoryMaxMB",  maxMemory);
        stats.put("systemLoadAvg", osBean.getSystemLoadAverage());
        
        // World info
        Map<String, Object> worlds = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            Map<String, Object> worldInfo = new HashMap<>();
            worldInfo.put("entities", world.getEntities().size());
            worldInfo.put("loadedChunks", world.getLoadedChunks().length);
            worldInfo.put("playersInWorld", world.getPlayers().size());
            worlds.put(world.getName(), worldInfo);
        }
        stats.put("worlds", worlds);
        
        // Cumulative stats
        stats.put("maxPlayersEver", maxPlayersOnline);
        stats.put("totalPlayTimeSeconds", totalPlayTime);
        
        return stats;
    }
    
    private double calculateTPS() {
        // Paper Server#getTPS() gives [1min, 5min, 15min] averages
        return Math.min(20.0, Bukkit.getServer().getTPS()[0]);
    }
    
    /**
     * Call this on a repeating task (e.g. every second) to track playtime
     * and record new maximum online-player counts.
     */
    public void updatePlayerStats() {
        int curr = Bukkit.getOnlinePlayers().size();
        if (curr > maxPlayersOnline) {
            maxPlayersOnline = curr;
        }
        totalPlayTime += curr;  // adds one second per online player per call
    }
}
