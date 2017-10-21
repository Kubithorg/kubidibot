package fr.kubithon.kubidibot.command;

import com.google.inject.Inject;
import fr.kubithon.kubidibot.AudioBridge;
import java.util.Map;
import net.dv8tion.jda.core.Permission;
import org.jetbrains.annotations.NotNull;
import org.krobot.command.CommandContext;
import org.krobot.command.CommandHandler;
import org.krobot.command.SuppliedArgument;
import org.krobot.permission.UserRequires;
import org.krobot.util.Dialog;

/**
 * Volume command's implementation.
 *
 * <p>
 *     Simply set the volume of the sent audio. Only an admin can
 *     run this.
 * </p>
 *
 * @author Litarvan
 * @version 1.0.0
 */
@UserRequires(Permission.ADMINISTRATOR)
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
