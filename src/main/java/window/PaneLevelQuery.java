package window;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class PaneLevelQuery<Tuple, Result> {
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
