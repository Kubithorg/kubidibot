package fr.kubithon.kubidibot;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.command.CommandDrop;
import fr.kubithon.kubidibot.command.CommandPop;
import fr.kubithon.kubidibot.command.CommandVolume;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.IBot;
import org.krobot.Krobot;
import org.krobot.command.CommandManager;
import org.krobot.config.ConfigProvider;

public class Kubidibot implements IBot
{
    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = LogManager.getLogger("Kubidibot");

    @Inject
    private CommandManager commands;

    @Inject
    private ConfigProvider config;

    @Inject
    private JDA jda;

    @Override
    public void init()
    {
        LOGGER.info("Starting Kubidibot v{}...", VERSION);

        // Registering events
        jda.addEventListener(this);

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

    @SubscribeEvent
    public void onJoin(GuildMemberJoinEvent event)
    {
        event.getMember().getUser().openPrivateChannel().complete().sendMessage(config.at("app.welcome"));
    }

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException
    {
        Krobot.start(args[0], Kubidibot.class);
    }
}
