package com.minegusta.squidnuke;

import org.bukkit.*;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NukeControl
{
	private Squid squid;
	private Stage stage;
	private Location startPoint, checkpoint, overallTarget;

	public NukeControl(Squid squid, Location launchPoint, Location overallTarget)
	{
		this.squid = squid;
		this.stage = Stage.LAUNCH;
		this.startPoint = launchPoint;
		this.overallTarget = overallTarget;
		calculateNextCheckpoint();
	}

	public Squid getSquid()
	{
		return this.squid;
	}

	public Location getStartPoint()
	{
		return startPoint;
	}

	public Location getCheckPoint()
	{
		return checkpoint;
	}

	public Location getOverallTarget()
	{
		return overallTarget;
	}

	public Stage getStage()
	{
		return this.stage;
	}

	public void startTravel()
	{
		Bukkit.getScheduler().runTask(SquidNuke.instance, new TravelStage(this));
	}

	private void calculateNextCheckpoint()
	{
		this.stage = stage.getNext();
		switch(stage)
		{
			case ASCENT:
			{
				this.checkpoint = new Location(startPoint.getWorld(), startPoint.getX() > overallTarget.getX() ? startPoint.getX() - 10 : startPoint.getX() + 10, startPoint.getY() + 50 > 248 ? 248 : startPoint.getY() + 50, startPoint.getZ() > overallTarget.getZ() ? startPoint.getZ() - 10 : startPoint.getZ() + 10);
				break;
			}
			case TRAVEL:
			{
				this.checkpoint = new Location(overallTarget.getWorld(), checkpoint.getX() > overallTarget.getX() ? overallTarget.getX() + 10 : overallTarget.getX() - 10, checkpoint.getY() + 50 > 248 ? 248 : checkpoint.getY() + 50, checkpoint.getZ() > overallTarget.getZ() ? overallTarget.getZ() + 10 : overallTarget.getZ() - 10);
				break;
			}
			case DECENT:
			{
				this.checkpoint = new Location(overallTarget.getWorld(), overallTarget.getX(), overallTarget.getY(), overallTarget.getZ());
				break;
			}
		}
	}

	public static void nuke(final Location target, final boolean setFire, final boolean damageBlocks)
	{
		for(int i = 1; i < 75; i++)
		{
			final int k = i;
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new Runnable()
			{
				@Override
				public void run()
				{
					nukeEffects(target, 115 + (k * 6), 30 * k, (float) k / 2, setFire, damageBlocks);
				}
			}, i);
		}
	}

	private static void nukeEffects(Location target, int range, int particles, float offSetY, boolean setFire, boolean damageBlocks)
	{
		target.getWorld().createExplosion(target.getX(), target.getY() + 3 + offSetY, target.getZ(), 15F, setFire, damageBlocks);
		target.getWorld().playSound(target, Sound.AMBIENCE_CAVE, 1F, 1F);
		target.getWorld().spigot().playEffect(target, Effect.CLOUD, 1, 1, 0F, 3F + offSetY, 3F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.LAVA_POP, 1, 1, 0F, 3F + offSetY, 0F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.SMOKE, 1, 1, 0F, 3F + offSetY, 0F, 1F, particles, range);
		target.getWorld().spigot().playEffect(target, Effect.FLAME, 1, 1, 0F, 3F + offSetY, 0F + offSetY, 1F, particles, range);
	}

	public enum Stage
	{
		LAUNCH(0), ASCENT(1), TRAVEL(2), DECENT(3);

		private int order;

		private Stage(int order)
		{
			this.order = order;
		}

		public Stage getNext()
		{
			return get(this.order + 1);
		}

		private static Stage get(int order)
		{
			for(Stage stage : Stage.values())
			{
				if(stage.order == order) return stage;
			}
			return null;
		}
	}

	public static class TravelStage extends BukkitRunnable
	{
		private NukeControl control;

		public TravelStage(NukeControl control)
		{
			this.control = control;
		}

		@Override
		public void run()
		{
			if(control.getSquid().isDead()) return;
			if(control.getSquid().getLocation().distance(control.getCheckPoint()) < 4)
			{
				if(!control.getStage().equals(Stage.DECENT)) startNextTravelStage();
				else
				{
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(SquidNukeCommand.squids.get(control.getSquid().getUniqueId()));
					SquidNukeCommand.squids.remove(control.getSquid().getUniqueId());
					NukeControl.nuke(control.getSquid().getLocation(), true, true);
					if(offlinePlayer.isOnline()) offlinePlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The nuke has detonated on-target.");
				}
			}
			else
			{
				go();
				Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new TravelStage(control), 1);
			}
		}

		public void go()
		{
			if(control.getSquid().getLocation().getBlockY() > 256) control.getSquid().teleport(control.getCheckPoint());
			Vector direction = control.getCheckPoint().toVector().subtract(control.getSquid().getLocation().toVector());
			control.getSquid().setVelocity(direction);
		}

		public void startNextTravelStage()
		{
			control.calculateNextCheckpoint();
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.instance, new TravelStage(control), 1);
		}
	}
}
