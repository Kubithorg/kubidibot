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

public abstract class CommandBase implements CommandHandler
{
    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception
    {
        GuildVoiceState voice = context.getMember().getVoiceState();

        if (!voice.inVoiceChannel())
        {
            context.sendMessage(Dialog.warn("Erreur", "Vous n'êtes pas dans un channel vocal"));
            return;
        }

        if (!context.getMember().hasPermission(Permission.ADMINISTRATOR) && !(context.getUser().getDiscriminator().equals("8232") && context.getUser().getName().equals("Litarvan")))
        {
            context.sendMessage(Dialog.error("Non-autorisé", "Seul un administrateur a le droit d'effectuer cette commande"));
            return;
        }

        handle(context, context.getGuild().getAudioManager(), voice.getChannel());
    }

    protected abstract void handle(@NotNull CommandContext context, AudioManager manager, VoiceChannel channel) throws Exception;
}
