import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;


public class Fast {
  private Point[] pointArr;
  private int N;

  public Fast(String filename) {
    readFile(filename);
    runAlg();
  }

  private void readFile(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader("src/assignment3/" + filename));
      N = Integer.parseInt(br.readLine());
      pointArr = new Point[N];
      for (int i = 0; i < N; i++) {
        String line = br.readLine();
        String[] nums = line.trim().split("\\s+");
        pointArr[i] = new Point(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
      }
      br.close();
    } catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
  }

  private void runAlg() {
    //each point gets to be the origin
    for (int i = 0; i < N; i++) {
      Point origin = pointArr[i];

      //make a duplicate array for sorting
      int pointsOnRight = N - i - 1 ; //only compare to points to the right
      Point[] copyArr = new Point[pointsOnRight];
      for (int j = 0; j + i + 1 < N; j++) {
        copyArr[j] = pointArr[j + i + 1];
      }

      Arrays.sort(copyArr, origin.SLOPE_ORDER);
      double lastSlope = Double.NEGATIVE_INFINITY; //slope of point with itself
      int count = 2; //number of consecutive colinear points
      //walk through array, see how many duplicates there are
      int j;
      for (j = 0; j < copyArr.length; j++) {
        Point thisPoint = copyArr[j];
        double thisSlope = origin.slopeTo(thisPoint); //skip duplicate points
        if (thisSlope == Double.NEGATIVE_INFINITY) continue;
        if (thisSlope == lastSlope) {
          count++;
        } else {
          if (count >= 4) handleLine(origin, j - count + 1, j - 1, copyArr);
          count = 2;
          lastSlope = thisSlope;
        }
      }
      if (count >= 4) { //in case line segment hits end of array
        handleLine(origin, j - count + 1, j - 1, copyArr);
      }
    }
  }

  private void handleLine(Point originPoint, int startIndex, int endIndex, Point arr[]) {
    int pointsInLine = endIndex - startIndex + 1;
    Point[] line = new Point[pointsInLine];
    line[0] = originPoint; //the first of the points in line is the origin point
    for (int k = 0; k < pointsInLine - 1; k++) {
      line[k + 1] = arr[startIndex + k];
    }
    drawNTuple(line);
    printNTuple(line);
  }

  private void drawNTuple(Point[] line) {
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    Arrays.sort(line);
    //line[0].drawTo(line[line.length - 1]);
    for (int i = 0; i < line.length; i++) {
      line[i].draw();
      if (i < line.length - 1) {//delete later
        line[i].drawTo(line[i+1]);
      }
    }
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