package net.ticketeer.prompt;

import net.ticketeer.Ticketeer;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

public class CreateTicketPrompt implements Prompt {
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (((Ticketeer) context.getPlugin()).getAPI().isLoggedIn((CommandSender) context.getForWhom())) {
            return new EnterTitlePrompt();
        } else {
            return new EnterEmailPrompt();
        }
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
        return false;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Messaging.sendTr(context.getForWhom(), CommandMessages.CREATE_TICKET_HEADER);
        return "";
    }

    public static ConversationFactory setupConversation(Plugin plugin) {
        return new ConversationFactory(plugin).withConversationCanceller(new RegexConversationCanceller("finish|exit"))
                .withLocalEcho(false).withModality(true);
    }
}
