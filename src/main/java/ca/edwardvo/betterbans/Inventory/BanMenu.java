package ca.edwardvo.betterbans.Inventory;

import ca.edwardvo.betterbans.BetterBans;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BanMenu implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (player.isOp() && args.length == 1){

                IconMenu banMenu = new IconMenu("Ban " + args[0], 18, new IconMenu.OptionClickEventHandler() {
                    @Override
                    public void onOptionClick(final IconMenu.OptionClickEvent event) {
                        IconMenu banPara = new IconMenu("" , 9, new IconMenu.OptionClickEventHandler() {
                            @Override
                            public void onOptionClick(final IconMenu.OptionClickEvent eventPara){

                                //event.getName()
                            }
                        }, BetterBans.plugin)
                                .setOption(3, new ItemStack(Material.BEDROCK, 1), "IP-Ban", "Bans the player's IP for the selected time")
                                .setOption(5, new ItemStack(Material.GLASS, 1), "Ban", String.format("Ban %s for the selected time", args[0]));
                        banPara.open(player);
                    }
                }, BetterBans.plugin)
                        .setOption(3, new ItemStack(Material.APPLE, 1), "Food", "Blah")
                        .setOption(4, new ItemStack(Material.IRON_SWORD, 1), "Kill aura", "Any PVP bans")
                        .setOption(5, new ItemStack(Material.DIAMOND_ORE, 1), "X-ray", "Any x-ray hackers")
                        .setOption(16, new ItemStack(Material.BARRIER, 1), "Perm-Ban", "Any hackers you want permanently removed")
                        .setOption(17, new ItemStack(Material.COMMAND, 1), "Custom", "Use your own ban length");
                banMenu.open(player);
            }
            else if (player.isOp() && args.length != 1){
                player.sendRawMessage("§c§l[BetterBans]§8§l §c Incorrect usage.");
            }
        }
        return true;
    }
}
