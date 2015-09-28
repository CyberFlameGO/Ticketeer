package net.ticketeer.prompt;

import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class EnterTitlePrompt extends StringPrompt {
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        input = input.trim();
        if (input.isEmpty() || input.length() > 200) {
            Messaging.sendTr((CommandSender) context.getForWhom(), CommandMessages.INVALID_TITLE);
            return this;
        }
        context.setSessionData("title", input);
        Messaging.sendTr(context.getForWhom(), CommandMessages.TITLE_SET, input);
        return new EnterContentPrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Messaging.sendTr(context.getForWhom(), CommandMessages.TITLE_PROMPT);
        return "";
    }
}
