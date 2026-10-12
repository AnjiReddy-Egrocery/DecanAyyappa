package com.dst.ayyapatelugu.Services;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class ZoomLayout extends FrameLayout {

    private static final float MIN_SCALE = 1f;
    private static final float MAX_SCALE = 3f;

    private float scale = 1f;
    private float tx = 0f, ty = 0f;
    private float lastX, lastY;
    private float lastFocusX, lastFocusY;
    private int touchSlop;
    private ScaleGestureDetector scaleDetector;

    public ZoomLayout(Context context) {
        super(context);
        init(context);
    }

    public ZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        lastFocusX = d.getFocusX();
                        lastFocusY = d.getFocusY();
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector d) {
                        float newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale * d.getScaleFactor()));
                        float ratio = newScale / scale;
                        float fx = d.getFocusX(), fy = d.getFocusY();

                        // finger madhya point daggara zoom + two-finger pan
                        tx = fx - (fx - tx) * ratio + (fx - lastFocusX);
                        ty = fy - (fy - ty) * ratio + (fy - lastFocusY);
                        scale = newScale;
                        lastFocusX = fx;
                        lastFocusY = fy;
                        applyTransform();
                        return true;
                    }
                });
    }

    private void applyTransform() {
        View child = getChildAt(0);
        if (child == null) return;

        tx = Math.min(0f, Math.max(getWidth() * (1 - scale), tx));
        ty = Math.min(0f, Math.max(getHeight() * (1 - scale), ty));

        child.setPivotX(0f);
        child.setPivotY(0f);
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setTranslationX(tx);
        child.setTranslationY(ty);
    }

    // ScrollView edge ki vachaka matrame vertical ga pan cheyali
    private boolean canPanY(float dy) {
        View child = getChildAt(0);
        if (child == null) return false;
        if (dy > 0) return ty < 0f && !child.canScrollVertically(-1);
        if (dy < 0) return ty > getHeight() * (1 - scale) && !child.canScrollVertically(1);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                return true; // 2nd finger = pinch
            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() > 1) return true;
                if (scale > 1f) {
                    float dx = ev.getX() - lastX;
                    float dy = ev.getY() - lastY;
                    if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) return true;
                    if (Math.abs(dy) > touchSlop && canPanY(dy)) return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                int newIndex = ev.getActionIndex() == 0 ? 1 : 0;
                lastX = ev.getX(newIndex);
                lastY = ev.getY(newIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress() && ev.getPointerCount() == 1 && scale > 1f) {
                    float dx = ev.getX() - lastX;
                    float dy = ev.getY() - lastY;
                    tx += dx;
                    if (canPanY(dy)) ty += dy;
                    applyTransform();
                }
                lastX = ev.getX();
                lastY = ev.getY();
                break;
        }
        return true;
    }
}
