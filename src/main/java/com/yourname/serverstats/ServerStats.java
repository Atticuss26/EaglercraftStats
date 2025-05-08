package com.yourname.serverstats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yourname.serverstats.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ServerStats extends JavaPlugin implements PluginMessageListener {
    
    private static final String CHANNEL_NAME = "eaglercraft:stats";
    private StatsCollector statsCollector;
    private final Gson gson = new GsonBuilder().create();
    
    @Override
    public void onEnable() {
        // Initialize stats collector
        statsCollector = new StatsCollector(this);
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Register plugin channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, this);
        
        // Schedule periodic stats updates
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::broadcastStats, 100L, 100L);
        
        getLogger().info("ServerStats plugin enabled");
    }
    
    @Override
    public void onDisable() {
        // Unregister plugin channel
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CHANNEL_NAME, this);
        
        getLogger().info("ServerStats plugin disabled");
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL_NAME)) return;
        
        // Handle incoming requests (if any)
        // For now, we just broadcast stats periodically, so we don't need to handle incoming messages
    }
    
    public void broadcastStats() {
        try {
            Map<String, Object> statsData = statsCollector.collectStats();
            String jsonStats = gson.toJson(statsData);
            
            // Convert to bytes and send to all players
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(jsonStats.getBytes());
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendPluginMessage(this, CHANNEL_NAME, outputStream.toByteArray());
            }
        } catch (Exception e) {
            getLogger().warning("Error broadcasting stats: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public StatsCollector getStatsCollector() {
        return statsCollector;
    }
}