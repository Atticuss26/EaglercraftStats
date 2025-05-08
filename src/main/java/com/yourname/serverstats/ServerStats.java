package com.yourname.serverstats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yourname.serverstats.listeners.PlayerListener;           // ← fixed import
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;                           // ← specify UTF-8 charset
import java.util.Map;

public class ServerStats extends JavaPlugin implements PluginMessageListener {
    private static final String CHANNEL = "eaglercraft:stats";
    private StatsCollector statsCollector;
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void onEnable() {
        statsCollector = new StatsCollector(this);

        // register our join/quit listener
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this); :contentReference[oaicite:5]{index=5}

        // plugin messaging channels (Paper plugin-messaging guide)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL);       :contentReference[oaicite:6]{index=6}
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);

        // broadcast every 5 seconds (100 ticks)
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::broadcastStats, 100L, 100L);

        getLogger().info("ServerStats enabled on Paper 1.21.4");
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CHANNEL, this);
        getLogger().info("ServerStats disabled");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // no incoming handling yet
    }

    private void broadcastStats() {
        try {
            Map<String, Object> data = statsCollector.collectStats();
            String json = gson.toJson(data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(json.getBytes(StandardCharsets.UTF_8));                   :contentReference[oaicite:7]{index=7}
            byte[] payload = baos.toByteArray();

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendPluginMessage(this, CHANNEL, payload);
            }
        } catch (Exception e) {
            getLogger().warning("Failed to broadcast stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public StatsCollector getStatsCollector() {
        return statsCollector;
    }
}
