package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.krobot.command.CommandContext;
import org.krobot.util.Dialog;

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
