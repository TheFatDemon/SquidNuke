package com.censoredsoftware.squidnuke.util;

import com.censoredsoftware.squidnuke.SquidNuke;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Module to load configuration settings from any passed in PLUGIN's config.yml.
 */
public class Configs
{
	/**
	 * Constructor to create a new Configs for the given PLUGIN's <code>PLUGIN</code>.
	 * 
	 * @param PLUGIN The demigods PLUGIN the Configs attaches to.
	 * @param copyDefaults Boolean for copying the default config.yml found inside this demigods over the config file utilized by this library.
	 */
	static
	{
		Configuration config = SquidNuke.PLUGIN.getConfig();
		config.options().copyDefaults(true);
		SquidNuke.PLUGIN.saveConfig();
	}

	/**
	 * Retrieve the Integer setting for String <code>id</code>.
	 * 
	 * @param id The String key for the setting.
	 * @return Integer setting.
	 */
	public static int getSettingInt(String id)
	{
		if(SquidNuke.PLUGIN.getConfig().isInt(id)) return SquidNuke.PLUGIN.getConfig().getInt(id);
		else return -1;
	}

	/**
	 * Retrieve the String setting for String <code>id</code>.
	 * 
	 * @param id The String key for the setting.
	 * @return String setting.
	 */
	public static String getSettingString(String id)
	{
		if(SquidNuke.PLUGIN.getConfig().isString(id)) return SquidNuke.PLUGIN.getConfig().getString(id);
		else return null;
	}

	/**
	 * Retrieve the Boolean setting for String <code>id</code>.
	 * 
	 * @param id The String key for the setting.
	 * @return Boolean setting.
	 */
	public static boolean getSettingBoolean(String id)
	{
		return !SquidNuke.PLUGIN.getConfig().isBoolean(id) || SquidNuke.PLUGIN.getConfig().getBoolean(id);
	}

	/**
	 * Retrieve the Double setting for String <code>id</code>.
	 * 
	 * @param id The String key for the setting.
	 * @return Double setting.
	 */
	public static double getSettingDouble(String id)
	{
		if(SquidNuke.PLUGIN.getConfig().isDouble(id)) return SquidNuke.PLUGIN.getConfig().getDouble(id);
		else return -1;
	}

	/**
	 * Retrieve the List<String> setting for String <code>id</code>.
	 * 
	 * @param id The String key for the setting.
	 * @return List<String> setting.
	 */
	public static List<String> getSettingArrayListString(String id)
	{
		List<String> strings = new ArrayList<String>();
		if(SquidNuke.PLUGIN.getConfig().isList(id))
		{
			for(String s : SquidNuke.PLUGIN.getConfig().getStringList(id))
				strings.add(s);
			return strings;
		}
		return null;
	}
}
