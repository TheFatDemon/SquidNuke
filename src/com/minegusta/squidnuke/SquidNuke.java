package com.minegusta.squidnuke;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
		Set<String> nukeKeyHolders = Sets.newHashSet("HmmmQuestionMark", "LordKuso", "scarfacehd2", "_Alex");
		if(command.getName().equalsIgnoreCase("squidnuke") && (args.length == 1 || args.length == 2) && sender instanceof Player && nukeKeyHolders.contains(sender.getName()))
		{
			Player player = (Player) sender;
			if(args.length == 1 && args[0].equals("me")) return nukePlayer(player);
			else if(args.length == 1 && Bukkit.getPlayer(args[0]) != null) return nukePlayer(Bukkit.getPlayer(args[0]));
		}
		return false;
	}

	private boolean nukePlayer(final Player player)
	{
		int count = 0;
		final Location target = player.getLocation();
		for(final Entity exists : player.getWorld().getEntities())
		{
			if(exists instanceof LivingEntity)
			{
				if(exists.equals(player) || exists.getLocation().distance(target) < 30) continue;
				Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new Runnable()
				{
					@Override
					public void run()
					{
						launchNuke(false, player, new Location(exists.getWorld(), exists.getLocation().getX(), 0.0 + exists.getLocation().getWorld().getHighestBlockYAt(exists.getLocation()), exists.getLocation().getZ()), new Location(target.getWorld(), target.getX(), 0.0 + target.getWorld().getHighestBlockYAt(target), target.getZ()));
					}
				}, count * 2);
				count++;
			}
		}
		if(count > 0)
		{
			for(Entity nearby : player.getNearbyEntities(10, 10, 10))
			{
				if(nearby instanceof Player)
				{
					Player ohshit = (Player) nearby;
					ohshit.sendMessage(ChatColor.YELLOW + "" + count + " nuclear missiles have targeted your location.");
					ohshit.sendMessage(ChatColor.YELLOW + "May God have mercy on your soul.");
				}
			}
			player.sendMessage(ChatColor.YELLOW + "" + count + " nuclear missiles have targeted your location.");
			player.sendMessage(ChatColor.YELLOW + "May God have mercy on your soul.");
		}
		return true;
	}

	private void launchNuke(final boolean alert, final Player owner, final Location launch, final Location target)
	{
		warningSiren(false, launch, target);
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
						squid.setNoDamageTicks(3);
						NukeControl control = new NukeControl(squid, launch, target);
						control.startTravel();
						squids.put(squid.getUniqueId(), owner.getName());
						owner.sendMessage(ChatColor.YELLOW + "Launch!");
					}
					else if(alert) owner.sendMessage(ChatColor.GREEN + "" + count + "...");
				}
			}, (6 - i) * 20);
		}
	}

	private static void warningSiren(final boolean alertLaunch, final Location launch, final Location target)
	{
		for(int i = 0; i < 4; i++)
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new Runnable()
			{
				@Override
				public void run()
				{
					if(alertLaunch) launch.getWorld().playSound(launch, Sound.AMBIENCE_CAVE, 2F, 2F);
					target.getWorld().playSound(target, Sound.AMBIENCE_CAVE, 2F, 2F);
				}
			}, i * 30);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSquidDeath(EntityDeathEvent event)
	{
		if(!squids.containsKey(event.getEntity().getUniqueId())) return;
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(squids.get(event.getEntity().getUniqueId()));
		squids.remove(event.getEntity().getUniqueId());
		NukeControl.nuke(event.getEntity().getLocation(), true, true);
		if(offlinePlayer.isOnline()) offlinePlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The nuke has detonated off-target.");
	}
}
