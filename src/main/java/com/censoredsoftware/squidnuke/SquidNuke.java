package com.censoredsoftware.squidnuke;

import com.censoredsoftware.squidnuke.util.Configs;
import com.censoredsoftware.squidnuke.util.Randoms;
import com.google.common.collect.Maps;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class SquidNuke extends JavaPlugin implements Listener
{
	public static SquidNuke PLUGIN;
	public static boolean blockDamage, playerDamage, nukeCreeper;
	protected static Map<UUID, String> squids = Maps.newHashMap();

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		PLUGIN = this;
		blockDamage = Configs.getSettingBoolean("damage.block");
		playerDamage = Configs.getSettingBoolean("damage.player");
		nukeCreeper = Configs.getSettingBoolean("natural.nuke_creeper");

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
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void loadCommands()
	{
		getCommand("squidnuke").setExecutor(new SquidNukeCommand());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSquidDeath(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Player) return;
		if(squids.containsKey(event.getEntity().getUniqueId()))
		{
			squids.remove(event.getEntity().getUniqueId());
			NukeControl.nuke(event.getEntity().getLocation(), blockDamage, playerDamage);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreeperExplode(EntityExplodeEvent event)
	{
		if(nukeCreeper && event.getEntity().getType().equals(EntityType.CREEPER) && Randoms.generateIntRange(1, 100) > 75)
		{
			Creeper creeper = (Creeper) event.getEntity();
			creeper.setPowered(true);
			creeper.setCustomName("Nuke");
			creeper.setCustomNameVisible(true);
			NukeControl.nuke(creeper.getLocation(), blockDamage, playerDamage);
		}
	}
}
