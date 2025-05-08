package com.yourname.serverstats;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import com.destroystokyo.paper.ServerPaperMethods; // for getTPS()

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

        // also schedule a per-second updater to tally playtime
        Bukkit.getScheduler().runTaskTimer(this.plugin, this::updateCumulative, 20L, 20L);
    }

    public Map<String, Object> collectStats() {
        Map<String, Object> out = new HashMap<>();

        // basic server info
        out.put("version", Bukkit.getVersion());
        out.put("online", Bukkit.getOnlinePlayers().size());
        out.put("maxPlayers", Bukkit.getMaxPlayers());
        out.put("uptimeSec", (System.currentTimeMillis() - startTime) / 1000L);
        // Paper-specific: instant TPS (1-minute average)
        double tps = ((ServerPaperMethods) Bukkit.getServer()).getTPS()[0];
        out.put("tps", Math.min(20.0, tps));

        // JVM memory
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long usedMB = mem.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxMB  = mem.getHeapMemoryUsage().getMax()  / (1024 * 1024);
        out.put("memUsedMB", usedMB);
        out.put("memMaxMB",  maxMB);

        // OS load
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        out.put("loadAvg", os.getSystemLoadAverage());

        // world details
        Map<String, Map<String, Object>> worlds = new HashMap<>();
        for (World w : Bukkit.getWorlds()) {
            Map<String, Object> info = new HashMap<>();
            info.put("entities", w.getEntities().size());
            info.put("chunks", w.getLoadedChunks().length);
            info.put("players", w.getPlayers().size());
            worlds.put(w.getName(), info);
        }
        out.put("worlds", worlds);

        // cumulative
        out.put("maxOnlineEver", maxPlayersEver);
        out.put("totalPlayTimeSec", totalPlayTime);

        return out;
    }

    private void updateCumulative() {
        int curr = Bukkit.getOnlinePlayers().size();
        totalPlayTime += curr;      // one second per player per tick
        if (curr > maxPlayersEver) {
            maxPlayersEver = curr;
        }
    }
}
