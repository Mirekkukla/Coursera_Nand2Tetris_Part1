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
    node newFirstNode = new node();
    newFirstNode.contents = item;
    newFirstNode.previous = null;
    if (N == 0) {
      newFirstNode.next = null;
      first = newFirstNode;
      last = newFirstNode;
    } else {
      newFirstNode.next = first;
      first.previous = newFirstNode;
      first = newFirstNode;
    }
    N++;
  }

  // insert the item at the end
  public void addLast(Item item) {
    if (item == null) throw new java.lang.NullPointerException();
    node newLastNode = new node();
    newLastNode.contents = item;
    newLastNode.next = null;
    if (N == 0) {
      newLastNode.previous = null;
      first = newLastNode;
      last = newLastNode;
    } else {
      newLastNode.previous = last;
      last.next = newLastNode;
      last = newLastNode;
    }
    N++;
  }

  // delete and return the item at the front
  public Item removeFirst() {
    if (N == 0) throw new java.util.NoSuchElementException();
    Item ret = first.contents;
    if (N == 1) {
      first = null;
      last = null;
    } else if (N == 2) {
      first = first.next;
      first.previous = null;
      last.previous = null;
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
    } else if (N == 2) {
      last = last.previous;
      last.next = null;
      first.next = null;
    }else {
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
    private node currentNode = first;
    @Override
    public boolean hasNext() {
      return currentNode != null;
    }

    @Override
    public Item next() {
      if (!hasNext()) throw new java.util.NoSuchElementException();
      Item ret = currentNode.contents;
      currentNode = currentNode.next;
      return ret;
    }

    @Override
    public void remove() {
      throw new java.lang.UnsupportedOperationException();
    }
  }


  public static void main(String args[]) {
    Deque<Integer> test = new Deque<Integer>();
    test.addFirst(1);
    test.addLast(2);
    test.addLast(3);
    test.addFirst(0);
    Iterator<Integer> IT = test.iterator();
    test.addFirst(-1);
    while(IT.hasNext()) StdOut.print(IT.next());
    for (int x : test) StdOut.print(x);
//    StdOut.print(test.removeLast());
//    StdOut.print(test.removeLast());
//    StdOut.print(test.removeLast());
//    StdOut.print(test.removeLast());
//    StdOut.print(test.isEmpty());
//    StdOut.print(test.size());
  }

}
