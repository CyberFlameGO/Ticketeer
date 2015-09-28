package net.ticketeer.prompt;

import net.ticketeer.Ticketeer;
import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Messaging;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import com.google.common.collect.ImmutableMap;

public class EnterPasswordPrompt extends StringPrompt {
    @Override
    public Prompt acceptInput(final ConversationContext context, String input) {
        input = input.trim();
        if (input.isEmpty() || input.length() > 200) {
            Messaging.sendTr((CommandSender) context.getForWhom(), CommandMessages.INVALID_PASSWORD);
            return this;
        }
        context.setSessionData("password", input);
        Bukkit.getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable() {
            @Override
            public void run() {
                final String error = ((Ticketeer) context.getPlugin()).getAPI().loginUser(
                        (CommandSender) context.getForWhom(), (String) context.getSessionData("email"),
                        (String) context.getSessionData("password"));
                Bukkit.getScheduler().runTask(context.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        if (!error.isEmpty()) {
                            CreateTicketPrompt.setupConversation(context.getPlugin())
                                    .withInitialSessionData(ImmutableMap.<Object, Object> of("error", error))
                            .withFirstPrompt(new EnterEmailPrompt()).buildConversation(context.getForWhom())
                            .begin();
                        } else {
                            CreateTicketPrompt.setupConversation(context.getPlugin())
                            .withFirstPrompt(new EnterTitlePrompt()).buildConversation(context.getForWhom())
                            .begin();
                        }
                    }
                });
            }
        });
        return null;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Messaging.sendTr(context.getForWhom(), CommandMessages.PASSWORD_PROMPT);
        return "";
    }
}
