package com.minegusta.squidnuke;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
		if(command.getName().equalsIgnoreCase("squidnuke") && args.length == 3 && sender instanceof Player)
		{
			Player player = (Player) sender;
			if(!MiscUtility.isInt(args[0]) || !MiscUtility.isInt(args[1]) || !MiscUtility.isInt(args[2]))
			{
				player.sendMessage(ChatColor.RED + "Wrong syntax.");
				return false;
			}

		}
		return false;
	}
}
