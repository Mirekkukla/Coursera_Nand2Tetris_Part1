public class PercolationStats {
  private final double probsArr[];
  private final int N;
  private final int T;
  private final int totalSites;

  // perform T independent computational experiments on an N-by-N grid
  public PercolationStats(int N, int T) {
    probsArr = new double[T];
    this.N = N;
    this.T = T;
    this.totalSites = N * N;

    for (int i = 0; i < T; i++) {
      Percolation percSystem = new Percolation(N);
      int openSites = 0;
      boolean sysPercolates = false;
      while (!sysPercolates) {
        openRandom(percSystem);
        openSites++;
        sysPercolates = percSystem.percolates();
      }
      probsArr[i] = ((double) openSites) / totalSites;
    }
  }

  private void openRandom(Percolation P) {
    while (true) {
      int a = StdRandom.uniform(1, N + 1);
      int b = StdRandom.uniform(1, N + 1);
      if (!P.isOpen(a, b)) {
        P.open(a, b);
        break;
      }
    }
  }

  // sample mean of percolation threshold
  public double mean() {
    double sum = 0;
    for (int i = 0; i < T; i++) {
      sum += probsArr[i];
    }
    return sum / T;
  }

  // sample standard deviation of percolation threshold
  public double stddev() {
    if (T == 1) return Double.NaN;
    double sampMean = mean();
    double sumSquares = 0;
    for (int i = 0; i < T; i++) {
      sumSquares += (Math.pow(sampMean - probsArr[i], 2));
    }
    return Math.sqrt(sumSquares / (T - 1));
  }

  // returns lower bound of the 95% confidence interval
  public double confidenceLo() {
    return mean() - 1.96 * stddev() / Math.sqrt(T);
  }

  // returns upper bound of the 95% confidence interval
  public double confidenceHi() {
    return mean() + 1.96 * stddev() / Math.sqrt(T);
  }

  // test client, described below
  public static void main(String[] args) {
    if (args.length != 2) throw new IllegalArgumentException("Need ot give 2 arges");
    PercolationStats PS =
        new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    StdOut.println("mean: " + PS.mean());
    StdOut.println("stddev: " + PS.stddev());
    StdOut.println("95% confidence interval: " + PS.confidenceLo() + ", " + PS.confidenceHi());
  }
}
