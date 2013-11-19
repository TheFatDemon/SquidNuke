package com.censoredsoftware.squidnuke;

import org.bukkit.plugin.java.JavaPlugin;

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