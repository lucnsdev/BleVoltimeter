package lucns.blevoltimeter.activities;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lucns.blevoltimeter.R;

public class DialogInformation extends Dialog {

    public interface Callback {
        void onClick();
    }

    private Callback callback;

    public DialogInformation(Activity activity) {
        super(activity, R.style.DialogTheme);
        setCancelable(false);
        setContentView(R.layout.dialog_info);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onClick();
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void show(String title, Callback callback) {
        this.callback = callback;
        ((TextView) findViewById(R.id.textTitle)).setText(title);
        super.show();
    }

    public void show(String title) {
        ((TextView) findViewById(R.id.textTitle)).setText(title);
        super.show();
    }
}
