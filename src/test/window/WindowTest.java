package window;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class WindowTest {
  
  @Test
  public void addEmpty() {
    Window<Integer> wlq = new Window<>(2, 1);
    List<List<Pane<Integer>>> evicted = wlq.add(1, 1);
    assertEquals(0, evicted.size());
  }
  
  @Test
  public void addCreateNewPlq() {
    Window<Integer> wlq = new Window<>(2, 1);
    List<List<Pane<Integer>>> evicted = wlq.add(1, 1);
    evicted = wlq.add(2, 2);
    assertEquals(0, evicted.size());
  }
  
  @Test
  public void addEvictDueToWindowCompletion() {
    Window<Integer> wlq = new Window<>(2, 1);
    List<List<Pane<Integer>>> window;
    window = wlq.add(1, 1);
    assertEquals(0, window.size());
    window = wlq.add(2, 4);
    assertEquals(0, window.size());
    window = wlq.add(3, 7);
    assertEquals(1, window.size());
    assertEquals(2, window.get(0).size());
    window = wlq.add(10, 10);
    assertEquals(0, window);
  }
  
  @Test
  public void addTumblingWindow() {
    Window<Integer> wlq = new Window<>(1, 1);
    List<List<Pane<Integer>>> window;
    window = wlq.add(1, 1);
    assertEquals(0, window.size());
    window = wlq.add(2, 2);
    assertEquals(1, window.size());
  }
}