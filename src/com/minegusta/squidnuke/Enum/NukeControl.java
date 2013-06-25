package com.minegusta.squidnuke.Enum;

import org.bukkit.Location;
import org.bukkit.entity.Squid;

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
		return stage;
	}

	public void travel()
	{

	}

	private void calculateNextCheckpoint()
	{

	}

	public enum Stage
	{
		LAUNCH, ASCENT, TRAVEL, DECENT, HIT;
	}

	public static class TravelStage implements Runnable
	{
		private Squid watching;
		private Location checkpoint;

		public TravelStage(Squid squid, Location checkpoint)
		{
			this.watching = squid;
			this.checkpoint = checkpoint;
		}

		@Override
		public void run()
		{

		}

		public void stopTravelStage()
		{

		}

		public void startNextTravelStage()
		{

		}
	}
}
