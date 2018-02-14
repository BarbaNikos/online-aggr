import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class StreamAggregates {
  
  private static DecimalFormat decimalFormat = new DecimalFormat("##.####");
  
//  static double shed(int[] window, double mu, double variance, double confidence,
//                   double error) {
//    double sampleRate = calculateSampleRate(window.length, mu, variance, confidence, error);
//    int[] sample = ReservoirSampler.sample((float) sampleRate, window);
//    double shedAverage = Arrays.stream(sample).average().getAsDouble();
//    double actualAverage = Arrays.stream(window).average().getAsDouble();
//    float actualError = 100.0f * (float) Math.abs(shedAverage - actualAverage) / (float)
//        actualAverage;
//    System.out.println("sample rate: " + decimalFormat.format(sampleRate) + ", error: " +
//        decimalFormat.format(actualError));
//    return actualError;
//  }
  
  static double shed(double[] window, double mu, double variance, double confidence,
                   double error) {
    double sampleRate = calculateSampleRate(window.length, mu, variance, confidence, error);
    double[] sample;
    try {
      sample = ReservoirSampler.sample((float) sampleRate, window);
    } catch (IllegalArgumentException e) {
      System.out.println("illegal sample rate: " + sampleRate + ", for window with size: " +
          window.length);
      return -1.0f;
    }
    double shedAverage = Arrays.stream(sample).average().getAsDouble();
    double actualAverage = Arrays.stream(window).average().getAsDouble();
    float actualError = 100.0f * (float) Math.abs(shedAverage - actualAverage) / (float)
        actualAverage;
//    System.out.println("sample rate: " + decimalFormat.format(sampleRate) + ", error: " +
//        decimalFormat.format(actualError));
    return actualError;
  }
  
  static double shedSum(DescriptiveStatistics p, double[] window, double mu, double variance,
                        double confidence, double error) {
    double shedProbability = calculateSampleRate(window.length, mu, variance, confidence, error);
    p.addValue(shedProbability);
//    List<Double> nonShed = new ArrayList<>();
//    for (double d : window) {
//      Optional<Double> e = shedForSum(shedProbability, d);
//      if (e.isPresent())
//        nonShed.add(e.get());
//    }
    double shedSum = Arrays.stream(sampleForSum(shedProbability, window)).sum();
    double actualSum = Arrays.stream(window).sum();
    float actualError = (float) Math.abs(actualSum - shedSum);
    return actualError / actualSum;
  }
  
  static Optional<Double> shedForSum(double probability, double value) {
    if (ThreadLocalRandom.current().nextDouble() < probability)
      return Optional.of(0.0);
    return Optional.of(value / probability);
  }
  
  static double[] sampleForSum(double probability, double[] data) {
    if (probability >= 1.0)
      probability = 1.0;
    double[] sample = ReservoirSampler.sample((float) probability, data);
    double scale = probability;
    return Arrays.stream(sample).map(x -> x / scale).toArray();
  }
  
  static double calculateSampleRate(int windowSize, double mu, double variance,
                                    double confidence, double error) {
    double c = calculateC(windowSize, mu, variance, confidence);
    return c / error;
  }
  
  static double calculateC(int windowSize, double mu, double variance, double confidence) {
    double squaredMu = Math.pow(mu, 2.0);
    double first = (variance + squaredMu) / (2.0 * (double) windowSize * squaredMu);
    double second = Math.log(2.0 / confidence);
    return Math.sqrt(first * second);
  }
  
  private static void experiment(int iterations, double error, double confidence) {
    NormalDistribution dist = null;
    DescriptiveStatistics healthy = new DescriptiveStatistics();
    DescriptiveStatistics sick = new DescriptiveStatistics();
    DescriptiveStatistics prob = new DescriptiveStatistics();
    for (int i = 0; i < iterations; ++i) {
      dist = new NormalDistribution(50, Math.sqrt(10));
      double[] sample = dist.sample(1000);
      double average = Arrays.stream(sample).average().getAsDouble();
      double variance = Arrays.stream(sample).map(x -> Math.pow(x - average, 2.0)).average()
          .getAsDouble();
      double healthyError = shedSum(prob, sample, average, variance, confidence, error);
      if (healthyError < 0.0f)
        continue;
      dist = new NormalDistribution(350, Math.sqrt(10));
      sample = dist.sample(1000);
      double average1 = Arrays.stream(sample).average().getAsDouble();
      variance = Arrays.stream(sample).map(x -> Math.pow(x - average1, 2.0)).average()
          .getAsDouble();
      double sickError = shedSum(prob, sample, average1, variance, confidence, error);
      if (sickError < 0.0f)
        continue;
      healthy.addValue(healthyError);
      sick.addValue(sickError);
    }
    System.out.println("min\t25\t50\t75\tmax\tavg");
    StringBuilder h = new StringBuilder();
    h.append(decimalFormat.format(healthy.getMin()) + "\t");
    h.append(decimalFormat.format(healthy.getPercentile(0.25)) + "\t");
    h.append(decimalFormat.format(healthy.getPercentile(0.5)) + "\t");
    h.append(decimalFormat.format(healthy.getPercentile(0.75)) + "\t");
    h.append(decimalFormat.format(healthy.getMax()) + "\t");
    h.append(decimalFormat.format(healthy.getMean()) + "\t");
    StringBuilder s = new StringBuilder();
    s.append(decimalFormat.format(sick.getMin()) + "\t");
    s.append(decimalFormat.format(sick.getPercentile(0.25)) + "\t");
    s.append(decimalFormat.format(sick.getPercentile(0.5)) + "\t");
    s.append(decimalFormat.format(sick.getPercentile(0.75)) + "\t");
    s.append(decimalFormat.format(sick.getMax()) + "\t");
    s.append(decimalFormat.format(sick.getMean()) + "\t");
//    StringBuilder p = new StringBuilder();
//    p.append(decimalFormat.format(prob.getMin()) + "\t");
//    p.append(decimalFormat.format(prob.getPercentile(0.25)) + "\t");
//    p.append(decimalFormat.format(prob.getPercentile(0.5)) + "\t");
//    p.append(decimalFormat.format(prob.getPercentile(0.75)) + "\t");
//    p.append(decimalFormat.format(prob.getMax()) + "\t");
//    p.append(decimalFormat.format(prob.getMean()) + "\t");
    System.out.println(h.toString());
    System.out.println(s.toString());
//    System.out.println(p.toString());
  }
  
  public static void exploreSampleRate(int windowSize, double mu, double variance,
                                       double confidence) {
    for (double error = 0.0; error <= 1.0; error += 0.05) {
      double sampleRate = calculateSampleRate(windowSize, mu, variance, confidence, error);
      System.out.println(decimalFormat.format(error) + "," + decimalFormat.format(sampleRate));
    }
  }
  
  public static void exploreSampleRateTwo(double mu, double variance,
                                       double confidence, double error) {
    for (int windowSize = 1000; windowSize <= 1000000000; windowSize *= 10) {
      double sampleRate = calculateSampleRate(windowSize, mu, variance, confidence, error);
      System.out.println(decimalFormat.format(windowSize) + "," + decimalFormat.format(sampleRate));
    }
  }
  
  public static void main(String[] args) {
//    float error = 0.01f;
//    float confidence = 0.95f;
//    int N = 1000000;
//    double mu = 10;
//    double sigma = 10;
//    double mu1 = 70;
//    double sigma1 = 10;
//    NormalDistribution normalDistribution = new NormalDistribution(mu, sigma);
//    double[] normalSample = normalDistribution.sample(N);
//    shed(normalSample, normalSample.length, mu, sigma, confidence, error);
//    NormalDistribution normalDistribution1 = new NormalDistribution(mu1, sigma1);
//    double[] normalSample1 = normalDistribution1.sample(N);
//    shed(normalSample1, normalSample1.length, mu1, sigma1, confidence, error);
//    LogNormalDistribution logNormal = new LogNormalDistribution(1.789, 2.366);
//    double[] logNormalSample = logNormal.sample(N);
//    shed(logNormalSample, logNormalSample.length, 1.789, 2.366, confidence, error);
    double confidence = 0.9;
    double error = 0.1;
    double sampleRate = calculateSampleRate(1000, 50, 10, 1.0 - confidence, error);
    double sampleRateTwo = calculateSampleRate(1000, 350, 10, 1.0 - confidence, error);
    //experiment(10000, error, 1.0 - confidence);
    System.out.println("*******");
    System.out.println("confidence: " + confidence);
    System.out.println("error: " + error);
//    exploreSampleRate(1000, 50, 10, 1.0 - confidence);
    System.out.println("*********");
    exploreSampleRateTwo(350.0, 10.0, 1.0 - confidence, error);
  }
}
