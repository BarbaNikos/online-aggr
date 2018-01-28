/**
 * Created by nikosrom on 1/27/18.
 */
public class StreamOnlineShed {

  static float cFactor(float variance, float mean, float windowSize, float confidence) {
    double first = (variance + Math.pow(mean, 2.0)) / (2.0f * windowSize * Math.pow(mean, 2.0));
    double second = Math.log(2 / confidence);
    return (float) Math.sqrt(first * second);
  }

}
