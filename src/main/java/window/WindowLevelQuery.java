package window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class WindowLevelQuery<Tuple, Result> {
  private long range;
  
  private long slide;
  
  private long paneLevelQueryRange;
  
  private ArrayList<PaneLevelQuery<Tuple, Result>> buffer;
  
  public WindowLevelQuery(long range, long slide) {
    this.range = range;
    this.slide = slide;
    paneLevelQueryRange = gcd(this.range, this.slide);
    buffer = new ArrayList<>();
  }
  
  public Collection<PaneLevelQuery<Tuple, Result>> add(long timestamp, Tuple tuple) {
    if (buffer.isEmpty()) {
      buffer.add(new PaneLevelQuery<>(timestamp - paneLevelQueryRange, timestamp));
      buffer.get(0).add(tuple);
    }
    // search to see if it can be placed somewhere
    boolean inserted = false;
    for (PaneLevelQuery<Tuple, Result> plq : buffer) {
      if (timestamp >= plq.start && timestamp <= plq.end) {
        inserted = true;
        plq.add(tuple);
        break;
      }
    }
    // create new PLQ if timestamp > end of the last plq
    if (!inserted && timestamp > buffer.get(buffer.size() - 1).end) {
      long previousEndTimestamp = buffer.get(buffer.size() - 1).end;
      long newStart;
      long newEnd;
      if (timestamp - previousEndTimestamp > paneLevelQueryRange) {
        newStart = timestamp - paneLevelQueryRange;
        newEnd = timestamp;
      } else {
        newStart = previousEndTimestamp + 1;
        newEnd = newStart + paneLevelQueryRange;
      }
      buffer.add(new PaneLevelQuery<>(newStart, newEnd));
      buffer.get(buffer.size() - 1).add(tuple);
      long oldestEndTimestamp = buffer.get(0).end;
      if (oldestEndTimestamp < timestamp - range) {
        List<PaneLevelQuery<Tuple, Result>> evicted = new ArrayList<>();
        while (true) {
          if (buffer.get(0).end < timestamp - range) {
            evicted.add(buffer.remove(0));
          } else {
            break;
          }
        }
        if (!evicted.isEmpty())
          return evicted;
      }
    }
    return null;
  }
  
  private long gcd(long x, long y) {
    if (y == 0L) return x;
    return gcd(y, x % y);
  }
}
