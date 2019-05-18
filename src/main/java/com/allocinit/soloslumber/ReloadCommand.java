package com.allocinit.soloslumber;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(SoloSlumber plugin) {
        super(plugin, "reload");
    }

    @Override
    public void doCommand(CommandSender sender, String[] args) throws Exception {
        if (args.length != 0)
            throw new UsageException();

        this.soloSlumber.reloadConfig();

        sender.sendMessage("" + ChatColor.GREEN + "Configuration reloaded.");
    }

    @Override
    public void writeUsage(CommandSender player) {
        player.sendMessage(ChatColor.GREEN + "/soloslumber reload" + ChatColor.YELLOW + "- Reload the configuration.");
    }
}
