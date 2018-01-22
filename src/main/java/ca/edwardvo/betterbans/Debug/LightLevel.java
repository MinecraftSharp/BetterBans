package ca.edwardvo.betterbans.Debug;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LightLevel implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    { // On player movement!
        Player player = event.getPlayer();
        player.sendRawMessage("[BetterBans] Debug Light Data:" + player.getLocation().getBlock().getLightLevel());
    }
}
