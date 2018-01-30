package window;

import static org.junit.Assert.*;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class WindowLevelQueryTest {
  @org.junit.Before
  public void setUp() throws Exception {
  }
  
  @org.junit.After
  public void tearDown() throws Exception {
  }
  
  @org.junit.Test
  public void add() throws Exception {
    WindowLevelQuery<Integer, Integer> window = new WindowLevelQuery<>(10L, 1L);
    
  }
  
}