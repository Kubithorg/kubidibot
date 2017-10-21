package fr.kubithon.kubidibot.command;

import java.util.Map;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.krobot.command.CommandContext;
import org.krobot.command.CommandHandler;
import org.krobot.command.SuppliedArgument;
import org.krobot.util.Dialog;

/**
 * Abstraction layer for the commands.
 *
 * <p>
 *     This class's {@link #handle(CommandContext, Map)} implementation insures
 *     that the user that runs the command is in a voice channel. Also provides an {@link AudioManager}
 *     to the {@link #handle(CommandContext, AudioManager, VoiceChannel)} implementation.
 * </p>
 *
 * @author Litarvan
 * @author Oscar Davis (olsdavis)
 * @version 1.0.0
 */
public abstract class CommandBase implements CommandHandler
{
    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception
    {
        GuildVoiceState voice = context.getMember().getVoiceState();

        if (!voice.inVoiceChannel() && requireVoiceChannel())
        {
            context.sendMessage(Dialog.warn("Erreur", "Vous n'êtes pas dans un channel vocal"));
            return;
        }

        handle(context, context.getGuild().getAudioManager(), voice.getChannel());
    }

    protected abstract void handle(@NotNull CommandContext context, AudioManager manager, VoiceChannel channel) throws Exception;

    protected boolean requireVoiceChannel()
    {
        return false;
    }
}
