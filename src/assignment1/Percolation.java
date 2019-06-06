public class Percolation {

  private final int[][] grid; // A grid where each coord is mapped ot a setID
  private final int N; // grid size = NxN
  private final int topSiteID;
  private final int bottomSiteID;

  // tracks all connected sets of open sites
  // initialize on set for each elem in grid, +1 on top + 1 on bottom
  private final WeightedQuickUnionUF sites;

  // like sites, but doesn't include bottom elem
  // used to figure out fullness (we don't want flow to come from bottom)
  private final WeightedQuickUnionUF sitesNoBottom;

  public Percolation(int N) { // create N-by-N grid, with all sites blocked
    this.N = N;
    grid = new int[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        grid[i][j] = 1;
      }
    }

    topSiteID = N * N;
    bottomSiteID = N * N + 1;

    // initialize sets
    sites = new WeightedQuickUnionUF(N * N + 2);
    sitesNoBottom = new WeightedQuickUnionUF(N * N + 1);
  }

  // open site (row i, column j) if it is not already
  public void open(int i, int j) {
    checkBounds(i, j);
    int mySetID = getSetID(i, j);
    grid[i - 1][j - 1] = 0;

    // conenct top row to extra top elem
    if (i == 1) {
      sites.union(mySetID, topSiteID);
      sitesNoBottom.union(mySetID, topSiteID);
    }
    // same for bottom row, but nor for sitesNoBottom
    if (i == N) sites.union(mySetID, bottomSiteID);

    if (inBounds(i - 1, j) && isOpen(i - 1, j)) {
      sites.union(mySetID, getSetID(i - 1, j));
      sitesNoBottom.union(mySetID, getSetID(i - 1, j));
    }
    if (inBounds(i + 1, j) && isOpen(i + 1, j)) {
      sites.union(mySetID, getSetID(i + 1, j));
      sitesNoBottom.union(mySetID, getSetID(i + 1, j));
    }
    if (inBounds(i, j + 1) && isOpen(i, j + 1)) {
      sites.union(mySetID, getSetID(i, j + 1));
      sitesNoBottom.union(mySetID, getSetID(i, j + 1));
    }
    if (inBounds(i, j - 1) && isOpen(i, j - 1)) {
      sites.union(mySetID, getSetID(i, j - 1));
      sitesNoBottom.union(mySetID, getSetID(i, j - 1));
    }
  }

  // is site (row i, column j) open?
  public boolean isOpen(int i, int j) {
    checkBounds(i, j);
    return grid[i - 1][j - 1] == 0 ? true : false;
  }

  // is site (row i, column j) full?
  public boolean isFull(int i, int j) {
    checkBounds(i, j);
    int mySetID = getSetID(i, j);
    return sitesNoBottom.connected(mySetID, topSiteID);
  }

  // does the system percolate?
  public boolean percolates() {
    return sites.connected(topSiteID, bottomSiteID);
  }

  // make sure bounds in API sense (ie 1...N) are correct, throw error if not
  private void checkBounds(int i, int j) {
    if (i < 1 || j < 1 || i > N || j > N) {
      throw new IndexOutOfBoundsException("row index out of bounds, (i,j) = " + "(" + i + "," + j
          + ")");
    }
  }

  // return true if bounds in API sense are legal, false if note
  private boolean inBounds(int i, int j) {
    if (i < 1 || j < 1 || i > N || j > N) {
      return false;
    } else {
      return true;
    }
  }

  private int getSetID(int i, int j) { // params i,j used in the api sense (ie 1...N)
    checkBounds(i, j);
    return (N * (i - 1) + j) - 1; // SetID used in the cs sense (ie 0...NxN-1)
  }

}
