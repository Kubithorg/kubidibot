package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import fr.litarvan.krobot.command.CommandContext;
import fr.litarvan.krobot.util.Dialog;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

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
