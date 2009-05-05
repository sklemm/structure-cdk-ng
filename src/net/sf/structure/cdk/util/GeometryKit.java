/* =====================================================
 * StructureCDK : A 2D Molecular Visualization Framework
 * =====================================================
 *
 * Project Info:  http://structure.sourceforge.net
 *
 * Copyright (C) 2004-2006 Richard L. Apodaca
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Created Mar 20, 2004
 */
 
package net.sf.structure.cdk.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

/**
 * <code>GeometryKit is a collection of static utility methods for geometrical
 * calculation.
 * 
 * @author Richard Apodaca
 */
public final class GeometryKit
{
  /**
   * This class should not be instantiated.
   */
  private GeometryKit()
  {
    super();
  }

  /**
   * Returns the unsigned distance between the specified points.
   *
   * @param p1 the first point
   * @param p2 the second point
   *
   * @return the distance
   */
  public static double getDistance(Point2D p1, Point2D p2)
  {
    double result = getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());

    return result;
  }

  /**
   * Returns the unsigned distance between two specified points.
   *
   * @param x1 the x-coordinate of the first point
   * @param y1 the y-coordinate of the second point
   * @param x2 the x-coordinate of the second point
   * @param y2 the y-coordinate of the second point
   *
   * @return the unsigned distance
   */
  public static double getDistance(double x1, double y1, double x2, double y2)
  {
    double result = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

    return result;
  }

  /**
   * Returns the unsigned length of the specified line.
   *
   * @param line the line to measure
   *
   * @return the unsigned length of the line
   */
  public static double getLength(Line2D line)
  {
    double result =
        getDistance(line.getX1(), line.getY1(), line.getX2(), line.getY2());

    return result;
  }

  /**
   * Translates the specified line along its perpendicular by the specified
   * signed distance, resulting in a line that is parallel to the original.
   *
   * @param line the line to be translated
   * @param distance the signed translation distance
   */
  public static void translate(Line2D line, double distance)
  {
    double length = getLength(line);
    double x = line.getX2() - line.getX1();
    double y = line.getY2() - line.getY1();
    double dx = (distance * y) / length;
    double dy = -(distance * x) / length;

    line.setLine(line.getX1() + dx, line.getY1() + dy,
        line.getX2() + dx, line.getY2() + dy);
  }

  public static boolean pointAbove(Line2D line, Point2D point)
  {
    return (getDistance(line, point) < 0);
  }
  
  public static boolean pointAbove(Line2D line, double x, double y)
  {
    return (getDistance(line, x, y) < 0);
  }

  /**
   * Returns the signed distance between the specified point and the specified
   * line along the perpendicular to the line. If the point is "above" the line,
   * a positive value is returned. A negative value is returned if the point is
   * "below" the line.
   *
   * @param line the line
   * @param point the point
   *
   * @return the signed distance between the line and the point
   */
  public static double getDistance(Line2D line, Point2D point)
  {
    double angle =
        getAngle(line.getX1(), line.getY1(), point.getX(), point.getY()) -
        getAngle(line);

    // find the line-point distance
    double distance =
        getDistance(line.getX1(), line.getY1(), point.getX(), point.getY());

    double y = distance * Math.sin(angle);

    return y;
  }
  
  public static double getDistance(Line2D line, double xIn, double yIn)
  {
    double angle =
      getAngle(line.getX1(), line.getY1(), xIn, yIn) -
      getAngle(line);

  // find the line-point distance
  double distance =
      getDistance(line.getX1(), line.getY1(), xIn, yIn);

  double y = distance * Math.sin(angle);

  return y;
  }

  /**
   * Returns the angle, in radians, between the specified line and the x-axis.
   * The angle increases in the clockwise direction and varies from 0 to 2pi.
   *
   * @param x1 the x-coordinate of the first point
   * @param y1 the y-coordinate of the first point
   * @param x2 the x-coordinate of the second point
   * @param y2 the x-coordinate of the second point
   *
   * @return the angle in radians, from 0 to 2pi
   */
  public static double getAngle(double x1, double y1, double x2, double y2)
  {
    double x = x2 - x1;
    double y = y2 - y1;
    double length = getDistance(x1, y1, x2, y2);
    double simpleAngle = Math.acos(Math.abs(x) / length);
    double angle = 0D;

    if (x >= 0.0D && y >= 0.0D)
    {
      angle = simpleAngle;
    }

    else if (x >= 0.0 && y <= 0.0)
    {
      angle = Math.toRadians(360D) - simpleAngle;
    }

    else if (x <= 0.0D && y <= 0.0D)
    {
      angle = Math.toRadians(180D) + simpleAngle;
    }

    else if (x <= 0.0D && y >= 0.0D)
    {
      angle = Math.toRadians(180D) - simpleAngle;
    }

    return angle;
  }

  /**
   * Returns the angle, in radians, between the specified line and the x-axis.
   * The angle increases in the clockwise direction and varies from 0 to 2pi.
   *
   * @param line the line
   *
   * @return the angle in radians, from 0 to 2pi
   */
  public static double getAngle(Line2D line)
  {
    return getAngle(line.getX1(), line.getY1(), line.getX2(), line.getY2());
  }
  
  public static void shortenToCenter(Line2D line, double percent)
  {
    double newX1 = line.getX1() + 0.5 * percent * (line.getX2() - line.getX1());
    double newY1 = line.getY1() + 0.5 * percent * (line.getY2() - line.getY1());
    double newX2 = line.getX2() + 0.5 * percent * (line.getX1() - line.getX2());
    double newY2 = line.getY2() + 0.5 * percent * (line.getY1() - line.getY2());
    //double newX2 = line.getX1() + 0.5 * percent * (line.getX2() - line.getX1());
    //double newY2 = line.getY1() + 0.5 * percent * (line.getY2() - line.getY1();
    
    line.setLine(newX1, newY1, newX2, newY2);
  }

  public static void trimLine(Line2D line, Rectangle2D bounds)
  {
    Point2D source = null;
    Point2D target = null;
    boolean reverse = false;

    if (bounds.contains(line.getP1()) && bounds.contains(line.getP2()))
    {
      return;
    }

    if (bounds.contains(line.getP1()))
    {
      source = line.getP1();
      target = line.getP2();
    }

    else if (bounds.contains(line.getP2()))
    {
      source = line.getP2();
      target = line.getP1();
      reverse = true;
    }

    else
    {
      return;
    }

    double x1 = source.getX();
    double y1 = source.getY();
    double x2 = target.getX();
    double y2 = target.getY();

    double dx = x2 - x1; // x-distance between source and target
    double dy = y1 - y2; // y-distance between source and target

    double angle = getAngle(x1, y1, x2, y2);

    double left = bounds.getX();
    double right = left + bounds.getWidth();
    double top = bounds.getY();
    double bottom = top + bounds.getHeight();

    double angleBound1 = getAngle(x1, y1, right, top);
    double angleBound2 = getAngle(x1, y1, right, bottom);
    double angleBound3 = getAngle(x1, y1, left, bottom);
    double angleBound4 = getAngle(x1, y1, left, top);

    double x = 0.0;
    double y = 0.0;

    // right edge of bounds
    if ((angle >= angleBound1) || (angle <= angleBound2))
    {
      x = right;
      y = y1 - (x - x1) * (dy / dx);
    }

    // bottom edge of bounds
    else if ((angle > angleBound2) && (angle < angleBound3))
    {
      y = bottom;
      x = x1 + (y1 - y) * (dx / dy);
    }

    // left edge of bounds
    else if ((angle >= angleBound3) && (angle <= angleBound4))
    {
      x = left;
      y = y1 - (x - x1) * (dy / dx);
    }

    // top edge of bounds
    else if ((angle > angleBound4) && (angle < angleBound1))
    {
      y = top;
      x = x1 + (y1 - y) * (dx / dy);
    }

    if (reverse)
    {
      line.setLine(x2, y2, x, y);
    }

    else
    {
      line.setLine(x, y, x2, y2);
    }
  }
}
