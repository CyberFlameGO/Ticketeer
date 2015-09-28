package net.ticketeer.prompt;

import net.ticketeer.Ticketeer;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

public class EndPrompt {
    public void finish(final ConversationContext context) {
        Messaging.sendTr(context.getForWhom(), CommandMessages.SENDING_TICKET);
        Bukkit.getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (context.getPlugin().isEnabled()) {
                    String ticketURL = ((Ticketeer) context.getPlugin()).getAPI().submitTicket(
                            (CommandSender) context.getForWhom(), (String) context.getSessionData("title"),
                            (String) context.getSessionData("content"));
                    if (ticketURL.isEmpty()) {
                        Messaging.sendErrorTr(context.getForWhom(), CommandMessages.TICKET_SUBMISSION_FAILED);
                    } else {
                        Messaging.sendTr(context.getForWhom(), CommandMessages.CREATE_TICKET_END, ticketURL);
                    }
                }
            }
        });
    }
}