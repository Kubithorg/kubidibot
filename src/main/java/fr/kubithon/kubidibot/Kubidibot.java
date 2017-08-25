package fr.kubithon.kubidibot;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.command.CommandDrop;
import fr.kubithon.kubidibot.command.CommandPop;
import fr.kubithon.kubidibot.command.CommandVolume;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.IBot;
import org.krobot.Krobot;
import org.krobot.command.CommandBuilder;
import org.krobot.command.CommandHandler;
import org.krobot.command.CommandManager;
import org.krobot.config.ConfigProvider;

/**
 * Kubidibot main class
 *
 * <p>
 *     Register commands and configs, also contains the welcome message sender.<br>
 *     Fields that has the @{@link Inject} annotation are automatically filled
 *     during initialization.
 * </p>
 *
 * @author Litarvan
 * @version 1.0.0
 */
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

    /**
     * Register the commands.<br><br>
     *
     * Because of the line in {@link #init()} that calls this method, commands
     * registered here automatically have a prefix defined in config/app.json#prefix.<br>
     *
     * Note that the first commands.make argument is a path, parsed to read the
     * arguments. See {@link CommandBuilder#path(String)}
     */
    private void commands()
    {
        commands.make("pop", CommandPop.class).register();
        commands.make("drop", CommandDrop.class).register();
        commands.make("volume [value:number]", CommandVolume.class).register();
    }

    @SubscribeEvent
    public void onJoin(GuildMemberJoinEvent event)
    {
        // Displays the welcome message from the config
        PrivateChannel channel = event.getMember().getUser().openPrivateChannel().complete();
        channel.sendMessage(config.at("app.welcome")).queue();
    }

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException
    {
        Krobot.start(args[0], Kubidibot.class);
    }
}
