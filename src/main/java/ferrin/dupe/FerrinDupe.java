package ferrin.dupe;

import ferrin.dupe.listener.AnvilImpactListener;
import ferrin.dupe.manager.CooldownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FerrinDupe extends JavaPlugin implements CommandExecutor {

    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.cooldownManager = new CooldownManager();
        
        getServer().getPluginManager().registerEvents(new AnvilImpactListener(this), this);
        getCommand("ferrindupe").setExecutor(this);
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "FerrinDupe config reloaded!");
            return true;
        }
        return false;
    }
}