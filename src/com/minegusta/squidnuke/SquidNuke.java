package com.minegusta.squidnuke;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.plugin.java.JavaPlugin;

import com.minegusta.squidnuke.Object.NukeControl;
import com.minegusta.squidnuke.Utility.MiscUtility;

public class SquidNuke extends JavaPlugin
{
	public static SquidNuke instance;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		loadListeners();
		loadCommands();

		instance = this;

		getLogger().info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		getLogger().info("Successfully disabled.");
	}

	public void loadListeners()
	{
		// TODO When needed.
	}

	public void loadCommands()
	{
		getCommand("squidnuke").setExecutor(new SquidNukeCommand());
	}
}

class SquidNukeCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("squidnuke") && args.length == 2 && sender instanceof Player)
		{
			Player player = (Player) sender;
			if(!MiscUtility.isInt(args[0]) || !MiscUtility.isInt(args[1]))
			{
				player.sendMessage(ChatColor.RED + "Wrong syntax.");
				return false;
			}

			// Define variables.
			int X = Integer.parseInt(args[0]), Z = Integer.parseInt(args[1]);
			Location target = new Location(player.getWorld(), X, player.getWorld().getHighestBlockYAt(X, Z), Z);

			// Check conditions.
			if(target.toVector().isInSphere(player.getLocation().toVector(), 30))
			{
				player.sendMessage(ChatColor.RED + "That location is too close.");
				return false;
			}
			else if(!target.getChunk().isLoaded())
			{
				player.sendMessage(ChatColor.RED + "That location is too far away.");
				return false;
			}
			Squid squid = null;
			player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
			for(Entity entity : player.getNearbyEntities(1, 1, 1))
			{
				if(entity instanceof Squid)
				{
					squid = (Squid) entity;
					break;
				}
			}
			NukeControl control = new NukeControl(squid, player.getLocation(), target);
			control.startTravel();
		}
		return false;
	}
}
