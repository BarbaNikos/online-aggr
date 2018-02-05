package window;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class Pane<T> {
  
  int age;
  
  long start;
  
  long end;
  
  private Collection<T> buffer;
  
  Pane(long start, long end) {
    this.start = start;
    this.end = end;
    buffer = new LinkedList<>();
    age = 0;
  }
  
  void add(T tuple) {
    buffer.add(tuple);
  }
  
  Collection<T> getBuffer() {
    return buffer;
  }
  
  void mature() {
    ++age;
  }
  
  
}
