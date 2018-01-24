package ca.edwardvo.betterbans;

import ca.edwardvo.betterbans.Anticheat.FindXray;
import ca.edwardvo.betterbans.Bans.Connections;
import ca.edwardvo.betterbans.Debug.CommandDebug;
import ca.edwardvo.betterbans.Inventory.BanMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterBans extends JavaPlugin {

    public static BetterBans plugin;

    @Override
    public void onEnable(){
        plugin = this;
        //Enable antixray function
        Bukkit.getServer().getPluginManager().registerEvents(new FindXray(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new Connections(), plugin);
        this.getCommand("dbg").setExecutor(new CommandDebug());
        BanMenu banMenu = new BanMenu();
        this.getCommand("ban").setExecutor(banMenu);
        banMenu.Listen();
    }


    @Override
    public void onDisable(){


    }

}
