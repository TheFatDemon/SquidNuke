package com.censoredsoftware.squidnuke;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;
import java.util.UUID;

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
		if(command.getName().equalsIgnoreCase("squidnuke") && (args.length == 1 || args.length == 2) && sender instanceof Player && sender.hasPermission("squidnuke.nuke"))
		{
			String target = args[0];
			EntityType nukeType;
			if(args.length == 1) nukeType = EntityType.SQUID;
			else
			{
				try
				{
					nukeType = EntityType.valueOf(args[1].toUpperCase());
				}
				catch(Throwable thrown)
				{
					nukeType = EntityType.SQUID;
				}
			}
			Player player = (Player) sender;
			if(target.equalsIgnoreCase("me")) return nukePlayer(player, nukeType);
			else if(target.equals("*"))
			{
				for(Player online : Bukkit.getOnlinePlayers())
					nukePlayer(online, nukeType);
				return true;
			}
			else if(Bukkit.getPlayer(target) != null) return nukePlayer(Bukkit.getPlayer(args[0]), nukeType);
		}
		return false;
	}

	private boolean nukePlayer(final Player player, final EntityType type)
	{
		int count = 0;
		final Location target = player.getLocation();
		for(final Entity exists : player.getWorld().getEntities())
		{
			if(exists instanceof LivingEntity)
			{
				if(exists.equals(player) || exists.getLocation().distance(target) < 30 || exists.getLocation().distance(target) > 100) continue;
				Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new Runnable()
				{
					@Override
					public void run()
					{
						launchNuke(false, player, type, new Location(exists.getWorld(), exists.getLocation().getX(), 0.0 + exists.getLocation().getWorld().getHighestBlockYAt(exists.getLocation()), exists.getLocation().getZ()), new Location(target.getWorld(), target.getX(), 0.0 + target.getWorld().getHighestBlockYAt(target), target.getZ()));
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

	private void launchNuke(final boolean alert, final Player owner, final EntityType type, final Location launch, final Location target)
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

						LivingEntity squid = (LivingEntity) launch.getWorld().spawnEntity(launch, type);
						squid.setNoDamageTicks(3);
						NukeControl control = new NukeControl(squid, launch, target);
						control.startTravel();
						squids.put(squid.getUniqueId(), owner.getName());
						owner.sendMessage(ChatColor.DARK_RED + "☣");
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
		squids.remove(event.getEntity().getUniqueId());
		NukeControl.nuke(event.getEntity().getLocation(), true, true);
	}
}
