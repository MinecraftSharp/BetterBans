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
            //If they are diamonds at low light without night vision (fullbright) start alerts
            if (lightLevel < 3 && !player.hasPotionEffect(PotionEffectType.NIGHT_VISION)){
                //Set xray to one, we add to this, and if at zero it will return one
                Bukkit.getConsoleSender().sendMessage("[BetterBans] " +
                        playerName + " has mined diamonds in the dark");

                //No of blocks xrayed. This changes.
                int xray = 1;

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
                        Bukkit.broadcastMessage("[BetterBans] " + playerName  + " has mined " +
                                player.getMetadata("xray").get(0).value() + " diamonds in the dark!");
                        //Remove data
                        player.removeMetadata("xray", BetterBans.plugin);
                        player.removeMetadata("xrayAlert", BetterBans.plugin);
                        //Creates a honeypot for the player
                        DoHoneyPot(player);
                        cancel();
                    }
                }.runTaskLater(BetterBans.plugin, 100);

                //Set the metadata if we need to cancel
                player.setMetadata("xrayAlert", new FixedMetadataValue(BetterBans.plugin, xrayAlert));
            }

            if(block.hasMetadata("honeypot")){
                Bukkit.getConsoleSender().sendMessage("[BetterBans] " +
                        playerName + " has mined a honeypot");

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
                        Bukkit.broadcastMessage("[BetterBans] " + playerName  + " has mined " +
                                player.getMetadata("honeypotMined").get(0).value() + " honeypot diamonds!");
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
        }
    }

    //This places 8 diamonds nearby the player
    //If player breaks diamonds shortly after, sends alerts
    private static void DoHoneyPot(Player player){
        Location location = player.getLocation();
        Random rand = new Random();
        //Create diamonds away from player
        double x = rand.nextInt(20) + 5;
        double y = 0;
        if (location.getBlockY() > 11){
            y -= rand.nextInt(4);
        }
        else{
            y += rand.nextInt(4);
        }
        double z = rand.nextInt(10) + 5;

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

    private static void SetHoneyPot(final Location location){

        final Material material = location.getBlock().getType();
        new BukkitRunnable() {
            @Override
            public void run() {
                location.getBlock().setType(material);

            }
        }.runTaskLater(BetterBans.plugin, 36000L);

        location.getBlock().setType(Material.DIAMOND_ORE);
        Bukkit.getConsoleSender().sendMessage("Created honeypot at x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());

        location.getBlock().setMetadata("honeypot", new FixedMetadataValue(BetterBans.plugin, "true"));
    }
}
