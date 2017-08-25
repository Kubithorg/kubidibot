package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.krobot.command.CommandContext;
import org.krobot.permission.BotRequires;
import org.krobot.permission.UserRequires;
import org.krobot.util.Dialog;

/**
 * The Pop Command
 *
 * <p>
 *     Spawn Kubidibot in the caller's voice channel, and start the
 *     {@link AudioBridge}. Only an admin can run this.
 * </p>
 *
 * @author Litarvan
 * @version 1.0.0
 */
@UserRequires(Permission.ADMINISTRATOR)
@BotRequires({Permission.VOICE_CONNECT, Permission.MESSAGE_MANAGE}) // This is the permission that the bot needs to execute its operations, a simple check that replaces a missing permissions crash
public class CommandPop extends CommandBase
{
    @Inject
    private AudioBridge bridge;

    @Override
    protected void handle(@NotNull CommandContext context, AudioManager manager, VoiceChannel channel) throws Exception
    {
        Message message = context.sendMessage(Dialog.info("Démarrage...", "Démarrage du serveur en cours...")).get();
        manager.openAudioConnection(channel);

        bridge.setAudio(manager);
        bridge.start();

        message.delete().queue();
        context.sendMessage(Dialog.info("Bridge démarré", "Serveur démarré ! En attente de connexion..."));
    }

    @Override
    protected boolean requireVoiceChannel()
    {
        return true;
    }
}
