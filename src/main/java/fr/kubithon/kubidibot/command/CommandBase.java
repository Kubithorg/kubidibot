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
 * The Command Base
 *
 * <p>
 *     Since all commands requires to be in a voice channel,
 *     this class check this operation, and give the channel
 *     {@link AudioManager} to the commands.
 * </p>
 *
 * @author Litarvan
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
