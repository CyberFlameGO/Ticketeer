package net.ticketeer;

import net.ticketeer.command.Command;
import net.ticketeer.command.CommandContext;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TicketeerCommands {
    private final Ticketeer plugin;

    public TicketeerCommands(Ticketeer plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = { "ticketeer" },
            usage = "link [name] [secret]",
            desc = "Links the server to Ticketeer",
            modifiers = { "link" },
            min = 3,
            max = 3,
            permission = "ticketeer.link")
    public void link(CommandContext args, final CommandSender sender) {
        final String name = args.getString(1);
        final String secret = args.getString(2);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getAPI().setCredentials(name, secret);
                boolean success = plugin.getAPI().verifyServerCredentials();
                if (success) {
                    Messaging.sendTr(sender, CommandMessages.LINK_SUCCESSFUL);
                    plugin.getConfig().set("name", name);
                    plugin.getConfig().set("secret", secret);
                    plugin.saveConfig();
                } else {
                    plugin.getAPI().setCredentials("", "");
                    Messaging.sendErrorTr(sender, CommandMessages.LINK_UNSUCCESSFUL);
                }
            }
        });
    }
}
