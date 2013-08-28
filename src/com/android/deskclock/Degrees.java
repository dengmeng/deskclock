package com.android.deskclock;

public class Degrees
{
	static public double PI = 3.141592653589793D;
	
  public static double degreeGap(double degree1, double degree2)
  {
    double gap = Math.abs(degree1 - degree2);
    if (gap > 180.0D)
    	gap = 360.0D - gap;
    return gap;
  }

  public static double degree2Radian(double degree)
  {
    return PI * (degree / 180.0D);
  }

  public static double radian2Degree(double radian)
  {
    return 180.0D * radian / PI;
  }
}