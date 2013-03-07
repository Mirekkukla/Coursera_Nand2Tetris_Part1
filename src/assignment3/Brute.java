import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class Brute {
  private Point[] pointArr;

  public Brute(String filename) {
    readFile(filename);
    checkAllTuples();
  }

  private void readFile(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader("src/assignment3/" + filename));
      int numPoints = Integer.parseInt(br.readLine());
      pointArr = new Point[numPoints];
      for (int i = 0; i < numPoints; i++) {
        String line = br.readLine();
        String[] nums = line.trim().split("\\s+");
        pointArr[i] = new Point(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
      }
      br.close();
    } catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
  }

  // check all (N choose 2) 4-tuples
  private void checkAllTuples() {
    int arrSize = pointArr.length;
    Point[] fourPoints = new Point[4];
    for (int i1 = 0; i1 < arrSize; i1++) {
      for (int i2 = i1 + 1; i2 < arrSize; i2++) {
        for (int i3 = i2 + 1; i3 < arrSize; i3++) {
          for (int i4 = i3 + 1; i4 < arrSize; i4++) {
            fourPoints[0] = pointArr[i1];
            fourPoints[1] = pointArr[i2];
            fourPoints[2] = pointArr[i3];
            fourPoints[3] = pointArr[i4];
            if (collinear(fourPoints)) {
              draw4Tuple(fourPoints);
              print4Tuple(fourPoints);
            }
          }
        }
      }
    }
  }

  private boolean collinear(Point[] fourPoints) {
    double slope1 = fourPoints[0].slopeTo(fourPoints[1]);
    if(slope1 != fourPoints[0].slopeTo(fourPoints[2])) return false;
    else if(slope1 != fourPoints[0].slopeTo(fourPoints[3])) return false;
    else return true;
  }

  private void draw4Tuple(Point[] fourPoints) {
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    Arrays.sort(fourPoints);
    fourPoints[0].drawTo(fourPoints[fourPoints.length - 1]);
    for (int i = 0; i < fourPoints.length; i++) {
      fourPoints[i].draw();
    }
  }

  private void print4Tuple(Point[] fourPoints) {
    Arrays.sort(fourPoints);
    String s = "";
    for (int i = 0; i < fourPoints.length; i++) {
      s += fourPoints[i].toString() + (i != fourPoints.length - 1 ? " -> " : "");
    }
    StdOut.println(s);
  }

  public static void main(String[] args) {
    String filename = args[0];
    Brute goBrute = new Brute(filename);
  }
}
