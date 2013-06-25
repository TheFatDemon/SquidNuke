package com.minegusta.squidnuke.Utility;

public class MiscUtility
{
	public static boolean isInt(Object object)
	{
		try
		{
			Integer.parseInt(object.toString());
			return true;
		}
		catch(Exception ignored)
		{}
		return false;
	}
}
