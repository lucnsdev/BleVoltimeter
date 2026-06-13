package lucns.blevoltimeter.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import lucns.blevoltimeter.R;
import lucns.blevoltimeter.services.DataController;

public class WaveView extends View {

    private Paint paintLine, paintLineWave, paintText;

    private int[] amplitudes;
    private double compensation = 100.0d;
    private final Rect bounds = new Rect();

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) init();
    }

    public WaveView(Context context) {
        super(context);
        if (!isInEditMode()) init();
    }

    private void init() {
        paintLine = new Paint();
        paintLine.setColor(getContext().getColor(R.color.gray));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLineWave = new Paint();
        paintLineWave.setColor(getContext().getColor(R.color.accent));
        paintLineWave.setStyle(Paint.Style.STROKE);
        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(24f);
        paintText.setAntiAlias(true);
    }

    public void put(int value) {
        if (amplitudes == null) {
            amplitudes = new int[]{value};
        } else {
            int[] a = new int[amplitudes.length + 1];
            for (int i = 0; i < amplitudes.length; i++) a[i] = amplitudes[i];
            a[amplitudes.length] = value;
            amplitudes = a;
        }
        invalidate();
    }

    public void setValues(int[] values) {
        amplitudes = values;
        invalidate();
    }

    public void reset() {
        amplitudes = null;
        invalidate();
    }

    public void setCompensation(double compensation) {
        this.compensation = compensation;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfHeight = getHeight() / 2;
        canvas.drawLine(0, halfHeight, getWidth(), halfHeight, paintLine);

        if (amplitudes != null) {
            int maxAmplitude = 0;
            int length = Math.min(amplitudes.length, getWidth());
            for (int x = 0; x < length; x++) {
                int current = amplitudes[x + Math.max(amplitudes.length - getWidth(), 0)];
                if (current > maxAmplitude) maxAmplitude = current;
            }
            drawWaveForm(canvas, length, maxAmplitude);
            double voltage = maxAmplitude * compensation * DataController.getResolutionVoltage();
            String text;
            if (voltage < 1) {
                text = ((int) (voltage * 1000)) + "mV";
            } else {
                text = String.format(Locale.getDefault(), "%05.3fV", voltage);
            }
            paintText.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, 16, 8 + bounds.height(), paintText);
        }
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintLine);
    }

    private void drawWaveForm(Canvas canvas, int length, int maxAmplitude) {
        float pixelsPerAmplitude = (float) getHeight() / maxAmplitude;
        for (int x = 0; x < length; x++) {
            float current = amplitudes[x + Math.max(amplitudes.length - getWidth(), 0)] * pixelsPerAmplitude;
            if (current < 1) current = 1;
            canvas.drawLine(x, getHeight() - current, x, getHeight(), paintLineWave);
        }
    }
}
