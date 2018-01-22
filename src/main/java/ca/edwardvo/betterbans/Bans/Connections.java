package ca.edwardvo.betterbans.Bans;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Connections implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (event.getPlayer().getDisplayName().equalsIgnoreCase("eddy5641")){
            event.getPlayer().sendRawMessage("Pst... We are using BetterBans");

            Bukkit.broadcastMessage("§c§l[BetterBans] §8§l §c§oeddy5641 §7just joined the game :D");
        }
    }

}
