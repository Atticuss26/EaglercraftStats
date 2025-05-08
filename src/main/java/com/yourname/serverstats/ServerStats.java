package com.yourname.serverstats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerStats extends JavaPlugin implements PluginMessageListener {
    private static final String CHANNEL = "eaglercraft:stats";
    private StatsCollector statsCollector;
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void onEnable() {
        this.statsCollector = new StatsCollector(this);

        // register listener & messaging channel
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);

        // schedule a repeating task: broadcast every 5 seconds (100 ticks)
        Bukkit.getScheduler()
              .runTaskTimerAsynchronously(this, this::broadcastStats, 100L, 100L);

        getLogger().info("ServerStats enabled on Paper 1.21.4");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL, this);
        getLogger().info("ServerStats disabled");
    }

    private void broadcastStats() {
        try {
            Map<String, Object> data = statsCollector.collectStats();
            String json = gson.toJson(data);

            // prepare message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(json.getBytes(StandardCharsets.UTF_8));
            byte[] payload = baos.toByteArray();

            // send to all online players
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendPluginMessage(this, CHANNEL, payload);
            }
        } catch (Exception e) {
            getLogger().warning("Failed to broadcast stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // if you want to handle incoming requests, do it here
    }

    public StatsCollector getStatsCollector() {
        return statsCollector;
    }
}
