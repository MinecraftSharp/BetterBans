package ca.edwardvo.betterbans.Anticheat;

import ca.edwardvo.betterbans.BetterBans;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

//This is a work in progress function that tries to find xrayers
public class FindXray implements Listener {


    @EventHandler
    public void BlockDetectEvent(BlockBreakEvent event) {
        //Get the player
        final Player player = event.getPlayer();
        //The players name
        final String playerName = player.getDisplayName();
        //Get their location
        Location location = player.getLocation();
        //Get the block mined
        Block block = event.getBlock();
        //Run if the block is diamond ore
        if(block.getType() == Material.DIAMOND_ORE){
            //Get the light level
            int lightLevel = location.getBlock().getLightLevel();

            if(block.hasMetadata("honeypot")){
                Bukkit.getConsoleSender().sendMessage("[BetterBans] " + playerName + " has mined a honeypot");

                //No of honeypot blocks xrayed. This changes.
                int honeypotMined = 1;

                //Add previous attempts
                if (player.hasMetadata("honeypotMined")){
                    //Get their honeypot data
                    MetadataValue honeypotAmount = player.getMetadata("honeypotMined").get(0);
                    //Add the the number of honeypots mined
                    honeypotMined += (Integer) honeypotAmount.value();
                    player.removeMetadata("honeypotMined", BetterBans.plugin);
                    //Stop the previous alert created
                    ((BukkitTask)player.getMetadata("honeypotAlert").get(0).value()).cancel();
                }
                player.setMetadata("honeypotMined", new FixedMetadataValue(BetterBans.plugin, honeypotMined));

                //Task to alert admins
                BukkitTask honeypotAlert = new BukkitRunnable() {
                    @Override
                    public void run() {
                        //TODO: Change this to a permissions message. This is done for debug
                        Bukkit.broadcastMessage("§c§l[BetterBans]§8§l §c" + playerName  + " has mined " +
                                player.getMetadata("honeypotMined").get(0).value() + " honeypot diamonds!");
                        if (player.hasMetadata("honeypotTotal")){
                            int mined = player.getMetadata("honeypotTotal").get(0).asInt();
                            player.removeMetadata("honeypotTotal", BetterBans.plugin);
                            mined += player.getMetadata("honeypotMined").get(0).asInt();

                            player.setMetadata("honeypotTotal", new FixedMetadataValue(BetterBans.plugin, mined));

                            if (mined > 32){
                                //TODO: Check if auto ban is on. If yes ban them

                                Bukkit.broadcastMessage("§c§l[BetterBans]§8§l §c" + playerName  + " has reached auto-ban amount");
                            }
                        }
                        else{
                            player.setMetadata("honeypotTotal", new FixedMetadataValue(BetterBans.plugin, player.getMetadata("honeypotMined").get(0).value()));
                        }
                        //Remove data
                        player.removeMetadata("honeypotMined", BetterBans.plugin);
                        player.removeMetadata("honeypotAlert", BetterBans.plugin);
                        //Creates a honeypot
                        DoHoneyPot(player);
                        cancel();
                    }
                }.runTaskLater(BetterBans.plugin, 100);

                //Set the metadata if we need to cancel
                player.setMetadata("honeypotAlert", new FixedMetadataValue(BetterBans.plugin, honeypotAlert));
            }
            //This gets called if the player is mining at low light levels
            //This indicates fullbright
            else if (lightLevel < 3 && !player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {

                //Set xray to one, we add to this, and if at zero it will return one
                Bukkit.getConsoleSender().sendMessage(String.format("[BetterBans] %s has mined diamonds in the dark", playerName));

                //No of blocks xrayed. This changes.
                int xrayDark= 1;

                //Add previous attempts
                if (player.hasMetadata("xrayDark")){
                    //Get their xray data
                    MetadataValue xrayAmount = player.getMetadata("xrayDark").get(0);
                    //Add the the number of diamonds mined in the dark
                    xrayDark += (Integer) xrayAmount.value();
                    player.removeMetadata("xrayDark", BetterBans.plugin);
                    //Stop the previous alert created
                    ((BukkitTask)player.getMetadata("xrayDarkAlert").get(0).value()).cancel();
                }
                //Set the new player data
                player.setMetadata("xrayDark", new FixedMetadataValue(BetterBans.plugin, xrayDark));

                //Task to alert admins
                BukkitTask xrayAlert = new BukkitRunnable() {
                    @Override
                    public void run() {
                        //TODO: Change this to a permissions message. This is done for debug
                        Bukkit.broadcastMessage(String.format("§c§l[BetterBans] §8§l §c%s has mined %s diamonds in the dark!",
                                playerName, player.getMetadata("xrayDark").get(0).value()));
                        //Remove data
                        player.removeMetadata("xrayDark", BetterBans.plugin);
                        player.removeMetadata("xrayDarkAlert", BetterBans.plugin);
                        //Creates a honeypot for the player
                        DoHoneyPot(player);
                        cancel();
                    }
                }.runTaskLater(BetterBans.plugin, 100);

                //Set the metadata if we need to cancel
                player.setMetadata("xrayDarkAlert", new FixedMetadataValue(BetterBans.plugin, xrayAlert));
            }
            //This is the final method of finding out if a user has xray
            else {

                //Set xray to one, we add to this, and if at zero it will return one
                Bukkit.getConsoleSender().sendMessage(String.format("[BetterBans] %s has mined diamonds", playerName));

                //No of blocks xrayed. This changes.
                int xray= 1;

                //Add previous attempts
                if (player.hasMetadata("xray")){
                    //Get their xray data
                    MetadataValue xrayAmount = player.getMetadata("xray").get(0);
                    //Add the the number of diamonds mined in the dark
                    xray += (Integer) xrayAmount.value();
                    player.removeMetadata("xray", BetterBans.plugin);
                    //Stop the previous alert created
                    ((BukkitTask)player.getMetadata("xrayAlert").get(0).value()).cancel();
                }
                //Set the new player data
                player.setMetadata("xray", new FixedMetadataValue(BetterBans.plugin, xray));

                //Task to alert admins
                BukkitTask xrayAlert = new BukkitRunnable() {
                    @Override
                    public void run() {
                        //TODO: Change this to a permissions message. This is done for debug
                        Bukkit.broadcastMessage(String.format("§c§l[BetterBans] §8§l §c%s has mined %s diamonds!",
                                playerName, player.getMetadata("xray").get(0).value()));
                        //Remove data
                        player.removeMetadata("xray", BetterBans.plugin);
                        player.removeMetadata("xrayAlert", BetterBans.plugin);
                        //Creates a honeypot for the player
                        DoHoneyPot(player);
                        cancel();
                    }
                }.runTaskLater(BetterBans.plugin, 100);

                //Set the metadata if we need to cancel
                player.setMetadata("xrayDarkAlert", new FixedMetadataValue(BetterBans.plugin, xrayAlert));
            }
        }
    }

    //This places 8 diamonds nearby the player
    //If player breaks diamonds shortly after, sends alerts
    private static void DoHoneyPot(Player player){
        Location location = player.getLocation();
        Random rand = new Random();
        //Create diamonds away from player
        double x = rand.nextInt(20) + 5;
        //Create the y pos of the honeypot
        //Made so the player must mine away from the honeypot
        double y = 0;
        if (location.getBlockY() > 11){
            y -= (rand.nextInt(4) + 3);
        }
        else{
            y += (rand.nextInt(4) + 3);
        }
        //Create the z cord of the honeypot
        double z = rand.nextInt(20) + 5;

        //This is only debug
        Bukkit.getConsoleSender().sendMessage("Player at x: " + location.getBlockX() + " y: " +
                location.getBlockY() + " z: " + location.getBlockZ());

        //Lazy Hack
        //Creates an 2x2 vein of diamonds.
        final Location diamondVein1 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
        final Location diamondVein2 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x + 1, location.getBlockY() + y, location.getBlockZ() + z);
        final Location diamondVein3 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x + 1, location.getBlockY() + y + 1, location.getBlockZ() + z);
        final Location diamondVein4 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x + 1, location.getBlockY() + y + 1, location.getBlockZ() + z + 1);
        final Location diamondVein5 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x, location.getBlockY() + y + 1, location.getBlockZ() + z);
        final Location diamondVein6 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x, location.getBlockY() + y + 1, location.getBlockZ() + z + 1);
        final Location diamondVein7 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x + 1, location.getBlockY() + y, location.getBlockZ() + z + 1);
        final Location diamondVein8 = new Location(Bukkit.getWorlds().get(0),
                location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z + 1);

        //Creates the honeypot only if the block is stone gravel dirt
        SetHoneyPot(diamondVein1);
        SetHoneyPot(diamondVein2);
        SetHoneyPot(diamondVein3);
        SetHoneyPot(diamondVein4);
        SetHoneyPot(diamondVein5);
        SetHoneyPot(diamondVein6);
        SetHoneyPot(diamondVein7);
        SetHoneyPot(diamondVein8);
    }

    //Creates a honeypot at a set location
    //This block is a diamond ore
    private static void SetHoneyPot(final Location location){

        //Get the original block
        final Material material = location.getBlock().getType();
        //This runs code after 24000 ticks (20 ticks in a second. Block lasts for 20 min)
        new BukkitRunnable() {
            @Override
            public void run() {
                //
                location.getBlock().setType(material);

            }
        }.runTaskLater(BetterBans.plugin, 24000);

        //Create the block
        location.getBlock().setType(Material.DIAMOND_ORE);

        //Send a debug message about the honeypot
        Bukkit.getConsoleSender().sendMessage("[BetterBans] Created honeypot at x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());

        //Creates the honeypot
        location.getBlock().setMetadata("honeypot", new FixedMetadataValue(BetterBans.plugin, "true"));
    }
}
