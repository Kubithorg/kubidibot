package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import fr.litarvan.krobot.command.CommandContext;
import fr.litarvan.krobot.util.Dialog;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

public class CommandPop extends CommandBase
{
    @Inject
    private AudioBridge bridge;

    @Override
    protected void handle(@NotNull CommandContext context, AudioManager manager, VoiceChannel channel) throws Exception
    {
        Message message = context.sendMessage(Dialog.info("Connexion...", "Connexion au receveur en cours...")).get();
        manager.openAudioConnection(channel);

        bridge.setAudio(manager);
        bridge.connect();

        message.delete().queue();
        context.sendMessage(Dialog.info("Bridge établi", "Connexion réussie au receveur"));
    }
}
