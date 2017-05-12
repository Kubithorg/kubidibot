package fr.kubithon.kubidibot;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.command.CommandDrop;
import fr.kubithon.kubidibot.command.CommandPop;
import fr.kubithon.kubidibot.command.CommandVolume;
import fr.litarvan.krobot.IBot;
import fr.litarvan.krobot.Krobot;
import fr.litarvan.krobot.command.CommandManager;
import fr.litarvan.krobot.config.ConfigProvider;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Kubidibot implements IBot
{
    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = LogManager.getLogger("Kubidibot");

    @Inject
    private CommandManager commands;

    @Inject
    private ConfigProvider config;

    @Override
    public void init()
    {
        LOGGER.info("Starting Kubidibot v{}...", VERSION);

        // Registering configs
        config.from("config/app.json");
        config.from("config/network.json");

        // Registering commands
        commands.group().prefix(config.at("app.prefix")).apply(this::commands);

        LOGGER.info("Bot started");
    }

    private void commands()
    {
        commands.make("pop", CommandPop.class).register();
        commands.make("drop", CommandDrop.class).register();
        commands.make("volume [value:number]", CommandVolume.class).register();
    }

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException
    {
        Krobot.start(args[0], Kubidibot.class);
    }
}
