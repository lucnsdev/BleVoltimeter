package lucns.blevoltimeter.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import lucns.blevoltimeter.ble.BleManagerControl;

public class MainService extends Service {

    public class LocalBinder extends Binder {
        public MainService getServiceInstance() {
            return MainService.this;
        }
    }

    private LocalBinder iBinder;
    private BleManagerControl bleManagerControl;

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        iBinder = new LocalBinder();
        bleManagerControl = new BleManagerControl(this, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iBinder = null;
        bleManagerControl.close();
    }

    public BleManagerControl getBleManagerControl() {
        return bleManagerControl;
    }
}
