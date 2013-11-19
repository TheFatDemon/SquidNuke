package com.censoredsoftware.squidnuke;

import com.censoredsoftware.squidnuke.util.Configs;
import com.google.common.collect.Maps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class SquidNuke extends JavaPlugin implements Listener
{
	public static SquidNuke PLUGIN;
	public static boolean blockDamage, playerDamage;
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSquidDeath(EntityDeathEvent event)
	{
		if(!squids.containsKey(event.getEntity().getUniqueId())) return;
		squids.remove(event.getEntity().getUniqueId());
		NukeControl.nuke(event.getEntity().getLocation(), blockDamage, playerDamage);
	}
}