package com.minegusta.squidnuke.Object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.minegusta.squidnuke.SquidNuke;

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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SquidNuke.instance, new TravelStage(this), 10, 10);
	}

	private void calculateNextCheckpoint()
	{
		this.stage = stage.getNext();
		// TODO
	}

	public enum Stage
	{
		LAUNCH(0), ASCENT(1), TRAVEL(2), DECENT(3), HIT(4);

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
			if(control.getSquid().getLocation().toVector().isInSphere(control.getCheckPoint().toVector(), 4))
			{
				stopTravelStage();
				if(!control.getStage().equals(Stage.HIT)) startNextTravelStage();
			}
			else go();
		}

		public void go()
		{
			Vector startPoint = control.getSquid().getLocation().toVector();
			Vector direction = control.getCheckPoint().toVector().subtract(startPoint);
			direction.multiply(3F);
			control.getSquid().setVelocity(direction);
		}

		public void stopTravelStage()
		{
			Bukkit.broadcastMessage("Stop travel.");
			this.cancel();
		}

		public void startNextTravelStage()
		{
			control.calculateNextCheckpoint();
			Bukkit.getScheduler().scheduleSyncRepeatingTask(SquidNuke.instance, new TravelStage(control), 10, 10);
		}
	}
}
