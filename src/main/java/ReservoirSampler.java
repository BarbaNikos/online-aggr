import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by nikosrom on 1/27/18.
 */
public class ReservoirSampler {

  public static int[] sample(int n, int[] collection) {
    float k = ((float) n) / (float) collection.length;
    return sample(k, collection);
  }

  public static int[] sample(float k, int[] collection) {
    if (k <= 0.0f || k > 1.0f)
      throw new IllegalArgumentException("reservoir percentage invalid");
    int reservoirSize = (int) (((float) collection.length) * k);
    int[] reservoir = new int[reservoirSize];
    int i;
    for (i = 0; i < reservoirSize; ++i)
      reservoir[i] = collection[i];
    for (; i < collection.length; ++i) {
      int j = ThreadLocalRandom.current().nextInt(i + 1);
      if (j < reservoirSize) {
        reservoir[j] = collection[i];
      }
    }
    return reservoir;
  }
}
