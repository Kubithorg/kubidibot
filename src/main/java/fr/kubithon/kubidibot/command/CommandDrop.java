package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.krobot.command.CommandContext;
import org.krobot.permission.UserRequires;
import org.krobot.util.Dialog;

/**
 * The Drop Command
 *
 * <p>
 *     Disconnect Kubidibot from the voice channel and close
 *     the {@link AudioBridge}. Requires the caller to be
 *     an administrator.
 * </p>
 *
 * @author Litarvan
 * @version 1.0.0
 */
@UserRequires(Permission.ADMINISTRATOR)
public class CommandDrop extends CommandBase
{
    @Inject
    private AudioBridge bridge;

    @Override
    protected void handle(@NotNull CommandContext context, AudioManager manager, VoiceChannel channel)
    {
        manager.closeAudioConnection();
        bridge.stop();

        context.sendMessage(Dialog.info("Connexion fermée", "Le bridge a été stoppé"));
    }
}
