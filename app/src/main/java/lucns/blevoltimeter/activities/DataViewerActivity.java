package lucns.blevoltimeter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.ble.BleManagerControl;
import lucns.blevoltimeter.ble.ScannedBleDevice;
import lucns.blevoltimeter.fragments.FragmentGraphicsViewer;
import lucns.blevoltimeter.fragments.FragmentNumbersViewer;
import lucns.blevoltimeter.services.DataController;
import lucns.blevoltimeter.services.MainService;
import lucns.blevoltimeter.services.ServiceController;
import lucns.blevoltimeter.utils.Notify;
import lucns.blevoltimeter.utils.Utils;
import lucns.blevoltimeter.views.SliderView;

public class DataViewerActivity extends Activity {

    private BleManagerControl bleManagerControl;
    private DialogInformation dialog;
    private TextView textSamples, textSampleRate;
    private DataController dataController;

    private SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viewer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dataController = DataController.getInstance();
        dialog = new DialogInformation(this);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonSettings) {
                    Utils.vibrate();
                    startActivity(new Intent(DataViewerActivity.this, SettingsActivity2.class));
                }
            }
        };
        findViewById(R.id.buttonSettings).setOnClickListener(onClickListener);

        textSamples = findViewById(R.id.textSamples);
        textSampleRate = findViewById(R.id.textSampleRate);
        sliderView = findViewById(R.id.sliderView);
        sliderView.addFragment(new FragmentGraphicsViewer(this));
        sliderView.addFragment(new FragmentNumbersViewer(this));

        ServiceController.getInstance(this, new ServiceController.OnServiceAvailableListener() {
            @Override
            public void onAvailable(MainService mainService) {
                bleManagerControl = mainService.getBleManagerControl();
                bleManagerControl.setCallback(callback);
                bleManagerControl.setUUIDs("d4e74c8a-f35c-4773-861d-f230abacc001", "d4e74c8a-f35c-4773-861d-f230abacc002", "d4e74c8a-f35c-4773-861d-f230abacc003");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleManagerControl.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bleManagerControl != null) bleManagerControl.setCallback(callback);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private final BleManagerControl.Callback callback = new BleManagerControl.Callback() {

        long start;
        int samples;

        @Override
        public void onStateChanged(boolean enabled) {
            Utils.vibrate();
            if (enabled) {
                if (dialog != null) dialog.dismiss();
            } else {
                dialog.show(getString(R.string.bluetooth_disabled), new DialogInformation.Callback() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(DataViewerActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        }

        @Override
        public void onDevicesAvailable(ScannedBleDevice[] devices) {
        }

        @Override
        public void onConnectionChanged(boolean connected) {
            if (isDestroyed() || isFinishing()) return;
            if (!connected) {
                Utils.vibrate();
                Notify.showToast(R.string.disconnected);
                startActivity(new Intent(DataViewerActivity.this, MainActivity.class));
                finish();
            }
        }

        @Override
        public void onServicesDiscovered(boolean success) {
        }

        @Override
        public void onReceive(byte[] data) {
            if (data.length != 8) {
                Log.e("lucas", "Wrong data length: " + data.length);
                return;
            }
            int a0 = (data[0] << 8) + (data[1] & 0xFF);
            int a1 = (data[2] << 8) + (data[3] & 0xFF);
            int a2 = (data[4] << 8) + (data[5] & 0xFF);
            int a3 = (data[6] << 8) + (data[7] & 0xFF);
            //Log.d("Lucas", adc0 + ", " + adc1 + ", " + adc2 + ", " + adc3);
            dataController.putValues(a0, a1, a2, a3);
            textSamples.setText(String.valueOf(dataController.getSamplesCount()));
            samples++;
            long m = System.currentTimeMillis();
            if (start == 0) {
                start = m;
                return;
            }
            if (m - start > 999) {
                start = m;
                textSampleRate.setText(samples + "/s");
                samples = 0;
            }
        }
    };
}
