package net.ticketeer;

import net.ticketeer.command.Command;
import net.ticketeer.command.CommandContext;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.prompt.CreateTicketPrompt;
import net.ticketeer.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

public class TicketCommands {
    private final Ticketeer plugin;

    public TicketCommands(Ticketeer plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = { "ticket" },
            usage = "new",
            desc = "Create a ticket using the ingame interface",
            modifiers = { "new" },
            min = 1,
            max = 1,
            permission = "ticketeer.new")
    public void createTicket(CommandContext args, CommandSender sender) {
        if (!plugin.getAPI().isVerified()) {
            Messaging.sendError(sender, CommandMessages.MUST_BE_VERIFIED);
            return;
        }
        if (!(sender instanceof Conversable) || ((Conversable) sender).isConversing()) {
            return;
        }
        CreateTicketPrompt.setupConversation(plugin).withFirstPrompt(new CreateTicketPrompt())
        .buildConversation((Conversable) sender).begin();
    }

    @Command(
            aliases = { "ticket" },
            usage = "web",
            desc = "Get a link to create a new ticket",
            modifiers = { "web" },
            min = 1,
            max = 1,
            permission = "ticketeer.web")
    public void web(CommandContext args, CommandSender sender) {
        if (!plugin.getAPI().isVerified()) {
            Messaging.sendError(sender, CommandMessages.MUST_BE_VERIFIED);
            return;
        }
        String url = plugin.getAPI().getNewTicketURL(sender);
        Messaging.sendTr(sender, CommandMessages.TICKET_URL, url);
    }
}
