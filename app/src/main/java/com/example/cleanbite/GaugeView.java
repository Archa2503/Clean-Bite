package com.example.cleanbite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;
    private static final int ANGLE_START = 135;
    private static final int ANGLE_SWEEP = 270;
    private static final int ANIMATION_DURATION = 3000; // 3 seconds
    private static final int ANIMATION_CYCLES = 3;

    private int value;
    private float animationValue;
    private long animationStartTime;
    private boolean isAnimating;
    private String toxicityLevel;

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
    }

    public void setValue(int value, String toxicityLevel) {
        this.value = Math.max(MIN_VALUE, Math.min(value, MAX_VALUE));
        this.toxicityLevel = toxicityLevel;
        animationValue = 0;
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
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
            animationValue = cycle < 2 ? MAX_VALUE * (cycle < 1 ? progress : 2 - progress) : 0;
            if (progress >= 1f) {
                isAnimating = false;
                animationValue = value;
            }
        } else {
            animationValue = value;
        }

        // Draw the arc
        drawArc(canvas, arcRect, animationValue);

        // Draw the needle
        float needleAngle = ANGLE_START + (ANGLE_SWEEP * animationValue / MAX_VALUE);
        float needleX = width / 2 + (width / 3) * (float) Math.cos(Math.toRadians(needleAngle));
        float needleY = height / 2 + (height / 3) * (float) Math.sin(Math.toRadians(needleAngle));
        canvas.drawLine(width / 2, height / 2, needleX, needleY, needlePaint);

        // Draw the value text
        String valueText = String.valueOf((int) animationValue);
        canvas.drawText(valueText, width / 2, height / 2 + textPaint.getTextSize() / 2, textPaint);

        if (isAnimating) {
            invalidate();
        }
    }

    private void drawArc(Canvas canvas, RectF rect, float value) {
        int startColor, middleColor, endColor;

        switch (toxicityLevel.toLowerCase()) {
            case "low":
                startColor = Color.GREEN;
                middleColor = Color.GREEN;
                endColor = Color.GREEN;
                break;
            case "medium":
                startColor = Color.GREEN;
                middleColor = Color.YELLOW;
                endColor = Color.YELLOW;
                break;
            case "high":
                startColor = Color.RED;
                middleColor = Color.RED;
                endColor = Color.RED;
                break;
            default:
                startColor = Color.GREEN;
                middleColor = Color.YELLOW;
                endColor = Color.RED;
                break;
        }

        Paint paint = new Paint(arcPaint);
        float sweepAngle = ANGLE_SWEEP * value / MAX_VALUE;

        // Draw the start color arc
        paint.setColor(startColor);
        canvas.drawArc(rect, ANGLE_START, sweepAngle * 0.5f, false, paint);

        // Draw the middle color arc
        paint.setColor(middleColor);
        canvas.drawArc(rect, ANGLE_START + sweepAngle * 0.5f, sweepAngle * 0.25f, false, paint);

        // Draw the end color arc
        paint.setColor(endColor);
        canvas.drawArc(rect, ANGLE_START + sweepAngle * 0.75f, sweepAngle * 0.25f, false, paint);
    }
}