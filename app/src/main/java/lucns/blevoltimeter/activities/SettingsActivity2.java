package lucns.blevoltimeter.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.services.DataController;
import lucns.blevoltimeter.services.Stabilizer;
import lucns.blevoltimeter.utils.Notify;
import lucns.blevoltimeter.utils.Utils;

public class SettingsActivity2 extends Activity {

    private DataController dataController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dataController = DataController.getInstance();
        double[] compensations = dataController.getCompensations();

        TextView textA0 = findViewById(R.id.textA0);
        TextView textA1 = findViewById(R.id.textA1);
        TextView textA2 = findViewById(R.id.textA2);
        TextView textA3 = findViewById(R.id.textA3);
        EditText editTextA0 = findViewById(R.id.editTextA0);
        EditText editTextA1 = findViewById(R.id.editTextA1);
        EditText editTextA2 = findViewById(R.id.editTextA2);
        EditText editTextA3 = findViewById(R.id.editTextA3);
        editTextA0.setText(String.valueOf(compensations[0]));
        editTextA1.setText(String.valueOf(compensations[1]));
        editTextA2.setText(String.valueOf(compensations[2]));
        editTextA3.setText(String.valueOf(compensations[3]));

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrate();
                Notify.showToast(R.string.calibrated);
                double a0 = Double.parseDouble(editTextA0.getText().toString());
                double a1 = Double.parseDouble(editTextA1.getText().toString());
                double a2 = Double.parseDouble(editTextA2.getText().toString());
                double a3 = Double.parseDouble(editTextA3.getText().toString());
                dataController.setCompensations(new double[]{a0, a1, a2, a3});
                dataController.save();
            }
        });
        Stabilizer stabilizer = new Stabilizer();
        Stabilizer stabilizer2 = new Stabilizer();
        Stabilizer stabilizer3 = new Stabilizer();
        Stabilizer stabilizer4 = new Stabilizer();
        dataController.addCallback("calibration", new DataController.OnValuesChangedListener() {
            @Override
            public void onValuesChanged() {
                String a0C = editTextA0.getText().toString();
                String a1C = editTextA1.getText().toString();
                String a2C = editTextA2.getText().toString();
                String a3C = editTextA3.getText().toString();
                double a0Compensation = a0C.isEmpty() ? 1 : Double.parseDouble(a0C);
                double a1Compensation = a1C.isEmpty() ? 1 : Double.parseDouble(a1C);
                double a2Compensation = a2C.isEmpty() ? 1 : Double.parseDouble(a2C);
                double a3Compensation = a3C.isEmpty() ? 1 : Double.parseDouble(a3C);
                double[] voltages = dataController.getVoltagesWithoutCompensations();
                textA0.setText(String.format(Locale.getDefault(), "%05.3fV", stabilizer.put(voltages[0]) * a0Compensation));
                textA1.setText(String.format(Locale.getDefault(), "%05.3fV", stabilizer2.put(voltages[1]) * a1Compensation));
                textA2.setText(String.format(Locale.getDefault(), "%05.3fV", stabilizer3.put(voltages[2]) * a2Compensation));
                textA3.setText(String.format(Locale.getDefault(), "%05.3fV", stabilizer4.put(voltages[3]) * a3Compensation));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataController.removeCallback("calibration");
    }
}