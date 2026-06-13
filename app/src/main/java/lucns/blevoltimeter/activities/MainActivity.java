package lucns.blevoltimeter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.ble.BleManagerControl;
import lucns.blevoltimeter.ble.ScannedBleDevice;
import lucns.blevoltimeter.fragments.FragmentBleConnecting;
import lucns.blevoltimeter.fragments.FragmentBleEnable;
import lucns.blevoltimeter.fragments.FragmentBleScan;
import lucns.blevoltimeter.services.MainService;
import lucns.blevoltimeter.services.ServiceController;
import lucns.blevoltimeter.utils.Notify;
import lucns.blevoltimeter.utils.Utils;
import lucns.blevoltimeter.views.SliderView;

public class MainActivity extends Activity {

    private SliderView sliderView;
    private BleManagerControl bleManagerControl;
    private DialogInformation dialog;
    private FragmentBleScan fragmentBleScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new DialogInformation(this);

        fragmentBleScan = new FragmentBleScan(this, new FragmentBleScan.OnDeviceSelectedListener() {
            @Override
            public void onDeviceSelected(BluetoothDevice device) {
                sliderView.goToIndex(2);
                bleManagerControl.stopScan();
                bleManagerControl.connect(device);
            }
        });
        sliderView = findViewById(R.id.sliderView);
        sliderView.disableScroll(true);
        sliderView.addFragment(new FragmentBleEnable(this));
        sliderView.addFragment(fragmentBleScan);
        sliderView.addFragment(new FragmentBleConnecting(this));

        ServiceController.getInstance(this, new ServiceController.OnServiceAvailableListener() {
            @Override
            public void onAvailable(MainService mainService) {
                bleManagerControl = mainService.getBleManagerControl();
                bleManagerControl.setCallback(callback);
                bleManagerControl.setUUIDs("d4e74c8a-f35c-4773-861d-f230abacc001", "d4e74c8a-f35c-4773-861d-f230abacc002", "d4e74c8a-f35c-4773-861d-f230abacc003");
                bleManagerControl.startScan();
                sliderView.goToIndex(bleManagerControl.isEnabled() ? 1 : 0);
            }
        });

        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, onBackPressedListener);
    }

    private final OnBackInvokedCallback onBackPressedListener = new OnBackInvokedCallback() {
        @Override
        public void onBackInvoked() {
            if (isFinishing()) return;
            if (!sliderView.onBackPressed()) return;
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                if (isFinishing()) return;
                sliderView.goToIndex(1);
                bleManagerControl.startScan();
                return;
            }
            Notify.showToast(R.string.canceled);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) dialog.dismiss();
        getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(onBackPressedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bleManagerControl != null) {
            if (bleManagerControl.isConnected()) {
                startActivity(new Intent(MainActivity.this, DataViewerActivity.class));
                finish();
                return;
            }
            if (sliderView.getCurrentIndex() == 1) bleManagerControl.startScan();
            bleManagerControl.setCallback(callback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderView.getCurrentIndex() == 1) bleManagerControl.stopScan();
    }

    private final BleManagerControl.Callback callback = new BleManagerControl.Callback() {

        @Override
        public void onStateChanged(boolean enabled) {
            Utils.vibrate();
            fragmentBleScan.changeEnabled(enabled);
            if (enabled) {
                if (dialog != null) dialog.dismiss();
            } else {
                sliderView.goToIndex(0);
                dialog.show(getString(R.string.bluetooth_disabled), new DialogInformation.Callback() {
                    @Override
                    public void onClick() {
                    }
                });
            }
        }

        @Override
        public void onDevicesAvailable(ScannedBleDevice[] devices) {
            Utils.vibrate();
            fragmentBleScan.updateList(devices);
        }

        @Override
        public void onConnectionChanged(boolean connected) {
            if (!connected) {
                Utils.vibrate();
                Notify.showToast(R.string.disconnected);
                sliderView.goToIndex(bleManagerControl.isEnabled() ? 1 : 0);
                bleManagerControl.startScan();
            }
        }

        @Override
        public void onServicesDiscovered(boolean success) {
            Utils.vibrate();
            if (!success) {
                dialog.show(getString(R.string.bluetooth_services_error), new DialogInformation.Callback() {
                    @Override
                    public void onClick() {
                        bleManagerControl.disconnect();
                        sliderView.goToIndex(bleManagerControl.isEnabled() ? 1 : 0);
                        if (bleManagerControl.isEnabled()) bleManagerControl.startScan();
                    }
                });
                return;
            }
            startActivity(new Intent(MainActivity.this, DataViewerActivity.class));
            finish();
        }

        @Override
        public void onReceive(byte[] data) {
        }
    };
}