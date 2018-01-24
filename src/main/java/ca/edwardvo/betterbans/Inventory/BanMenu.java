package ca.edwardvo.betterbans.Inventory;

import ca.edwardvo.betterbans.BetterBans;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;

public class BanMenu implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (player.isOp() && args.length == 1){

                IconMenu banMenu = new IconMenu("§cBan: " + args[0], 18, new IconMenu.OptionClickEventHandler() {
                    @Override
                    public void onOptionClick(final IconMenu.OptionClickEvent event) {

                        //This is used to get the mode the ban the player with
                        //TODO: I think this should be a switch statement.
                        BukkitTask banMode = new BukkitRunnable() {
                            @Override
                            public void run() {

                                if (event.getName().equalsIgnoreCase("Custom (hours)")){

                                    getBanLength(player , "Hours", args[0]);
                                }
                                else if(event.getName().equalsIgnoreCase("Custom (days)")){

                                    getBanLength(player , "Days", args[0]);
                                }
                                else if(event.getName().equalsIgnoreCase("Custom (months)")){

                                    getBanLength(player , "Months", args[0]);
                                }
                                else if (event.getName().equalsIgnoreCase("Cancel")){
                                    //I don't want this to do anything. Beautiful
                                }
                                else{

                                    IconMenu banPara = new IconMenu("§cBan type" , 9, new IconMenu.OptionClickEventHandler() {
                                        @Override
                                        public void onOptionClick(final IconMenu.OptionClickEvent eventPara){

                                            Bukkit.getPlayer(args[0]).kickPlayer(String.format("You have been %sned for %s",
                                                    eventPara.getName().toLowerCase(), event.getName().toLowerCase()));
                                            cancel();
                                        }
                                    }, BetterBans.plugin)
                                            .setOption(3, new ItemStack(Material.BEDROCK, 1), "IP-Ban", "Bans the player's IP for the selected time")
                                            .setOption(5, new ItemStack(Material.GLASS, 1), "Ban", String.format("Ban %s for the selected time", args[0]));

                                    banPara.open(player);
                                    cancel();
                                }
                            }
                        }.runTaskLater(BetterBans.plugin, 2);

                    }
                }, BetterBans.plugin)
                        //This below creates all of the options inside of the inventory

