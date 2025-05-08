package com.yourname.serverstats.listeners;

import com.yourname.serverstats.ServerStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final ServerStats plugin;
    
    public PlayerListener(ServerStats plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // You can add custom join stats here
        plugin.getStatsCollector().updatePlayerStats();
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // You can add custom quit stats here
        plugin.getStatsCollector().updatePlayerStats();
    }
}