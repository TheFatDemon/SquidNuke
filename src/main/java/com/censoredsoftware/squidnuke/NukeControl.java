package com.censoredsoftware.squidnuke;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NukeControl
{
	private LivingEntity squid;
	private OfflinePlayer target;
	private Stage stage;
	private Location startPoint, checkpoint, targetLocation;

	public NukeControl(LivingEntity squid, Location launchPoint, OfflinePlayer target, Location targetLocation)
	{
		this.squid = squid;
		this.target = target;
		stage = Stage.LAUNCH;
		startPoint = launchPoint;
		this.targetLocation = targetLocation;
		calculateNextCheckpoint();
	}

	public LivingEntity getNuke()
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

	public OfflinePlayer getTarget()
	{
		return target;
	}

	public Stage getStage()
	{
		return this.stage;
	}

	public void startTravel()
	{
		Bukkit.getScheduler().runTask(SquidNuke.PLUGIN, new TravelStage(this));
	}

	private void calculateNextCheckpoint()
	{
		this.stage = stage.getNext();
		if(target.isOnline()) targetLocation = target.getPlayer().getLocation();
		switch(stage)
		{
			case ASCENT:
			{
				this.checkpoint = new Location(startPoint.getWorld(), startPoint.getX() > targetLocation.getX() ? startPoint.getX() - 10 : startPoint.getX() + 10, startPoint.getY() + 50 > 248 ? 248 : startPoint.getY() + 50, startPoint.getZ() > targetLocation.getZ() ? startPoint.getZ() - 10 : startPoint.getZ() + 10);
				break;
			}
			case TRAVEL:
			{
				this.checkpoint = new Location(targetLocation.getWorld(), checkpoint.getX() > targetLocation.getX() ? targetLocation.getX() + 10 : targetLocation.getX() - 10, checkpoint.getY() + 50 > 248 ? 248 : checkpoint.getY() + 50, checkpoint.getZ() > targetLocation.getZ() ? targetLocation.getZ() + 10 : targetLocation.getZ() - 10);
				break;
			}
			case DECENT:
			{
				this.checkpoint = new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
				break;
			}
		}
	}

	public static void nuke(final Location target, final boolean block, final boolean player)
	{
		for(int i = 1; i < 25; i++)
		{
			final int k = i;
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.PLUGIN, new Runnable()
			{
				@Override
				public void run()
				{
					nukeEffects(target, 115 + (k * 6), 30 * k, (float) k / 2, block, player);
				}
			}, i);
		}
	}

	private static void nukeEffects(Location target, int range, int particles, float offSetY, boolean block, boolean player)
	{

		if(!player) target.getWorld().createExplosion(target.getX(), target.getY() + 3 + offSetY, target.getZ(), 6F, block, block);
		else
		{
			target.getWorld().spigot().playEffect(target, Effect.EXPLOSION_HUGE);
			target.getWorld().playSound(target, Sound.EXPLODE, 1F, 1F);
		}
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
			if(control.getNuke().isDead()) return;
			if(control.getNuke().getLocation().distance(control.getCheckPoint()) < 4)
			{
				if(!control.getStage().equals(Stage.DECENT)) startNextTravelStage();
				else
				{
					SquidNuke.squids.remove(control.getNuke().getUniqueId());
					NukeControl.nuke(control.getNuke().getLocation(), SquidNuke.blockDamage, SquidNuke.playerDamage);
					control.getNuke().remove();
				}
			}
			else
			{
				go();
				if(!control.getStage().equals(Stage.DECENT)) control.getNuke().setNoDamageTicks(2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.PLUGIN, new TravelStage(control), 1);
			}
		}

		public void go()
		{
			if(control.getNuke().getLocation().getBlockY() > 256) control.getNuke().teleport(control.getCheckPoint());
			Vector direction = control.getCheckPoint().toVector().subtract(control.getNuke().getLocation().toVector());
			control.getNuke().setVelocity(direction);
		}

		public void startNextTravelStage()
		{
			control.calculateNextCheckpoint();
			Bukkit.getScheduler().scheduleSyncDelayedTask(SquidNuke.PLUGIN, new TravelStage(control), 1);
		}
	}
}
