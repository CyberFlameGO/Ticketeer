package net.ticketeer.prompt;

import net.ticketeer.Ticketeer;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class EnterContentPrompt extends StringPrompt {
    @Override
    public Prompt acceptInput(final ConversationContext context, String input) {
        input = input.trim();
        if (input.isEmpty() || input.length() > 500000) {
            Messaging.sendTr((CommandSender) context.getForWhom(), CommandMessages.INVALID_CONTENT);
            return this;
        }
        context.setSessionData("content", input);

        final CommandSender sender = (CommandSender) context.getForWhom();
        if (!((Ticketeer) context.getPlugin()).getAPI().isLoggedIn(sender)) {
            return new EnterEmailPrompt();
        } else {
            new EndPrompt().finish(context);
            return null;
        }
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Messaging.sendTr(context.getForWhom(), CommandMessages.ENTER_CONTENT_PROMPT);
        return "";
    }
}
