import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.util.Arrays;

/**
 * @author Nikos R. Katsipoulakis (nick.katsip@gmail.com)
 */
public class StreamAggregates {
  
  static void shed(int[] window, int windowSize, double mu, double variance, double confidence,
                   double error) {
    double sampleRate = calculateSampleRate(windowSize, mu, variance, confidence, error);
    int[] sample = ReservoirSampler.sample((float) sampleRate, window);
    double shedAverage = Arrays.stream(sample).average().getAsDouble();
    double actualAverage = Arrays.stream(window).average().getAsDouble();
    float actualError = 100.0f * (float) Math.abs(shedAverage - actualAverage) / (float)
        actualAverage;
    System.out.println("sample rate: " + sampleRate + ", error: " + actualError);
  }
  
  static void shed(double[] window, int windowSize, double mu, double variance, double confidence,
                   double error) {
    double sampleRate = calculateSampleRate(windowSize, mu, variance, confidence, error);
    double[] sample = ReservoirSampler.sample((float) sampleRate, window);
    double shedAverage = Arrays.stream(sample).average().getAsDouble();
    double actualAverage = Arrays.stream(window).average().getAsDouble();
    float actualError = 100.0f * (float) Math.abs(shedAverage - actualAverage) / (float)
        actualAverage;
    System.out.println("sample rate: " + sampleRate + ", error: " + actualError);
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
  
  public static void main(String[] args) {
    float error = 0.01f;
    float confidence = 0.99f;
    int N = 1000000;
    double mu = 180000;
    double sigma = 17000;
    double mu1 = 100;
    double sigma1 = 20;
    NormalDistribution normalDistribution = new NormalDistribution(mu, sigma);
    double[] normalSample = normalDistribution.sample(N);
    shed(normalSample, normalSample.length, mu, sigma, confidence, error);
    NormalDistribution normalDistribution1 = new NormalDistribution(mu1, sigma1);
    double[] normalSample1 = normalDistribution.sample(N);
    shed(normalSample1, normalSample1.length, mu1, sigma1, confidence, error);
    LogNormalDistribution logNormal = new LogNormalDistribution(1.789, 2.366);
    double[] logNormalSample = logNormal.sample(N);
    shed(logNormalSample, logNormalSample.length, 1.789, 2.366, confidence, error);
  }
}
