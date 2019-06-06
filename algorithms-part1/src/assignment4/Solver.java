public class Solver {
  private pBoard solution = null;
  private final MinPQ<pBoard> pBoardQ = new MinPQ<pBoard>();
  private final MinPQ<pBoard> twinPBoardQ = new MinPQ<pBoard>();
  private Stack<Board> solutionStack = null;

  // find a solution to the initial board (using the A* algorithm)
  // search for solution on twin board at the same time. If the twin
  // board finds a solution, then we know one doesn't exist for the original
  public Solver(Board initial) {
    pBoardQ.insert(new pBoard(initial, null, 0));
    twinPBoardQ.insert(new pBoard(initial.twin(), null, 0));

    while (true) {
      pBoard currentPBoard = pBoardQ.delMin();
      pBoard currentTwinBoard = twinPBoardQ.delMin();

      if (currentPBoard.board.isGoal()) {
        solution = currentPBoard;
        break;
      } else if (currentTwinBoard.board.isGoal()) { //If the twin is solvable, the original isn't
        break;
      }

      insertNeighbors(pBoardQ, currentPBoard);
      insertNeighbors(twinPBoardQ, currentTwinBoard);
    }
  }

  // insert all neighbors of pboard into the priority queue, except for
  // the neighbor that was the parent of pboard
  private void insertNeighbors(MinPQ<pBoard> queue, pBoard pboard) {
    for (Board neighborBoard : pboard.board.neighbors()) {
      if (pboard.parent != null && neighborBoard.equals(pboard.parent.board)) {
        continue;
      }
      queue.insert(new pBoard(neighborBoard, pboard, pboard.moves + 1));
    }
  }

  // a Board with a pointer to its ancestor in the priority queue, as well as a priority
  private class pBoard implements Comparable<pBoard> {
    private final Board board;
    private final pBoard parent;
    private final int priority;
    private final int moves;

    private pBoard(Board board, pBoard parent, int moves) {
      this.board = board;
      this.parent = parent;
      this.moves = moves;
      this.priority = board.manhattan() + moves;
    }

    @Override
    public int compareTo(pBoard other) {
      return this.priority - other.priority;
    }
  }

  public boolean isSolvable() {
    if (solution == null) return false;
    else return true;
  }

  public int moves() { // min number of moves to solve initial board; -1 if no solution
    if (solution == null) return -1;
    else return solution.moves;
  }

  // sequence of boards in a shortest solution; null if no solution
  public Iterable<Board> solution() {
    if (solution == null) return null;
    if (solutionStack == null) {
      solutionStack = new Stack<Board>();
      solutionStack.push(solution.board);
      pBoard parentPBoard = solution.parent;
      while (parentPBoard != null) {
        solutionStack.push(parentPBoard.board);
        parentPBoard = parentPBoard.parent;
      }
      return solutionStack;
    } else {
      return solutionStack;
    }
  }

  public static void main(String[] args) { // solve a slider puzzle (given below)
    // create initial board from file
    In in = new In(args[0]);
    int N = in.readInt();
    int[][] blocks = new int[N][N];
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    System.out.println("We're about to solve:");
    System.out.println(initial.toString());

    // solve the puzzle
    long startTime = System.currentTimeMillis();
    Solver solver = new Solver(initial);
    long finishTime = System.currentTimeMillis();

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        StdOut.println(board);
    }
    System.out.println("That took: " + (finishTime - startTime) * 1.0 / 1000 + " s");
  }
}
