package window;

import java.util.Collection;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class PaneLevelQuery<Tuple> {
  long start;
  
  long end;
  
  Collection<Tuple> buffer;
  
  PaneLevelQuery(long start, long end) {
    this.start = start;
    this.end = end;
  }
  
  void add(Tuple tuple) {
    buffer.add(tuple);
  }
}
