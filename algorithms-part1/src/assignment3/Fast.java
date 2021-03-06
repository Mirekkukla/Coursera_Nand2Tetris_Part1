import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class Fast {
  private Point[] points;
  private int N;

  private Fast(String filename) {
    readFile(filename);
    runAlg();
  }

  public Fast() {}

  private void readFile(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      N = Integer.parseInt(br.readLine());
      points = new Point[N];
      String line;
      int i = 0;
      while ((line = br.readLine()) != null) {
        if (line.isEmpty()) continue;
        String[] nums = line.trim().split("\\s+");
        points[i] = new Point(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
        i++;
      }
      br.close();
    } catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
  }

  private void runAlg() {
    for (int i = 0; i < N; i++) {//each point gets to be the origin once
      Point origin = points[i];
      StdDraw.setXscale(0, 32768);
      StdDraw.setYscale(0, 32768);
      origin.draw();

      //array of points, sorted according to the slope they have relative to origin
      //only consider points that are at a higher index (than the origin) in pointArr
      Point[] pointsBySlope = Arrays.copyOfRange(points, 0, N);
      Arrays.sort(pointsBySlope, origin.SLOPE_ORDER);

      //search pointsBySlope for all sets of size 3 or more that have the same slope
      //for each set x we find, we know that all the points in x U {origin}
      //lie on a straight line segment
      double lastSlope = Double.NEGATIVE_INFINITY; //slope of point with itself
      int currentSetSize = 2; //number of points with same slope
      int j;
      for (j = 0; j < pointsBySlope.length; j++) {
        Point thisPoint = pointsBySlope[j];
        double thisSlope = origin.slopeTo(thisPoint);
        if (thisSlope == Double.NEGATIVE_INFINITY) continue; //skip duplicate points
        if (thisSlope == lastSlope) {
          currentSetSize++;
        } else {
          if (currentSetSize >= 4) {
            //otherPoints doesn't include the origin point
            Point[] otherPoints = Arrays.copyOfRange(pointsBySlope, j - currentSetSize + 1, j);
            handleSegment(origin, otherPoints);
          }
          currentSetSize = 2;
          lastSlope = thisSlope;
        }
      }
      if (currentSetSize >= 4) { //in current set is at the end of the array
        Point[] otherPoints = Arrays.copyOfRange(pointsBySlope, j - currentSetSize + 1, j);
        handleSegment(origin, otherPoints);
      }
    }
  }

  private void handleSegment(Point originPoint, Point otherPoints[]) {
    Arrays.sort(otherPoints);
    //only choose use this line is the originPoint is the leftmost point
    //this guarentees each line segment gets chosen exactly once
    if (originPoint.compareTo(otherPoints[0]) <= 0) {
      Point[] line = new Point[1 + otherPoints.length];
      line[0] = originPoint;
      System.arraycopy(otherPoints, 0, line, 1, otherPoints.length);
      drawLine(line);
      printNTuple(line);
    }
  }

  private void drawLine(Point[] line) {
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    Arrays.sort(line);
    line[0].drawTo(line[line.length - 1]);
  }

  private void printNTuple(Point[] line) {
    Arrays.sort(line);
    String s = "";
    for (int i = 0; i < line.length; i++) {
      s += line[i].toString() + (i != line.length - 1 ? " -> " : "");
    }
    StdOut.println(s);
  }

   public static void main(String[] args) {
     String filename = args[0];
     Fast goFast = new Fast(filename);
   }
}