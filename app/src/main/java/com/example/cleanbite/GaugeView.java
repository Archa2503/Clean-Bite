package com.example.cleanbite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private static final int MAX_VALUE = 5;
    private static final int MIN_VALUE = 0;
    private static final int ANGLE_START = 135;
    private static final int ANGLE_SWEEP = 180;
    private static final int ANIMATION_DURATION = 1000; // 1 second
    private static final int ANIMATION_CYCLES = 3;

    private float value;
    private float animationValue;
    private long animationStartTime;
    private boolean isAnimating;

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint textPaint;

    private RectF arcRect;

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        arcPaint = new Paint();
        arcPaint.setStrokeWidth(30f);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setAntiAlias(true);

        needlePaint = new Paint();
        needlePaint.setColor(Color.BLACK);
        needlePaint.setStrokeWidth(5f);
        needlePaint.setStrokeCap(Paint.Cap.ROUND);
        needlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        arcRect = new RectF();

        animationValue = 0;
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
    }

    public void setValue(float value) {
        this.value = Math.max(MIN_VALUE, Math.min(value, MAX_VALUE));
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float arcPadding = arcPaint.getStrokeWidth() / 2;
        arcRect.set(arcPadding, arcPadding, w - arcPadding, h - arcPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (isAnimating) {
            long elapsedTime = System.currentTimeMillis() - animationStartTime;
            float progress = Math.min(elapsedTime / (float) ANIMATION_DURATION, 1f);
            float cycle = (progress * ANIMATION_CYCLES) % ANIMATION_CYCLES;
            animationValue = cycle < 2 ? ANGLE_SWEEP * (cycle < 1 ? progress : 2 - progress) : 0;
            if (progress >= 1f) {
                isAnimating = false;
                animationValue = value * ANGLE_SWEEP / MAX_VALUE;
            }
        } else {
            animationValue = value * ANGLE_SWEEP / MAX_VALUE;
        }

        // Draw the arc
        drawArc(canvas, arcRect, animationValue);

        // Draw the needle
        float needleAngle = ANGLE_START + animationValue;
        float needleX = width / 2 + (width / 3) * (float) Math.cos(Math.toRadians(needleAngle));
        float needleY = height / 2 + (height / 3) * (float) Math.sin(Math.toRadians(needleAngle));
        canvas.drawLine(width / 2, height / 2, needleX, needleY, needlePaint);

        // Draw the value text
        String valueText = String.format("%.1f", value);
        canvas.drawText(valueText, width / 2, height / 2 + textPaint.getTextSize() / 2, textPaint);

        if (isAnimating) {
            invalidate();
        }
    }

    private void drawArc(Canvas canvas, RectF rect, float value) {
        int startColor, endColor;

        if (value <= 2 * ANGLE_SWEEP / MAX_VALUE) {
            startColor = Color.GREEN;
            endColor = Color.GREEN;
        } else if (value <= 3.5 * ANGLE_SWEEP / MAX_VALUE) {
            startColor = Color.GREEN;
            endColor = Color.YELLOW;
        } else {
            startColor = Color.RED;
            endColor = Color.RED;
        }

        Paint paint = new Paint(arcPaint);
        float sweepAngle = value;

        // Draw the start color arc
        paint.setColor(startColor);
        canvas.drawArc(rect, ANGLE_START, sweepAngle * 0.5f, false, paint);

        // Draw the end color arc
        paint.setColor(endColor);
        canvas.drawArc(rect, ANGLE_START + sweepAngle * 0.5f, sweepAngle * 0.5f, false, paint);
    }
}