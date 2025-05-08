package com.yourname.serverstats;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

public class StatsCollector {
    private final Plugin plugin;
    private final long startTime;
    private long totalPlayTime = 0;
    private int maxPlayersEver = 0;

    public StatsCollector(Plugin plugin) {
        this.plugin = plugin;
        this.startTime = System.currentTimeMillis();

        // tally per-second playtime and max-online
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateCumulative, 20L, 20L); :contentReference[oaicite:8]{index=8}
    }

    public Map<String, Object> collectStats() {
        Map<String, Object> out = new HashMap<>();

        out.put("version", Bukkit.getVersion());
        out.put("online", Bukkit.getOnlinePlayers().size());
        out.put("maxPlayers", Bukkit.getMaxPlayers());
        out.put("uptimeSec", (System.currentTimeMillis() - startTime) / 1000L);

        // Paper’s Server#getTPS() gives [1m,5m,15m] averages—take the first element. :contentReference[oaicite:9]{index=9}
        double tps = Bukkit.getServer().getTPS()[0];
        out.put("tps", Math.min(20.0, tps));

        // JVM memory metrics
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        out.put("memUsedMB", mem.getHeapMemoryUsage().getUsed() / (1024*1024));
        out.put("memMaxMB",  mem.getHeapMemoryUsage().getMax()  / (1024*1024));

        // OS load average
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        out.put("loadAvg", os.getSystemLoadAverage());

        // per-world stats
        Map<String, Map<String, Object>> worlds = new HashMap<>();
        for (World w : Bukkit.getWorlds()) {
            Map<String, Object> info = new HashMap<>();
            info.put("entities",     w.getEntities().size());
            info.put("chunks",       w.getLoadedChunks().length);
            info.put("players",      w.getPlayers().size());
            worlds.put(w.getName(), info);
        }
        out.put("worlds", worlds);

        // cumulative stats
        out.put("maxOnlineEver",    maxPlayersEver);
        out.put("totalPlayTimeSec", totalPlayTime);

        return out;
    }

    private void updateCumulative() {
        int curr = Bukkit.getOnlinePlayers().size();
        totalPlayTime += curr;
        if (curr > maxPlayersEver) {
            maxPlayersEver = curr;
        }
    }
}
