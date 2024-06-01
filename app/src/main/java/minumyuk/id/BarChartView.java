package minumyuk.id;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private List<BarEntry> entries = new ArrayList<>();
    private Paint barPaint;
    private Paint textPaint;
    private Paint loadingPaint;

    private float loadingBarX = 0;
    private Handler handler = new Handler();
    private Runnable loadingRunnable;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#5DCCFC"));

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#625D5D"));
        textPaint.setTextSize(30f);

        loadingPaint = new Paint();
        loadingPaint.setColor(Color.parseColor("#B7B7B7"));

        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                loadingBarX += 10;
                if (loadingBarX > getWidth()) {
                    loadingBarX = 0;
                }
                invalidate();
                handler.postDelayed(this, 16); // Update every 16ms (~60fps)
            }
        };
        handler.post(loadingRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (entries.isEmpty()) {
            drawLoadingBar(canvas);
        } else {
            drawBars(canvas);
        }
    }

    private void drawLoadingBar(Canvas canvas) {
        float barWidth = getWidth() / 4f;
        float maxBarHeight = getHeight() * 0.8f;
        float left = loadingBarX;
        float top = getHeight() - maxBarHeight;
        float right = left + barWidth;
        float bottom = getHeight();
        canvas.drawRect(left, top, right, bottom, loadingPaint);
    }

    private void drawBars(Canvas canvas) {
        float barWidth = getWidth() / (entries.size() * 2f);
        float maxBarHeight = getHeight() * 0.8f;

        float maxEntryValue = 0;
        for (BarEntry entry : entries) {
            if (entry.getValue() > maxEntryValue) {
                maxEntryValue = entry.getValue();
            }
        }

        for (int i = 0; i < entries.size(); i++) {
            BarEntry entry = entries.get(i);
            float barHeight = (entry.getValue() / maxEntryValue) * maxBarHeight;
            float left = i * 2 * barWidth;
            float top = getHeight() - barHeight;
            float right = left + barWidth;
            float bottom = getHeight();
            canvas.drawRect(left, top, right, bottom, barPaint);
            canvas.drawText(String.valueOf(entry.getValue()), left, top - 10, textPaint);
        }
    }

    public void setEntries(List<BarEntry> entries) {
        this.entries = entries;
        handler.removeCallbacks(loadingRunnable); // Stop the loading animation
        invalidate();
    }

    public static class BarEntry {
        private final float value;

        public BarEntry(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }
}