                        //First row
                        .setOption(0, new ItemStack(Material.SUGAR, 1), "Speed", "2Fast4U")
                        .setOption(1, new ItemStack(Material.DIAMOND_BOOTS, 1), "Jesus", "Just casually wakin' on water")
                        .setOption(2, new ItemStack(Material.BOOK_AND_QUILL, 1), "Spam", "I'M TRYING TO READ HERE")
                        .setOption(3, new ItemStack(Material.ELYTRA, 1), "Fly", "I AM A BIRD AND A PLANE")
                        .setOption(4, new ItemStack(Material.SUGAR, 1), "Speed", "2Fast4U")
                        .setOption(5, new ItemStack(Material.STICK, 1), "AntiKnockback", "I'm fat like a tank. I don't move")
                        .setOption(6, new ItemStack(Material.IRON_SWORD, 1), "Aura", "Nothing shall stand in my way")
                        .setOption(7, new ItemStack(Material.DIAMOND_BLOCK, 64), "X-ray", "I'm rich now!")
                        .setOption(8, new ItemStack(Material.TNT, 1), "Griefing", "Goodbye lil house")
                        //Second row
                        .setOption(9, new ItemStack(Material.ENDER_CHEST, 1), "Exploits", "Duping in progress... §kI had 1 now I have 2")
                        .setOption(12, new ItemStack(Material.COMMAND_MINECART, 1), "Hacking", "THIS IS MY SEVER NOW §k123")
                        .setOption(13, new ItemStack(Material.COMMAND_CHAIN, 1), "Custom (hours)", "Ooo I can pick")
                        .setOption(14, new ItemStack(Material.COMMAND_REPEATING, 1), "Custom (days)", "Ooo I can pick")
                        .setOption(15, new ItemStack(Material.COMMAND, 1), "Custom (months)", "Ooo I can pick... now suffer")
                        .setOption(16, new ItemStack(Material.BEDROCK, 1), "Forever", "Bye bye! Have a good time :)")
                        .setOption(17, new ItemStack(Material.BARRIER, 1), "Cancel", "Noooo, they were good boi");
                banMenu.open(player);
            }
            else if ((player.isOp() || player.hasPermission("")) && args.length != 1){
                player.sendRawMessage("§c§l[BetterBans]§8§l §c Incorrect usage.");
                player.sendRawMessage("/ban <player name>");
                //Do not return false
            }
            else{

                //TODO: have same colour
                player.sendRawMessage("You do not have permission to use this command!");
            }
        }
        else if (sender instanceof ConsoleCommandSender){


        }
        return true;
    }

    //same as string.IsNullOrEmpty() in c#
    public boolean isBlank(String value) {
        return (value == null || value.equals("") || value.equals("null") || value.trim().equals(""));
    }

    //Checks if a string is a number and can safely be converted
    public boolean isOnlyNumber(String value) {
        boolean ret = false;
        if (!isBlank(value)) {
            ret = value.matches("^[0-9]+$");
        }
        return ret;
    }

    //TODO: A lot of the code is repeated. Make code shorter (easier to read)
    //This gets called when the plugin is loaded
    public void Listen(){

        //Packet adapter for when listening
        PacketListener pListen = new PacketAdapter(BetterBans.plugin, PacketType.Play.Client.UPDATE_SIGN) {

            //Function for when it runs
            @Override
            public void onPacketReceiving(PacketEvent event) {
                //Get the packet
                PacketContainer packet = event.getPacket();

                //Check if it is the UPDATE_SIGN packet even though it should
                if (packet.getType() == PacketType.Play.Client.UPDATE_SIGN){

                    //Get the player
                    Player signPlayer = event.getPlayer();
                    //Check to make sure it the player actually is the correct one
                    //TODO: Make sure that the player has perms to ban a player, theoretically they should
                    if (signPlayer.hasMetadata("banPlayer")){

                        //Read the text on the sign
                        final String[] sign = packet.getStringArrays().read(0);
                        //Lazy hack. Get the last object so if player cancel we don't ban them instead of the last (newest) one
                        //Needs to be tested
                        final String banPlayer = signPlayer.getMetadata("banPlayer").get(signPlayer.getMetadata("banPlayer").size() - 1).asString();

                        signPlayer.sendMessage("§c§l[BetterBans]§8§l §c Banning: " + banPlayer);

                        //Weird thingy. This gets called like 5 times and it annoys me
                        if (sign[2].isEmpty()) {
                            return;
                        }

                        signPlayer.removeMetadata("banPlayer", BetterBans.plugin);

                        //Check the custom length time
                        if (sign[0].equalsIgnoreCase("§c§lHours")){
                            //This checks if the 3rd line of the sign has a number
                            if (isOnlyNumber(sign[2].replaceAll("[^\\d.]", ""))){

                                IconMenu banPara = new IconMenu("§cHour ban type" , 9, new IconMenu.OptionClickEventHandler() {
                                    @Override
                                    public void onOptionClick(final IconMenu.OptionClickEvent eventPara){


                                        Bukkit.getPlayer(banPlayer).
                                                kickPlayer(String.format("You have been %sned for %s hours",
                                                        eventPara.getName().toLowerCase(),
                                                        sign[2].replaceAll("[^\\d.]", "")));
                                    }
                                }, BetterBans.plugin)
                                        .setOption(3, new ItemStack(Material.BEDROCK, 1),
                                                "IP-Ban",
                                                "Bans the player's IP for the selected time")
                                        .setOption(5, new ItemStack(Material.GLASS, 1),
                                                "Ban",
                                                String.format("Ban %s for the selected time",
                                                        banPlayer));

                                banPara.open(signPlayer);
                            }
                            else {

                                //The player has use some weird format or edited the wrong line
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c Incorrect usage.");
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c A number was not found in the 3rd line.");
                            }
                        }
                        else if (sign[0].equalsIgnoreCase("§c§lDays")){
                            //This checks if the 3rd line of the sign has a number
                            if (isOnlyNumber(sign[2].replaceAll("[^\\d.]", ""))){

                                IconMenu banPara = new IconMenu("§cDay ban type" , 9, new IconMenu.OptionClickEventHandler() {
                                    @Override
                                    public void onOptionClick(final IconMenu.OptionClickEvent eventPara){


                                        Bukkit.getPlayer(banPlayer).
                                                kickPlayer(String.format("You have been %sned for %s days",
                                                        eventPara.getName().toLowerCase(),
                                                        sign[2].replaceAll("[^\\d.]", "")));
                                    }
                                }, BetterBans.plugin)
                                        .setOption(3, new ItemStack(Material.BEDROCK, 1),
                                                "IP-Ban",
                                                "Bans the player's IP for the selected time")
                                        .setOption(5, new ItemStack(Material.GLASS, 1),
                                                "Ban",
                                                String.format("Ban %s for the selected time",
                                                        banPlayer));

                                banPara.open(signPlayer);
                            }
                            else {

                                //Tell player they input the wrong data
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c Incorrect usage.");
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c A number was not found in the 3rd line.");
                            }
                        }
                        else if (sign[0].equalsIgnoreCase("§c§lMonths")){
                            //This checks if the 3rd line of the sign has a number
                            if (isOnlyNumber(sign[2].replaceAll("[^\\d.]", ""))){


                                IconMenu banPara = new IconMenu("§cMonth ban type" , 9, new IconMenu.OptionClickEventHandler() {
                                    @Override
                                    public void onOptionClick(final IconMenu.OptionClickEvent eventPara){


                                        Bukkit.getPlayer(banPlayer).
                                                kickPlayer(String.format("You have been %sned for %s months",
                                                        eventPara.getName().toLowerCase(),
                                                        sign[2].replaceAll("[^\\d.]", "")));
                                    }
                                }, BetterBans.plugin)
                                        .setOption(3, new ItemStack(Material.BEDROCK, 1),
                                                "IP-Ban",
                                                "Bans the player's IP for the selected time")
                                        .setOption(5, new ItemStack(Material.GLASS, 1),
                                                "Ban",
                                                String.format("Ban %s for the selected time",
                                                        banPlayer));

                                banPara.open(signPlayer);
                            }
                            else {

                                //Tell player they input the wrong data
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c Incorrect usage.");
                                signPlayer.sendRawMessage("§c§l[BetterBans]§8§l §c A number was not found in the 3rd line.");
                            }
                        }
                    }
                }
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(pListen);
    }

    public void getBanLength(Player player, String mode, String banPlayerName){

        //Get the player's position
        Location location = player.getLocation();

        //This creates a position with no y value (height)
        BlockPosition playerPositionXZ = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());

        //Packet to send fake sign to the player
        PacketContainer signFake = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);

        //Packet to edit test on sign
        PacketContainer signText = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);

        //Packet to open sign
        PacketContainer signOpen = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        //Give the fake sign a position
        signFake.getBlockPositionModifier().write(0, playerPositionXZ);

        //Specify the sign type
        signFake.getBlockData().write(0, WrappedBlockData.createData(Material.SIGN_POST));

        //Give the sign to edit the fake sing's position
        signOpen.getBlockPositionModifier().write(0, playerPositionXZ);

        //Create NBT data for the sign's text
        NbtCompound signNbt = (NbtCompound) signText.getNbtModifier().read(0);

        //Text on the sign
        signNbt.put("Text1", String.format("{\"text\":\"§c§l%s\"}", mode));
        signNbt.put("Text2", "{\"text\":\"vvvvvvvvvvvvvvvv\"}");
        signNbt.put("Text3", "{\"text\":\"\"}");
        signNbt.put("Text4", "{\"text\":\"^^^^^^^^^^^^^^^\"}");


        //Create the text
        signText.getBlockPositionModifier().write(0, playerPositionXZ);
        signText.getIntegers().write(0, 9);

        //Write the NBT data to the text of the sign
        signText.getNbtModifier().write(0, signNbt);


        try {

            //Create a metadata stating the player is trying to ban someone and who they are trying to ban
            player.setMetadata("banPlayer", new FixedMetadataValue(BetterBans.plugin, banPlayerName));

            //Send the packets to create the sign, put the text on the sign then open the sign editor
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, signFake);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, signText);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, signOpen);

        } catch (InvocationTargetException e) {

            //Something happened
            e.printStackTrace();
        }
    }
}
