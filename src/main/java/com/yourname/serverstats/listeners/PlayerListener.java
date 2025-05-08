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
    public void onJoin(PlayerJoinEvent e) {
        plugin.getStatsCollector().collectStats();                         :contentReference[oaicite:10]{index=10}
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getStatsCollector().collectStats();
    }
}
