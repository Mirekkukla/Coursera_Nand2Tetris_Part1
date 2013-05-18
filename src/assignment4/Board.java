public class Board {
  private final int dim;
  private final short[][] blocks; // ok for any board <= than 180x180
  private final int hamming = -1;
  private final int manhattan = -1;
  private Stack<Board> neighbors;
  private final short blank = 0;
  private short emptyI; // i coord of empty block
  private short emptyJ; // j coord of empty block

  // construct a board from an N-by-N array of ints
  public Board(int[][] blocks) {
    dim = blocks.length;
    if (dim > 181) throw new IllegalArgumentException("Board has to be <= 180x180");
    if (dim < 2) throw new IllegalArgumentException("Board has to be >= 2x2");
    this.blocks = new short[dim][dim];
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        this.blocks[i][j] = (short) blocks[i][j];
        if (blocks[i][j] == 0) {
          emptyI = (short) i;
          emptyJ = (short) j;
        }
      }
    }
  }

  // the length of the side of the board
  public int dimension() {
    return dim;
  }

  // number of blocks out of place
  public int hamming() {
    if (hamming == -1)
      return getHammingScore();
    else
      return hamming;
  }

  // sum of Manhattan distances between blocks and goal
  public int manhattan() {
    if (manhattan == -1)
      return getManhattanScore();
    else
      return manhattan;
  }

  // is this board the goal board?
  public boolean isGoal() {
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (i == dim - 1 && j == dim - 1) continue; // don't check bottom right, should be zero
        if (blocks[i][j] != i * dim + (j + 1)) return false;
      }
    }
    return true;
  }

  // a board obtained by exchanging two adjacent blocks in the same row
  public Board twin() {
    int twinBlocks[][] = blockCopy(blocks);
    int swapRow; // make sure we're not swapping a block with a blank
    if (blocks[0][0] != 0 && blocks[0][1] != 0)
      swapRow = 0;
    else
      swapRow = 1;
    twinBlocks[swapRow][0] = blocks[swapRow][1];
    twinBlocks[swapRow][1] = blocks[swapRow][0];
    return new Board(twinBlocks);
  }

  @Override
  public boolean equals(Object y) {
    if (y == this) return true;
    if (y == null) return false;
    if (y.getClass() != this.getClass()) return false;
    if (this.dim != ((Board) y).dim) return false;
    if (this.manhattan != ((Board) y).manhattan || this.hamming != ((Board) y).hamming)
      return false;
    return this.toString().equals(y.toString());
  }

  // returns stack of all neighboring boards
  public Iterable<Board> neighbors() {
    if (neighbors == null) { //if not cached
      neighbors = new Stack<Board>();
      for (int i_shift = -1; i_shift <= 1; i_shift++) {
        for (int j_shift = -1; j_shift <= 1; j_shift++) {
          if (Math.abs(i_shift) - Math.abs(j_shift) != 0
              && isLegalLoc(emptyI + i_shift, emptyJ + j_shift)) {
            int[][] newBlocks = blockCopy(blocks);
            newBlocks[emptyI][emptyJ] = blocks[emptyI + i_shift][emptyJ + j_shift];
            newBlocks[emptyI + i_shift][emptyJ + j_shift] = blank;
            Board neighbor = new Board(newBlocks);
            neighbors.push(neighbor);
          }
        }
      }
    }
    return neighbors();
  }

  // returns a copy of the array (takes shors, return its)
  private int[][] blockCopy(short[][] oldBlocks) {
    int[][] newBlocks = new int[dim][dim];
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        newBlocks[i][j] = oldBlocks[i][j];
      }
    }
    return newBlocks;
  }

  // first line gives the dimension (length of a side)
  // then, return on line per row
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(dim + "\n");
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        s.append(String.format("%2d ", blocks[i][j]));
      }
      s.append("\n");
    }
    return s.toString();
  }

  // this returns the hamming SCORE, not the hamming priority function
  private int getHammingScore() {
    int count = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if ((blocks[i][j] != blank) && (blocks[i][j] != numFromIJCoord(i, j))) count++;
      }
    }
    return count;
  }

  // see getHamingScore comment
  private int getManhattanScore() {
    int count = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        int num = blocks[i][j];
        if (num != blank)
          count += Math.abs(iCoordFromNum(num) - i) + Math.abs(jCoordFromNum(num) - j);
      }
    }
    return count;
  }

  // num starts at one, which corresponds to top left of board
  // num increases left to right, top to bottom.
  // 1 2 3 (0,0) (0,1) (0,2)
  // 4 5 6 <-> (1,0) (1,1) (1,2)
  // 7 8 9 (2,0) (2,1) (2,2)
  private int iCoordFromNum(int num) {
    return (num - 1) / dim;
  }

  // see iCoordFromNum
  private int jCoordFromNum(int num) {
    return (num - 1) % dim;
  }

  // see iCoordFromNum
  private int numFromIJCoord(int i, int j) {
    return i * dim + (j + 1);
  }

  // where top left <-> (0,0), i is row, j is column
  private boolean isLegalLoc(int i, int j) {
    if (i < 0 || i >= dim || j < 0 || j >= dim)
      return false;
    else
      return true;
  }

  // unit tests
  public static void main(String[] args) {
    // int size = 3;
    // int blocks[][] = new int[size][size];
    // blocks[0][0] = 1;
    // blocks[0][1] = 2;
    // blocks[0][2] = 3;
    // blocks[1][0] = 4;
    // blocks[1][1] = 5;
    // blocks[1][2] = 6;
    // blocks[2][0] = 7;
    // blocks[2][1] = 8;
    // blocks[2][2] = 0;
    //
    // Board doneB = new Board(blocks);
    // if (doneB.isGoal() == false) System.out.println("FAIL");
    // if (doneB.getManhattanScore() != 0) System.out.println("FAIL");
    // if (doneB.getHammingScore() != 0) System.out.println("FAIL");
    // System.out.println("Done board");
    // System.out.println(doneB.toString());
    //
    // Board twinB = doneB.twin();
    // assert (doneB.isGoal() == false);
    // assert (doneB.getManhattanScore() == 2);
    // assert (doneB.getHammingScore() == 2);
    // System.out.println("Twin board");
    // System.out.println(twinB.toString());
    //
    // System.out.println("Neighbor boards");
    // for (Board board : doneB.neighbors()) {
    // System.out.println(board.toString());
    // }

  }
}
