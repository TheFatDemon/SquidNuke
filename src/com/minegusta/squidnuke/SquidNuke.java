package com.minegusta.squidnuke;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Sets;
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
		instance = this;

		loadListeners();
		loadCommands();

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
		// Todo.
	}

	public void loadCommands()
	{
		getCommand("squidnuke").setExecutor(new SquidNukeCommand());
	}
}

class SquidNukeCommand implements CommandExecutor, Listener
{
	static Set<UUID> squids = Sets.newHashSet();

	public SquidNukeCommand()
	{
		SquidNuke.instance.getServer().getPluginManager().registerEvents(this, SquidNuke.instance);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("squidnuke") && (args.length == 1 || args.length == 2) && sender instanceof Player)
		{
			Player player = (Player) sender;
			int X, Z;
			if(args.length == 2 && MiscUtility.isInt(args[0]) && MiscUtility.isInt(args[1]))
			{
				X = Integer.parseInt(args[0]);
				Z = Integer.parseInt(args[1]);
			}
			else if(args.length == 1 && Bukkit.getPlayer(args[0]) != null)
			{
				X = Bukkit.getPlayer(args[0]).getLocation().getBlockX();
				Z = Bukkit.getPlayer(args[0]).getLocation().getBlockZ();
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Wrong syntax.");
				return false;
			}

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
			squids.add(squid.getUniqueId());
			player.sendMessage(ChatColor.YELLOW + "Launch!");
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSquidDeath(EntityDeathEvent event)
	{
		if(!squids.contains(event.getEntity().getUniqueId())) return;
		squids.remove(event.getEntity().getUniqueId());
		NukeControl.nuke((Squid) event.getEntity(), true, true);
	}
}
