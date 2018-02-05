package window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class Window<T> {
  
  private final long range;
  
  private final long slide;
  
  private final long paneRange;
  
  private final long maxNumberOfPanes;
  
  private ArrayList<Pane<T>> buffer;
  
  Window(int range, int slide) {
    if (range < 0L || slide < 0L)
      throw new IllegalArgumentException("negative range or slide");
    if (range < slide)
      throw new IllegalArgumentException("range must be bigger than slide");
    this.range = range;
    this.slide = slide;
    paneRange = gcd(this.range, this.slide);
    buffer = new ArrayList<>();
    maxNumberOfPanes = range / gcd(range, slide);
  }
  
  public boolean addEmpty(long timestamp, T tuple) {
    if (buffer.isEmpty()) {
      buffer.add(new Pane<>(timestamp - paneRange, timestamp));
      buffer.get(0).add(tuple);
      return true;
    }
    return false;
  }
  
  public boolean addInMostRecentPane(long timestamp, T tuple) {
    if (timestamp <= buffer.get(buffer.size() - 1).start)
      return false;
    if (timestamp > buffer.get(buffer.size() - 1).start &&
        timestamp <= buffer.get(buffer.size() - 1).end) {
      buffer.get(buffer.size() - 1).add(tuple);
      return true;
    }
    return false;
  }
  
  public List<Pane<T>> lockPane(int paneIndex) {
    ArrayList<Pane<T>> window = new ArrayList<Pane<T>>((int) range);
    for (int i = paneIndex - (int) range; i <= paneIndex; ++i) {
      if (i >= 0)
        window.add(buffer.get(i));
    }
    return window;
  }
  
  public List<List<Pane<T>>> createNewPane(long timestamp, T tuple) {
    long timeDifference = timestamp - buffer.get(0).start;
    if (timeDifference <= paneRange) {
      // this will generate one window
      long newStart = buffer.get(buffer.size() - 1).end;
      long newEnd = newStart + paneRange;
      Pane<T> newPane = new Pane<>(newStart, newEnd);
      newPane.add(tuple);
      buffer.add(newPane);
    } else {
      // this will generate multiple windows
      
    }
  }
  
  public List<List<Pane<T>>> add(long timestamp, T tuple) {
    List<Pane<T>> window = new LinkedList<>();
    if (buffer.isEmpty()) {
      buffer.add(new Pane<>(timestamp - paneRange, timestamp));
      buffer.get(0).add(tuple);
      return discretizeToWindows(window, range);
    } else {
      if (timestamp < buffer.get(buffer.size() - 1).start)
        return discretizeToWindows(window, range);
      if (timestamp > buffer.get(buffer.size() - 1).start &&
          timestamp <= buffer.get(buffer.size() - 1).end) {
        buffer.get(buffer.size() - 1).add(tuple);
        return discretizeToWindows(window, range);
      } else {
        boolean inserted = false;
        // keep adding panes until one is created with the right margins
        // invariant: by the end of the loop, the tuple will have been inserted in the last pane
        do {
          long nextStart = buffer.get(buffer.size() - 1).end;
          long nextEnd = nextStart + paneRange;
          Pane<T> newPane = new Pane<>(nextStart, nextEnd);
          if (timestamp > nextStart && timestamp <= nextEnd) {
            newPane.add(tuple);
            inserted = true;
            buffer.add(newPane);
            break;
          } else {
            buffer.add(newPane);
          }
        } while (buffer.size() < maxNumberOfPanes);
        if (!inserted) {
          long nextStart = buffer.get(buffer.size() - 1).end;
          long nextEnd = nextStart + paneRange;
          while ((nextStart < timestamp && timestamp <= nextEnd)) {
            nextStart = nextEnd;
            nextEnd = nextStart + paneRange;
          }
          Pane<T> newPane = new Pane<>(nextStart, nextEnd);
          newPane.add(tuple);
          buffer.add(newPane);
        }
        // need to check if a window is completed and evict those windows
        if (buffer.size() > maxNumberOfPanes) {
          while (buffer.size() > maxNumberOfPanes)
            window.add(buffer.remove(0));
          int index = 0;
          while (window.get(window.size() - 1).end - window.get(0).start < range) {
              window.add(buffer.get(index++));
          }
        }
        return discretizeToWindows(window, range);
      }
    }
  }
  
  private List<List<Pane<T>>> discretizeToWindows(List<Pane<T>> panes, long range) {
    List<List<Pane<T>>> windows = new LinkedList<>();
    List<Pane<T>> window = new LinkedList<>();
    while (panes.size() > 0) {
      if (window.size() == 0)
        window.add(panes.remove(0));
      else {
        if (window.get(0).start - panes.get(0).end < range)
          window.add(panes.remove(0));
        else {
          windows.add(window);
          window = new LinkedList<>();
          window.add(panes.remove(0));
        }
      }
    }
    if (window.size() > 0)
      windows.add(window);
    return windows;
  }
  
  public long getMaxNumberOfPanes() {
    return maxNumberOfPanes;
  }
  
  public long getPaneRange() {
    return paneRange;
  }
  
  private long gcd(long x, long y) {
    if (y == 0L) return x;
    return gcd(y, x % y);
  }
}
