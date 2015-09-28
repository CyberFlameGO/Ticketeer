package net.ticketeer.prompt;

import java.util.regex.Pattern;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

public class RegexConversationCanceller implements ConversationCanceller {
    private final Pattern regex;

    public RegexConversationCanceller(Pattern regex) {
        this.regex = regex;
    }

    public RegexConversationCanceller(String regex) {
        this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean cancelBasedOnInput(ConversationContext context, String input) {
        return regex.matcher(input).matches();
    }

    @Override
    public ConversationCanceller clone() {
        return new RegexConversationCanceller(regex);
    }

    @Override
    public void setConversation(Conversation conversation) {
    }
}
