import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class OnlineAggregation {
  
  static void onlineAggregationAverage(int[] sample, float a, float b, float error, float
      confidence) {
    float onlineSum = 0.0f;
    float count = 0.0f;
    for (int i = 0; i < sample.length; ++i) {
      onlineSum += sample[i];
      count += 1;
      float onlineEpsilon = interval((int) count, a, b, confidence);
      float onlineAverage = onlineSum / count;
      System.out.println("online avg: " + onlineAverage + ", interval: " + onlineEpsilon);
      if ((onlineEpsilon / onlineAverage) <= error)
        break;
    }
    float onlineAverage = onlineSum / count;
    OptionalDouble r = Arrays.stream(sample).average();
    float actualError = Math.abs(onlineAverage - (float) r.getAsDouble()) / (float) r.getAsDouble();
    System.out.println("after " + count + " elements, online avg: " + (onlineSum / count) +
    ", real average: " + r.getAsDouble());
    System.out.println("actual error: " + actualError);
  }
  
  static float cFactor(float variance, float mean, float windowSize, float confidence) {
    double first = (variance + Math.pow(mean, 2.0)) / (2.0f * windowSize * Math.pow(mean, 2.0));
    double second = Math.log(2 / confidence);
    return (float) Math.sqrt(first * second);
  }
  
  static float interval(int n, float a, float b, float confidence) {
    float first = 1.0f / (2.0f * (float) n);
    float second = (float) Math.log(2.0f / (1.0f - confidence));
    return (b - a) * (float) Math.sqrt(first * second);
  }
  
  public static void main(String[] args) {
    UniformIntegerDistribution distribution = new UniformIntegerDistribution(0, 10000);
    int[] sample = distribution.sample(10000000);
    onlineAggregationAverage(sample, 0, 1000, 0.1f, 0.99f);
  }
}
