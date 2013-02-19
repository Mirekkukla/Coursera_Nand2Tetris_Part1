import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {

  private node first;
  private node last;
  private int N;

  private class node {
    Item contents;
    node previous;
    node next;
  }

  // construct an empty deque
  public Deque() {
    this.first = null;
    this.last = null;
    this.N = 0;
  }

  // is the deque empty?
  public boolean isEmpty() {
    return N == 0 ? true : false;
  }

  // return the number of items on the deque
  public int size() {
    return N;
  }

  // insert the item at the front
  public void addFirst(Item item) {
    if (item == null) throw new java.lang.NullPointerException();
    node newFrontNode = new node();
    newFrontNode.contents = item;
    newFrontNode.previous = null;
    newFrontNode.next = first;
    if (this.first != null) {
      first.previous = newFrontNode;
    }
    first = newFrontNode;
    if (N == 0) last = newFrontNode;
    N++;
  }

  // insert the item at the end
  public void addLast(Item item) {
    node newLastNode = new node();
    newLastNode.contents = item;
    newLastNode.next = null;
    newLastNode.previous = last;
    if (last != null) {
      last.next = newLastNode;
    }
    last = newLastNode;
    if (N == 0) first = newLastNode;
    N++;
  }

  // delete and return the item at the front
  public Item removeFirst() {
    if (N == 0) throw new java.util.NoSuchElementException();
    Item ret = first.contents;
    if (N == 1) {
      first = null;
      last = null;
    } else {
      first = first.next;
      first.previous = null;
    }
    N--;
    return ret;
  }

  // delete and return the item at the end
  public Item removeLast() {
    if (N == 0) throw new java.util.NoSuchElementException();
    Item ret = last.contents;
    if (N == 1) {
      first = null;
      last = null;
    } else {
      last = last.previous;
      last.next = null;
    }
    N--;
    return ret;
  }

  // return an iterator over items in order from front to end
  @Override
  public Iterator<Item> iterator() {
    return new DequeIterator();
  }

  private class DequeIterator implements Iterator<Item> {

  }


  public static void main(String args[]) {
    Deque<Integer> test = new Deque<Integer>();
    test.addFirst(1);
    test.addLast(2);
    test.addLast(3);
    test.addFirst(0);
    StdOut.print(test.removeLast());
    StdOut.print(test.removeLast());
    StdOut.print(test.removeLast());
    StdOut.print(test.removeLast());
    StdOut.print(test.isEmpty());
    StdOut.print(test.size());
  }

}
