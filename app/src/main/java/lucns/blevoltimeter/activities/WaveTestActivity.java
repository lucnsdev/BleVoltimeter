package lucns.blevoltimeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.Locale;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.views.WaveView;

public class WaveTestActivity extends Activity {

    private TextView textA0, textA1, textA2, textA3, textSamples, textSampleRate;
    private WaveView waveViewA0, waveViewA1, waveViewA2, waveViewA3;
    private int samples;
    private boolean running;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_graphics_viewer);

        textA0 = findViewById(R.id.textA0);
        textA1 = findViewById(R.id.textA1);
        textA2 = findViewById(R.id.textA2);
        textA3 = findViewById(R.id.textA3);
        textSamples = findViewById(R.id.textSamples);
        textSampleRate = findViewById(R.id.textSampleRate);
        waveViewA0 = findViewById(R.id.waveViewA0);
        waveViewA1 = findViewById(R.id.waveViewA1);
        waveViewA2 = findViewById(R.id.waveViewA2);
        waveViewA3 = findViewById(R.id.waveViewA3);

        handler = new Handler(Looper.getMainLooper());
    }

    public void putValues(int a0, int a1, int a2, int a3) {
        samples++;
        double parcel = 0.512d / 32768;
        double v0 = a0 * parcel;
        double v1 = a1 * parcel;
        double v2 = a2 * parcel;
        double v3 = a3 * parcel;
        textA0.setText(String.format(Locale.getDefault(), "%05.3fv", v0));
        textA1.setText(String.format(Locale.getDefault(), "%05.3fv", v1));
        textA2.setText(String.format(Locale.getDefault(), "%05.3fv", v2));
        textA3.setText(String.format(Locale.getDefault(), "%05.3fv", v3));
        waveViewA0.put(a0);
        waveViewA1.put(a1);
        waveViewA2.put(a2);
        waveViewA3.put(a3);
        textSamples.setText(String.valueOf(samples));
    }

    private void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long interval = 25;
                /*
                SineWaveGenerator sine0 = new SineWaveGenerator(1);
                SineWaveGenerator sine1 = new SineWaveGenerator(5);
                SineWaveGenerator sine2 = new SineWaveGenerator(13);
                SineWaveGenerator sine3 = new SineWaveGenerator(19);

                SineWaveGenerator2 sine0 = new SineWaveGenerator2(1, 20, 0, 32767, interval);
                SineWaveGenerator2 sine1 = new SineWaveGenerator2(1, 20, 0, 32767, interval);
                SineWaveGenerator2 sine2 = new SineWaveGenerator2(1, 20, 0, 32767, interval);
                SineWaveGenerator2 sine3 = new SineWaveGenerator2(1, 20, 0, 32767, interval);
            */
                SineWaveGenerator3 sine0 = new SineWaveGenerator3(1, 0, 32767, 3000);
                SineWaveGenerator3 sine1 = new SineWaveGenerator3(5, 0, 32767, 5000);
                SineWaveGenerator3 sine2 = new SineWaveGenerator3(13, 0, 32767, 7000);
                SineWaveGenerator3 sine3 = new SineWaveGenerator3(19, 0, 32767, 13000);
                while (running) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            putValues(sine0.next(), sine1.next(), sine2.next(), sine3.next());
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }
}
