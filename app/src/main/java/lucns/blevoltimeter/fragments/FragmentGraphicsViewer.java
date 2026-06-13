package lucns.blevoltimeter.fragments;

import android.app.Activity;
import android.widget.TextView;

import java.util.Locale;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.services.DataController;
import lucns.blevoltimeter.views.FragmentView;
import lucns.blevoltimeter.views.WaveView;

public class FragmentGraphicsViewer extends FragmentView {

    private TextView textA0, textA1, textA2, textA3;
    private WaveView waveViewA0, waveViewA1, waveViewA2, waveViewA3;
    private DataController dataController;

    public FragmentGraphicsViewer(Activity activity) {
        super(activity);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.fragment_graphics_viewer);

        textA0 = findViewById(R.id.textA0);
        textA1 = findViewById(R.id.textA1);
        textA2 = findViewById(R.id.textA2);
        textA3 = findViewById(R.id.textA3);
        waveViewA0 = findViewById(R.id.waveViewA0);
        waveViewA1 = findViewById(R.id.waveViewA1);
        waveViewA2 = findViewById(R.id.waveViewA2);
        waveViewA3 = findViewById(R.id.waveViewA3);

        dataController = DataController.getInstance();
        dataController.addCallback("fragment_graphics", new DataController.OnValuesChangedListener() {
            @Override
            public void onValuesChanged() {
                updateValues();
            }
        });
    }

    private void updateValues() {
        int[] a0 = dataController.getA0Samples();
        int[] a1 = dataController.getA1Samples();
        int[] a2 = dataController.getA2Samples();
        int[] a3 = dataController.getA3Samples();
        double[] voltages = dataController.getVoltages();
        double[] compensations = dataController.getCompensations();
        textA0.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[0]));
        textA1.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[1]));
        textA2.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[2]));
        textA3.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[3]));
        waveViewA0.setCompensation(compensations[0]);
        waveViewA1.setCompensation(compensations[1]);
        waveViewA2.setCompensation(compensations[2]);
        waveViewA3.setCompensation(compensations[3]);
        waveViewA0.setValues(a0);
        waveViewA1.setValues(a1);
        waveViewA2.setValues(a2);
        waveViewA3.setValues(a3);
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        dataController.removeCallback("fragment_graphics");
    }
}
