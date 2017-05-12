package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import fr.litarvan.krobot.command.CommandContext;
import fr.litarvan.krobot.command.CommandHandler;
import fr.litarvan.krobot.command.SuppliedArgument;
import fr.litarvan.krobot.util.Dialog;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CommandVolume implements CommandHandler
{
    @Inject
    private AudioBridge bridge;

    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception
    {
        if (args.containsKey("value"))
        {
            bridge.setVolume((double) args.get("value").getAsNumber() / 100.0D);
        }

        context.sendMessage(Dialog.info("Volume", "Volume : " + (int) (bridge.getVolume() * 100D) + "%"));
    }
}
