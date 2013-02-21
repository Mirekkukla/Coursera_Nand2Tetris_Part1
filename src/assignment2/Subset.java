
public class Subset {
  public static void main(String args[]) {
    if (args.length != 1) throw new IllegalArgumentException("Need ot give 1 arges");
    int numToPrint = Integer.parseInt(args[0]);
    RandomizedQueue<String> Q = new RandomizedQueue<String>();
    while (!StdIn.isEmpty()) {
      Q.enqueue(StdIn.readString());
    }
    assert(Q.size() >= numToPrint);
    int i = 0;
    for (String s : Q) {
      if (i == numToPrint) break;
      StdOut.println(s);
      i++;
    }
  }
}
