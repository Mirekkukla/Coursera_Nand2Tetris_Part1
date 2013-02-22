import java.util.Iterator;

import com.javamex.classmexer.MemoryUtil;


/**
 * A randomized queue. Elements can be added and removed in constant ammortized time.
 * The element to be returned is selected uniformly at random.
**/
public class RandomizedQueue<Item> implements Iterable<Item> {
  /*
   * Store elements in a dynamically resizing array. Double the array size once the array fills up.
   * Shrink the array once it's 3/4 empty. This guarantees constant amortized work
   *
   * Use a queue to keep track of the indices that correspond to empty array cells.
   *
   * To retrieve a random element: select a random number between (0, array size - 1). If the cell
   * is empty, try again. Expected worst case work = 4 here since array is never less than 1/4 empty.
   *
   * To store an element: deque an index value from our emptyIndices queue, and store the element
   * at that index.
   */

  private Item[] array;
  private Stack<Integer> emptyIndices;
  private int N;

  // construct an empty randomized queue
  public RandomizedQueue() {
    N = 0;
    emptyIndices = new Stack<Integer>();
  }

  // is the queue empty?
  public boolean isEmpty() { return N == 0; }

  // return the number of items on the queue
  public int size() { return N; }

  // add the item
  public void enqueue(Item item) {
    if (item == null) throw new java.lang.NullPointerException();
    if (N == 0) {
      array = (Item[]) new Object[1];
      array[0] = item;
    } else if (emptyIndices.isEmpty()) {
      int oldSize = array.length;
      Item[] newArray = (Item[]) new Object[oldSize * 2];
      for (int i = 0; i < newArray.length; i++) {
        if (i < oldSize) { newArray[i] = array[i]; }// copy old
        else if (i == oldSize) { newArray[i] = item; }// insert new
        else {
          newArray[i] = null;
          emptyIndices.push(i); // mark the rest as empty
        }
      }
      array = newArray;
    } else {
      int emptyIndex = emptyIndices.pop();
      array[emptyIndex] = item;
    }
    N++;
  }

  // delete and return a random item
  public Item dequeue() {
    if (N == 0) throw new java.util.NoSuchElementException();
    int indexToRemove = findIndexOfRand();
    Item ret = array[indexToRemove];
    array[indexToRemove] = null;
    emptyIndices.push(indexToRemove);
    N--;

    if (N <= array.length / 4) {
      Item[] newArray = (Item[]) new Object[array.length / 4];
      int i = 0;
      for (Item item : array) {
        if (item != null) {
          newArray[i] = item;
          i++;
        }
      }
      assert(i == newArray.length);
      array = newArray; //the array should be full...
      emptyIndices = new Stack<Integer>(); //...so reset empty indices
    }
    return ret;
  }

  // return (but do not delete) a random item
  public Item sample() {
    if (N == 0) throw new java.util.NoSuchElementException();
    return array[findIndexOfRand()];
  }

  private int findIndexOfRand() {
    while (true) {
      int randInt = StdRandom.uniform(array.length);
      if (array[randInt] != null) {
        return randInt;
      }
    }
  }


  // return an independent iterator over items in random order
  @Override
  public Iterator<Item> iterator() {
    return new RandIterator();
  }

  private class RandIterator implements Iterator<Item> {
    int[] indexArray = new int[N];
    int count = 0;

    public RandIterator() {
      int i = 0;
      //get indices of all items
      for (int j = 0; j < array.length; j++) {
        if (array[j] != null) {
          indexArray[i] = j;
          i++;
        }
      }

      StdRandom.shuffle(indexArray);
    }

    @Override
    public boolean hasNext() {
      return count < N;
    }

    @Override
    public Item next() {
      if (count == N) throw new java.util.NoSuchElementException();
      Item ret = array[indexArray[count]];
      count++;
      return ret;
    }

    @Override
    public void remove() {
      throw new java.lang.UnsupportedOperationException();
    }

  }

  public static void main(String args[]) {
    RandomizedQueue<String> Q = new RandomizedQueue<String>();
    for (Integer i = 1; i < 20; i++) {
      Q.enqueue(Integer.toString(i));
    }
    for (Integer i = 1; i < 10; i++) {
      String s = Q.dequeue();
    }
    long noBytes = MemoryUtil.deepMemoryUsageOf(Q);

//    Integer a = new Integer(123456);
//    StdOut.println("size of integer = " + MemoryUtil.memoryUsageOf(a) + " bytes");

//    StdOut.println(noBytes);
//
//    for (String x : Q) {
//      StdOut.print(x + " ");
//    }
//    StdOut.println("");
//
//    int max = Q.size();
//    for (int i = 0; i < max/2; i++) {
//      String x = Q.dequeue();
//      StdOut.print(x + " ");
//    }
//
//    StdOut.println("");
//
//    for (String x : Q) {
//      StdOut.print(x + " ");
//    }
  }
}
