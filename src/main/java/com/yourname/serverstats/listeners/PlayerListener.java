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
        // trigger an immediate stats update when someone joins
        plugin.getStatsCollector().collectStats();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // likewise on quit
        plugin.getStatsCollector().collectStats();
    }
}
