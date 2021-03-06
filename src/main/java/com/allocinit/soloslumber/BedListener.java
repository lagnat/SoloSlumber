package com.allocinit.soloslumber;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BedListener implements Listener {
    private SoloSlumber soloSlumber;

    public BedListener(SoloSlumber soloSlumber) {
        this.soloSlumber = soloSlumber;
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() != BedEnterResult.OK)
            return;

        // TODO - Should we schedule this a tick from now to see if the
        // player DOES enter the bed? e.g. bed enter was denied by some
        // other plugin that runs after us.

        final Player player = e.getPlayer();
        final World world = player.getWorld();

        if (!SoloSlumber.getPlugin().allowedInWorld(player)) {
            return;
        }

        // If the sleeper was previously a waker, remove his veto
        List<Player> forcers = SoloSlumber.getPlugin().getNightForcers().get(world.getUID());
        if (forcers != null)
            forcers.remove(player);

        // Night forced?
        if (this.soloSlumber.getConfig().getBoolean("wakerForcesNight")) {
            Map<UUID, Long> worldVsNextDay = SoloSlumber.getPlugin().getWorldVsNextDay();
            Long nextDay = worldVsNextDay.get(world.getUID());
            if (nextDay == null)
                nextDay = 0L;
            if (world.getFullTime() < nextDay && forcers != null && !forcers.isEmpty()) {
                for (Player forcer : forcers) {
                    player.sendMessage(this.soloSlumber.getMessage(forcer, "skip_night_vetoed"));
                }
                return;
            }
        }

        boolean foundOtherPlayers = false;

        for (Player otherPlayer : world.getPlayers()) {
            if (otherPlayer == player)
                continue;

            foundOtherPlayers = true;

            TextComponent message = new TextComponent(
                    TextComponent.fromLegacyText(this.soloSlumber.getMessage(player, "sleep_warning")));

            TextComponent clickHere = new TextComponent(
                    TextComponent.fromLegacyText(this.soloSlumber.getMessage(player, "click_here")));
            clickHere.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/soloslumber wake"));
            clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.soloSlumber.getMessage(player, "wake_them_up")).create()));

            message.addExtra(clickHere);

            otherPlayer.spigot().sendMessage(message);
        }

        if (!foundOtherPlayers)
            return;

        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLater(soloSlumber, () -> {
            makeDay(world);
        }, soloSlumber.getConfig().getLong("napTime"));

        this.soloSlumber.getSleepers().put(player.getName(), task);
    }

    @EventHandler
    public void onBedLeave(final PlayerBedLeaveEvent e) {
        final Player player = e.getPlayer();

        if (!SoloSlumber.getPlugin().allowedInWorld(player)) {
            return;
        }

        BukkitTask task = this.soloSlumber.getSleepers().remove(player.getName());
        if (task != null) {
            task.cancel();
        }

        if (isNight(player.getWorld()))
            SoloSlumber.getPlugin().tellEveryoneElse(player, SoloSlumber.getPlugin().getMessage(player, "got_up"));
    }

    private void makeDay(World world) {
        if (isNight(world) || world.hasStorm() || world.isThundering()) {
            world.setTime(0L);
            if (world.hasStorm()) {
                world.setStorm(false);
                world.setThundering(false);
            }

            String msg = this.soloSlumber.getMessage(null, "night_skipped");

            if (msg != null && !msg.isEmpty()) {
                for (Player p : world.getPlayers()) {
                    p.sendMessage(msg);
                }
            }
        }
    }

    private boolean isNight(World world) {
        return world.getTime() >= 12541 && world.getTime() <= 23458;
    }
}