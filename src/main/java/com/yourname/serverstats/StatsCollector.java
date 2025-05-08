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
    private long startTime;
    
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
        stats.put("uptime", (System.currentTimeMillis() - startTime) / 1000);
        stats.put("tps", calculateTPS());
        
        // Performance metrics
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        
        stats.put("memoryUsed", usedMemory);
        stats.put("memoryMax", maxMemory);
        stats.put("cpuLoad", Math.round(osBean.getSystemLoadAverage() * 100) / 100.0);
        
        // World info
        Map<String, Object> worlds = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            Map<String, Object> worldInfo = new HashMap<>();
            worldInfo.put("entities", world.getEntities().size());
            worldInfo.put("chunks", world.getLoadedChunks().length);
            worldInfo.put("players", world.getPlayers().size());
            worlds.put(world.getName(), worldInfo);
        }
        stats.put("worlds", worlds);
        
        // Additional stats
        stats.put("maxPlayersEver", maxPlayersOnline);
        stats.put("totalPlayTime", totalPlayTime);
        
        return stats;
    }
    
    private double calculateTPS() {
        // This is a simplistic approach - for a more accurate TPS calculation
        // you should implement a moving average over multiple sample points
        return Math.min(20, Bukkit.getServer().getTPS()[0];
    }
    
    public void updatePlayerStats() {
        int currentPlayers = Bukkit.getOnlinePlayers().size();
        if (currentPlayers > maxPlayersOnline) {
            maxPlayersOnline = currentPlayers;
        }
        
        // Update total play time (in seconds)
        totalPlayTime += currentPlayers;
    }
}
