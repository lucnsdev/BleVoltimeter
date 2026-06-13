package lucns.blevoltimeter.fragments;

import android.app.Activity;
import android.widget.TextView;

import java.util.Locale;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.services.DataController;
import lucns.blevoltimeter.services.Stabilizer;
import lucns.blevoltimeter.views.FragmentView;
import lucns.blevoltimeter.views.textview.AutofitTextView;

public class FragmentNumbersViewer extends FragmentView {

    private AutofitTextView textA0, textA1, textA2, textA3;
    private TextView textA02, textA12, textA22, textA32;
    private DataController dataController;
    private Stabilizer stabilizerA0, stabilizerA1, stabilizerA2, stabilizerA3;

    public FragmentNumbersViewer(Activity activity) {
        super(activity);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.fragment_numbers_viewer);

        stabilizerA0 = new Stabilizer();
        stabilizerA1 = new Stabilizer();
        stabilizerA2 = new Stabilizer();
        stabilizerA3 = new Stabilizer();

        String baseText = "00,000v";
        textA0 = findViewById(R.id.textA0);
        textA0.autoSizeWithText(baseText);
        textA1 = findViewById(R.id.textA1);
        textA1.autoSizeWithText(baseText);
        textA2 = findViewById(R.id.textA2);
        textA2.autoSizeWithText(baseText);
        textA3 = findViewById(R.id.textA3);
        textA3.autoSizeWithText(baseText);
        textA02 = findViewById(R.id.textA02);
        textA12 = findViewById(R.id.textA12);
        textA22 = findViewById(R.id.textA22);
        textA32 = findViewById(R.id.textA32);

        dataController = DataController.getInstance();
        dataController.addCallback("fragment_numbers", new DataController.OnValuesChangedListener() {
            @Override
            public void onValuesChanged() {
                updateValues();
            }
        });
    }

    private void updateValues() {
        double[] voltages = dataController.getVoltages();
        textA0.setText(String.format(Locale.getDefault(), "%05.3f", stabilizerA0.put(voltages[0])));
        textA1.setText(String.format(Locale.getDefault(), "%05.3f", stabilizerA1.put(voltages[1])));
        textA2.setText(String.format(Locale.getDefault(), "%05.3f", stabilizerA2.put(voltages[2])));
        textA3.setText(String.format(Locale.getDefault(), "%05.3f", stabilizerA3.put(voltages[3])));
        textA02.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[0]));
        textA12.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[1]));
        textA22.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[2]));
        textA32.setText(String.format(Locale.getDefault(), "%05.3fV", voltages[3]));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        dataController.removeCallback("fragment_numbers");
    }
}
