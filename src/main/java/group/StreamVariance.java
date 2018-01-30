package group;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class StreamVariance {
  private float sum;
  
  private float squareSum;
  
  private float count;
  
  private float m;
  
  private float v;
  
  public StreamVariance() {
    sum = 0.0f;
    count = 0.0f;
    m = 0.0f;
    v = -1.0f;
  }
  
  public void add(float x) {
    sum += x;
    squareSum += Math.pow(x, 2.0f);
    count += 1;
    float mPrime = m + (x - m) / count;
    if (v < 0) {
      v = 0;
    } else {
      v = v + (x - m)*(x - mPrime);
    }
  }
  
  public float mean() {
    return sum / count;
  }
  
  public int count() {
    return (int) count;
  }
  
  public float variance() {
    return (float) (squareSum / count - Math.pow(mean(), 2));
  }
  
  public float preciseVariance() {
    return v / (count - 1);
  }
}
