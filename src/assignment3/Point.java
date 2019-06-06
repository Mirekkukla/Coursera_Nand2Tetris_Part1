import java.util.Comparator;

public class Point implements Comparable<Point> {

  // compare points by slope
  public final Comparator<Point> SLOPE_ORDER = new SlopeOrder(); // YOUR DEFINITION HERE

  private final int x; // x coordinate
  private final int y; // y coordinate

  // create the point (x, y)
  public Point(int x, int y) {
    /* DO NOT MODIFY */
    this.x = x;
    this.y = y;
  }

  private class SlopeOrder implements Comparator<Point> {
    @Override
    public int compare(Point q1, Point q2) {
      Double slope1 = slopeTo(q1);
      Double slope2 = slopeTo(q2);
      if (slope1.equals(slope2))
        return 0;
      else if (slope1 < slope2)
        return -1;
      else
        return 1;
    }
  }

  // plot this point to standard drawing
  public void draw() {
    /* DO NOT MODIFY */
    StdDraw.point(x, y);
  }

  // draw line between this point and that point to standard drawing
  public void drawTo(Point that) {
    /* DO NOT MODIFY */
    StdDraw.line(this.x, this.y, that.x, that.y);
  }

  // slope between this point and that point
  public double slopeTo(Point that) {
    if (compareTo(that) == 0) {
      return Double.NEGATIVE_INFINITY;
    }
    else if (this.x == that.x)
      return Double.POSITIVE_INFINITY;
    else if (this.y == that.y)
      return 0.0; // positive infinity for horizontal lines
    else
      return ((double) (that.y - this.y)) / (that.x - this.x);
  }

  // is this point lexicographically smaller than that one?
  // comparing y-coordinates and breaking ties by x-coordinates
  @Override
  public int compareTo(Point that) {
    if (this.y == that.y) {
      return this.x - that.x;
    } else {
      return this.y - that.y;
    }
  }

  // return string representation of this point
  @Override
  public String toString() {
    /* DO NOT MODIFY */
    return "(" + x + ", " + y + ")";
  }

  // unit test
  public static void main(String[] args) {
    Point smallest = new Point(1, 2);
    Point medium = new Point(1, 3);
    Point largest = new Point(2, 3);

    assert (smallest.compareTo(smallest) == 0);
    assert (smallest.compareTo(medium) < 0);
    assert (medium.compareTo(smallest) > 0);
    assert (medium.compareTo(largest) < 0);
    assert (largest.compareTo(medium) > 0);

    assert (smallest.slopeTo(smallest) == Double.NEGATIVE_INFINITY);
    assert (medium.slopeTo(largest) == 0.0);
    assert (smallest.slopeTo(medium) == Double.POSITIVE_INFINITY);
    assert (smallest.slopeTo(largest) == 1.0);
  }
}
