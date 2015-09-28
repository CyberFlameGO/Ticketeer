package net.ticketeer.prompt;

import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class EnterEmailPrompt extends StringPrompt {
    @Override
    public Prompt acceptInput(final ConversationContext context, String input) {
        input = input.trim();
        if (input.isEmpty() || input.length() > 200) {
            Messaging.sendTr((CommandSender) context.getForWhom(), CommandMessages.INVALID_EMAIL);
            return this;
        }
        context.setSessionData("email", input);
        return new EnterPasswordPrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
        if (context.getSessionData("error") != null) {
            Messaging.sendErrorTr(context.getForWhom(), CommandMessages.ENTER_EMAIL_PROMPT_ERROR,
                    context.getSessionData("error"));
            return "";
        }
        Messaging.sendTr(context.getForWhom(), CommandMessages.ENTER_EMAIL_PROMPT);
        return "";
    }
}
