package ca.edwardvo.betterbans.Debug;

import ca.edwardvo.betterbans.BetterBans;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDebug implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendRawMessage("[BetterBans]: Debug mode enabled");


            Bukkit.getServer().getPluginManager().registerEvents(new LightLevel(), BetterBans.plugin);

        }

        return false;
    }
}
