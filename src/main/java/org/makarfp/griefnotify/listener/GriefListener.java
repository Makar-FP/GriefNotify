package org.makarfp.griefnotify.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.makarfp.griefnotify.GriefNotify;
import org.makarfp.griefnotify.data.MaterialData;
import org.makarfp.griefnotify.util.GameMessageBuilder;
import org.makarfp.griefnotify.util.TelegramMessageBuilder;
import org.makarfp.griefnotify.util.TelegramUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GriefListener implements Listener {

    private final GriefNotify plugin;
    private final Map<String, Long> lastAlertTimestamps = new ConcurrentHashMap<>();
    private final Map<Player, Long> lastAlertPerPlayer = new ConcurrentHashMap<>();
    private final Map<Location, Player> recentPlacements = new ConcurrentHashMap<>();
    private final Map<Location, Player> recentSpawns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastOwnerAlertTimestamps = new ConcurrentHashMap<>();

    private static final long ALERT_OWNER_COOLDOWN = 600000;
    private static final long ALERT_COOLDOWN = 3600000;
    private static final long ALERT_PER_PLAYER_COOLDOWN = 450000;
    private static final long PLACEMENT_CACHE_TTL = 5 * 60 * 1000;

    public GriefListener(GriefNotify plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            notifyIfNeeded(null, block);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Player source = entity instanceof Player ? (Player) entity : null;

        for (Block block : event.blockList()) {
            notifyIfNeeded(source, block);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            notifyIfNeeded(null, block);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            notifyIfNeeded(null, block);
        }
    }

    @EventHandler
    public void onWitherBreak(EntityChangeBlockEvent event) {
        if (event.getEntityType().name().contains("WITHER")) {
            notifyIfNeeded(null, event.getBlock());
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        Material type = event.getBlock().getType();
        if (!MaterialData.isTracked(type)) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();
        recentPlacements.put(loc, player);

        new BukkitRunnable() {
            @Override
            public void run() {
                recentPlacements.remove(loc);
            }
        }.runTaskLater(plugin, PLACEMENT_CACHE_TTL / 50);
    }

    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            Location loc = event.getLocation();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(loc.getWorld()) && player.getLocation().distance(loc) < 5) {
                    recentSpawns.put(loc, player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            recentSpawns.remove(loc);
                        }
                    }.runTaskLater(plugin, PLACEMENT_CACHE_TTL / 50);
                    break;
                }
            }
        }
    }

    private void notifyIfNeeded(Player attacker, Block block) {
        if (attacker == null) {
            attacker = findNearbyRecentPlacer(block.getLocation());
        }

        RegionManager regions = getRegionManager(block);
        if (regions == null) return;

        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
        if (set.size() == 0) return;

        for (ProtectedRegion region : set) {
            if (shouldSkipRegion(region, attacker)) continue;
            if (!shouldNotify(region.getId(), attacker)) continue;

            updateAlertTimestamps(region.getId(), attacker);

            String attackerName = attacker != null ? attacker.getName() : "Неизвестно";
            String worldName = getWorldName(block.getWorld());

            if (!notifyOnlineOwners(region, attackerName, worldName)) {
                notifyOfflineOwners(region, attackerName, worldName);
            }
        }
    }

    private RegionManager getRegionManager(Block block) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(block.getWorld()));
    }

    private boolean shouldSkipRegion(ProtectedRegion region, Player attacker) {
        if (attacker == null) return false;
        UUID uuid = attacker.getUniqueId();
        return region.getOwners().contains(uuid) || region.getMembers().contains(uuid);
    }

    private boolean shouldNotify(String regionId, Player attacker) {
        long currentTime = System.currentTimeMillis();

        long lastAlert = lastAlertTimestamps.getOrDefault(regionId, 0L);
        if (currentTime - lastAlert < ALERT_COOLDOWN) return false;

        if (attacker != null) {
            long lastPlayerAlert = lastAlertPerPlayer.getOrDefault(attacker, 0L);
            if (currentTime - lastPlayerAlert < ALERT_PER_PLAYER_COOLDOWN) return false;
        }

        return true;
    }

    private void updateAlertTimestamps(String regionId, Player attacker) {
        long currentTime = System.currentTimeMillis();
        lastAlertTimestamps.put(regionId, currentTime);
        if (attacker != null) {
            lastAlertPerPlayer.put(attacker, currentTime);
        }
    }

    private boolean shouldNotifyOwner(UUID ownerId) {
        long currentTime = System.currentTimeMillis();
        long lastAlert = lastOwnerAlertTimestamps.getOrDefault(ownerId, 0L);
        return currentTime - lastAlert >= ALERT_OWNER_COOLDOWN;
    }

    private void updateOwnerAlert(UUID ownerId) {
        lastOwnerAlertTimestamps.put(ownerId, System.currentTimeMillis());
    }

    private boolean notifyOnlineOwners(ProtectedRegion region, String attackerName, String worldName) {
        for (UUID uuid : region.getOwners().getUniqueIds()) {
            if (!shouldNotifyOwner(uuid)) continue;

            Player owner = Bukkit.getPlayer(uuid);
            if (owner != null && owner.isOnline() && plugin.getConfigManager().isGriefNotifyEnabled(owner.getName())) {
                sendInGameMessage(owner, attackerName, region.getId(), worldName);
                updateOwnerAlert(uuid); // запоминаем время
                return true;
            }
        }
        return false;
    }

    private void notifyOfflineOwners(ProtectedRegion region, String attackerName, String worldName) {
        for (UUID uuid : region.getOwners().getUniqueIds()) {
            if (!shouldNotifyOwner(uuid)) continue;

            String offlineName = Bukkit.getOfflinePlayer(uuid).getName();
            if (offlineName != null && plugin.getConfigManager().isGriefNotifyEnabled(offlineName)) {
                Long telegramId = plugin.getConfigManager().getTelegramId(offlineName);
                if (telegramId != null) {
                    String message = TelegramMessageBuilder.build(attackerName, region.getId() + " (" + worldName + ")");
                    updateOwnerAlert(uuid); // запоминаем
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            TelegramUtil.sendTelegramMessage(telegramId, message);
                        }
                    }.runTaskAsynchronously(plugin);
                }
            }
        }
    }

    private Player findNearbyRecentPlacer(Location location) {
        for (Map.Entry<Location, Player> entry : recentPlacements.entrySet()) {
            if (entry.getKey().getWorld().equals(location.getWorld()) && entry.getKey().distance(location) <= 15) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Location, Player> entry : recentSpawns.entrySet()) {
            if (entry.getKey().getWorld().equals(location.getWorld()) && entry.getKey().distance(location) <= 15) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void sendInGameMessage(Player player, String attackerName, String regionId, String worldName) {
        String message = GameMessageBuilder.buildInGameMessage(attackerName, regionId + " (" + worldName + ")");
        String title = ChatColor.RED + "" + ChatColor.BOLD + "ГРИФ!";
        String subtitle = ChatColor.GRAY + "Ваш регион пытаются загриферить";
        player.sendMessage(message);
        player.sendTitle(title, subtitle, 10, 70, 20);

        new BukkitRunnable() {
            int timesPlayed = 0;

            @Override
            public void run() {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
                timesPlayed++;
                if (timesPlayed >= 6) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private String getWorldName(World world) {
        switch (world.getName()) {
            case "world":
                return "Обычный мир";
            case "world_nether":
                return "Нижний мир";
            case "world_the_end":
                return "Эндер мир";
            default:
                return world.getName();
        }
    }
}