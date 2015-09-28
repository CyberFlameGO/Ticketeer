package net.ticketeer;

import java.io.File;
import java.util.Locale;

import net.ticketeer.command.CommandManager;
import net.ticketeer.command.Injector;
import net.ticketeer.util.Translator;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Ticketeer extends JavaPlugin {
    private final TicketeerAPI api = new TicketeerAPI();
    private final CommandManager commands = new CommandManager();

    public TicketeerAPI getAPI() {
        return api;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String cmdName, String[] args) {
        String modifier = args.length > 0 ? args[0] : "";
        if (!commands.hasCommand(command, modifier) && !modifier.isEmpty()) {
            return commands.suggestClosestModifier(sender, command.getName(), modifier);
        }

        Object[] methodArgs = { sender };
        return commands.executeSafe(command, args, sender, methodArgs);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupTranslator();
        commands.setInjector(new Injector(this));
        commands.register(TicketeerCommands.class);
        commands.register(TicketCommands.class);
        api.configure(getConfig());
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                api.verifyServerCredentials();
            }
        });
        // new Updater(this, 88413, getFile(), Updater.UpdateType.DEFAULT,
        // getConfig().getBoolean("autoupdate", true))
        // .getResult();
        saveConfig();
    }

    private void setupTranslator() {
        Locale locale = Locale.getDefault();
        String setting = getConfig().getString("locale");
        if (!setting.isEmpty()) {
            String[] parts = setting.split("[\\._]");
            switch (parts.length) {
                case 1:
                    locale = new Locale(parts[0]);
                    break;
                case 2:
                    locale = new Locale(parts[0], parts[1]);
                    break;
                case 3:
                    locale = new Locale(parts[0], parts[1], parts[2]);
                    break;
                default:
                    break;
            }
        }
        Translator.setInstance(new File(getDataFolder(), "lang"), locale);
    }
}
