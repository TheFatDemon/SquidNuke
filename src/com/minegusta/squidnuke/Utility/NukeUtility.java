package com.minegusta.squidnuke.Utility;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

public class NukeUtility
{
	public static void nuke(Plugin plugin, final Location target, final boolean setFire, final boolean damageBlocks)
	{
		for(int i = 0; i < 60; i++)
		{
			final int k = i;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					nukeEffects(target, 110 + k, 30 * k, k / 4, setFire, damageBlocks);
				}
			}, i);

		}
	}

	private static void nukeEffects(Location target, int range, int particles, int offSetY, boolean setFire, boolean damageBlocks)
	{
		target.getWorld().createExplosion(target.getX(), target.getY() + 3 + offSetY, target.getZ(), 7F, setFire, damageBlocks);
		target.getWorld().playSound(target, Sound.AMBIENCE_CAVE, 1F, 1F);
		target.getWorld().spigot().playEffect(target, Effect.CLOUD, 1, 1, 3F, 0F, 3F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.LAVA_POP, 1, 1, 0.4F, 10F, 0.4F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.SMOKE, 1, 1, 0.4F, 10F, 0.4F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.FLAME, 1, 1, 0.4F, 10F, 0.4F, 1F, particles, range);
	}
}
