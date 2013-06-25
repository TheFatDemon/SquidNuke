package com.minegusta.squidnuke;

import java.util.Map;
import java.util.UUID;

import org.bukkit.*;
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

import com.google.common.collect.Maps;
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
	static Map<UUID, String> squids = Maps.newHashMap();

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
			launchNuke(player, player.getLocation(), target);
			return true;
		}
		return false;
	}

	private void launchNuke(final Player owner, final Location launch, final Location target)
	{
		launch.getWorld().playSound(launch, Sound.ENDERDRAGON_DEATH, 2F, 2F);
		target.getWorld().playSound(target, Sound.ENDERDRAGON_DEATH, 2F, 2F);
		for(int i = 6; i > 0; i--)
		{
			final int count = i - 1;
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new Runnable()
			{
				@Override
				public void run()
				{
					if(count == 0)
					{
						Squid squid = null;
						launch.getWorld().spawnEntity(launch, EntityType.SQUID);
						for(Entity entity : launch.getChunk().getEntities())
						{
							if(entity instanceof Squid && entity.getLocation().distance(launch) < 1)
							{
								squid = (Squid) entity;
								break;
							}
						}
						NukeControl control = new NukeControl(squid, launch, target);
						control.startTravel();
						squids.put(squid.getUniqueId(), owner.getName());
						owner.sendMessage(ChatColor.YELLOW + "Launch!");
					}
					else owner.sendMessage(ChatColor.GREEN + "" + count + "...");
				}
			}, (6 - i) * 20);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSquidDeath(EntityDeathEvent event)
	{
		if(!squids.containsKey(event.getEntity().getUniqueId())) return;
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(squids.get(event.getEntity().getUniqueId()));
		squids.remove(event.getEntity().getUniqueId());
		NukeControl.nuke(event.getEntity().getLocation(), true, true);
		if(offlinePlayer.isOnline()) offlinePlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The nuke detonated before it reached it's target.");
	}
}
