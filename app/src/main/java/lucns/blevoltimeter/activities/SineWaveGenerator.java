package lucns.blevoltimeter.activities;

public class SineWaveGenerator {

    private double frequencyHz;
    private long startTime;

    public SineWaveGenerator(double frequencyHz) {
        setFrequency(frequencyHz);
        this.startTime = System.nanoTime();
    }

    public void setFrequency(double frequencyHz) {
        this.frequencyHz = frequencyHz;
    }

    public int next() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - startTime) / 1_000_000_000.0;
        double phase = 2.0 * Math.PI * frequencyHz * elapsedSeconds;
        double sine = Math.sin(phase);
        return (int) ((sine + 1.0) * 0.5 * 32767);
    }
}