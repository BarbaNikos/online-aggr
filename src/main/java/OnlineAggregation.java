import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class OnlineAggregation {

  private static void onlineAverage(int[] relation, int n, float a, float b, float confidence) {
    int[] sample = ReservoirSampler.sample(n, relation);
    OptionalDouble onlineOptionalMean = Arrays.stream(sample).average();
    double onlineAverage = onlineOptionalMean.getAsDouble();
    OptionalDouble r = Arrays.stream(relation).average();
    float interval = interval(sample.length, a, b, confidence);
    printStatistics(sample.length, onlineAverage, r.getAsDouble(), interval);
  }

  private static void onlineVariance(int[] relation, int n, float a, float b, float confidence) {
    int[] sample = ReservoirSampler.sample(n, relation);
    OptionalDouble onlineOptionalMean = Arrays.stream(sample).average();
    double onlineMean = onlineOptionalMean.getAsDouble();
    OptionalDouble onlineOptionalVariance = Arrays.stream(sample).asDoubleStream()
        .map(s -> Math.pow(s - onlineMean, 2)).average();
    double onlineVariance = onlineOptionalVariance.getAsDouble();
    OptionalDouble r = Arrays.stream(relation).average();
    double actualMean = r.getAsDouble();
    OptionalDouble actualOptionalVariance = Arrays.stream(relation).asDoubleStream()
        .map(s -> Math.pow(s - actualMean, 2)).average();
    double actualVariance = actualOptionalVariance.getAsDouble();
    float interval = interval(sample.length, a, b, confidence);
    printStatistics(sample.length, onlineVariance, actualVariance, interval);
  }

  private static void printStatistics(int n, double estimate, double actual, float interval) {
    double actualError = Math.abs(estimate - actual) / actual;
    System.out.println("Using " + n + " elements, online aggregate: " + estimate +
        " (actual: " + actual + "), error: " + actualError);
    System.out.println("interval: " + interval);
    System.out.print("success: ");
    if (actual >= (estimate - interval) && actual <= (estimate + interval))
      System.out.println("true");
    else
      System.out.println("false");
  }

  private static void onlineSum(int[] relation, int n, float a, float b, float confidence) {
    int[] sample = ReservoirSampler.sample(n, relation);
    double onlineSum = Arrays.stream(sample).asDoubleStream()
//        .map(s -> relation.length * s)
        .sum();
    double actualSum = Arrays.stream(relation).sum();
    float interval = interval(sample.length, a, b, confidence);
    printStatistics(sample.length, onlineSum, actualSum, interval);
  }
  
  private static float interval(int n, float a, float b, float confidence) {
    float first = 1.0f / (2.0f * (float) n);
    float second = (float) Math.log(2.0f / (1.0f - confidence));
    return (b - a) * (float) Math.sqrt(first * second);
  }

  private static int sampleSize(float confidence, float error, int a, int b) {
    double first = Math.log(2.0 / (1.0 - (double) confidence));
    double squareBoundDifference = Math.pow(b - a, 2.0);
    double squaredError = Math.pow(error, 2.0);
    double result = 0.5 * first * squareBoundDifference * squaredError;
    return (int) result;
  }
  
  public static void main(String[] args) {
    int a = 0;
    int b = 1000;
    float error = 0.01f;
    float confidence = 0.99f;
    int m = 100000;
    int n = sampleSize(confidence, error, a, b);
    UniformIntegerDistribution distribution = new UniformIntegerDistribution(a, b);
    int[] sample = distribution.sample(m);
    System.out.println("** AVG **");
    onlineAverage(sample, n, (float) a, (float) b, 0.99f);
    System.out.println("** VARIANCE **");
    onlineVariance(sample, n, (float) a, (float) b, 0.99f);
    System.out.println("** SUM **");
    onlineSum(sample, n, (float) a, (float) b, 0.99f);
  }
}
