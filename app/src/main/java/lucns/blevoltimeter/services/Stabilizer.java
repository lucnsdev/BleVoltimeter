package lucns.blevoltimeter.services;

public class Stabilizer {

    private static final int SIZE = 32;
    private final double[] buffer = new double[SIZE];
    private int index = 0;
    private int count = 0;
    private double sum = 0.0;

    public double put(double value) {
        if (count == SIZE) {
            sum -= buffer[index];
        } else {
            count++;
        }

        buffer[index] = value;
        sum += value;

        index = (index + 1) % SIZE;
        return sum / count;
    }
}
