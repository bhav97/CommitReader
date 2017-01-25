package bhav.commit.ui.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapted From the AVLoadingIndicator Library
 */
public class LoadingGrid extends View {

    int mMinWidth;
    int mMaxWidth;
    int mMinHeight;
    int mMaxHeight;
    private Grid balls;
    private int mIndicatorColor;
    private boolean mShouldStartAnimationDrawable;

    public LoadingGrid(Context context) {
        super(context);
        init();
    }

    public LoadingGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LoadingGrid(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mMinWidth = 24;
        mMaxWidth = 48;
        mMinHeight = 24;
        mMaxHeight = 48;

        mIndicatorColor = Color.WHITE;
        setIndicator(new Grid());

    }

    public void setIndicator(Grid d) {
        if (balls != d) {
            if (balls != null) {
                balls.setCallback(null);
                unscheduleDrawable(balls);
            }

            balls = d;
            //need to set indicator color again if you didn't specified when you update the indicator .
            setIndicatorColor(mIndicatorColor);
            if (d != null) {
                d.setCallback(this);
            }
            postInvalidate();
        }
    }

    public void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        balls.setColor(color);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return who == balls
                || super.verifyDrawable(who);
    }

    void startAnimation() {
        if (getVisibility() != VISIBLE) {
            return;
        }

        if (balls != null) {
            mShouldStartAnimationDrawable = true;
        }
        postInvalidate();
    }

    void stopAnimation() {
        if (balls != null) {
            balls.stop();
            mShouldStartAnimationDrawable = false;
        }
        postInvalidate();
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable dr) {
        if (verifyDrawable(dr)) {
            final Rect dirty = dr.getBounds();
            final int scrollX = getScrollX() + getPaddingLeft();
            final int scrollY = getScrollY() + getPaddingTop();

            invalidate(dirty.left + scrollX, dirty.top + scrollY,
                    dirty.right + scrollX, dirty.bottom + scrollY);
        } else {
            super.invalidateDrawable(dr);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateDrawableBounds(w, h);
    }

    private void updateDrawableBounds(int w, int h) {
        // onDraw will translate the canvas so we draw starting at 0,0.
        // Subtract out padding for the purposes of the calculations below.
        w -= getPaddingRight() + getPaddingLeft();
        h -= getPaddingTop() + getPaddingBottom();

        int right = w;
        int bottom = h;
        int top = 0;
        int left = 0;

        if (balls != null) {
            // Maintain aspect ratio. Certain kinds of animated drawables
            // get very confused otherwise.
            final int intrinsicWidth = balls.getIntrinsicWidth();
            final int intrinsicHeight = balls.getIntrinsicHeight();
            final float intrinsicAspect = (float) intrinsicWidth / intrinsicHeight;
            final float boundAspect = (float) w / h;
            if (intrinsicAspect != boundAspect) {
                if (boundAspect > intrinsicAspect) {
                    // New width is larger. Make it smaller to match height.
                    final int width = (int) (h * intrinsicAspect);
                    left = (w - width) / 2;
                    right = left + width;
                } else {
                    // New height is larger. Make it smaller to match width.
                    final int height = (int) (w * (1 / intrinsicAspect));
                    top = (h - height) / 2;
                    bottom = top + height;
                }
            }
            balls.setBounds(left, top, right, bottom);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas);
    }

    void drawTrack(Canvas canvas) {
        final Drawable d = balls;
        if (d != null) {
            // Translate canvas so a indeterminate circular progress bar with padding
            // rotates properly in its animation
            final int saveCount = canvas.save();

            canvas.translate(getPaddingLeft(), getPaddingTop());

            d.draw(canvas);
            canvas.restoreToCount(saveCount);

            if (mShouldStartAnimationDrawable) {
                ((Animatable) d).start();
                mShouldStartAnimationDrawable = false;
            }
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh = 0;

        final Drawable d = balls;
        if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.getIntrinsicHeight()));
        }

        updateDrawableState();

        dw += getPaddingLeft() + getPaddingRight();
        dh += getPaddingTop() + getPaddingBottom();

        final int measuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
        final int measuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    private void updateDrawableState() {
        final int[] state = getDrawableState();
        if (balls != null && balls.isStateful()) {
            balls.setState(state);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);

        if (balls != null) {
            balls.setHotspot(x, y);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        // This should come after stopAnimation(), otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow();
    }

    class Grid extends Drawable implements Animatable {

        private static final int ALPHA = 255;
        private static final float SCALE = 1.0f;
        private Rect drawBounds = new Rect();
        private HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new HashMap<>();
        private ArrayList<ValueAnimator> mAnimators;
        private int alpha = 255;
        private boolean mHasAnimators;
        private int[] alphas = new int[]{
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA,
                ALPHA
        };

        private float[] scaleFloats = new float[]{
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE,
                SCALE
        };

        private Paint mPaint = new Paint();
        private long pauseTime;

        Grid() {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }

        public int getColor() {
            return mPaint.getColor();
        }

        public void setColor(int color) {
            mPaint.setColor(color);
        }

        @Override
        public int getAlpha() {
            return alpha;
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            draw(canvas, mPaint);
        }

        private void draw(Canvas canvas, Paint paint) {
            float circleSpacing = 8;
            float radius = (getWidth() - circleSpacing * 6) / 10;
            float x = getWidth() / 2 - (radius * 3 + circleSpacing);
            float y = getWidth() / 2 - (radius * 3 + circleSpacing);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    canvas.save();
                    float translateX = x + (radius * 2) * j + circleSpacing * j;
                    float translateY = y + (radius * 2) * i + circleSpacing * i;
                    canvas.translate(translateX, translateY);
                    canvas.scale(scaleFloats[3 * i + j], scaleFloats[3 * i + j]);
                    paint.setAlpha(alphas[3 * i + j]);
                    canvas.drawCircle(0, 0, radius, paint);
                    canvas.restore();
                }
            }
        }

        private ArrayList<ValueAnimator> onCreateAnimators() {
            ArrayList<ValueAnimator> animators = new ArrayList<>();
            int[] durations = {720, 1020, 1280, 1420, 1450, 1180, 870, 1450, 1060, 720, 1020, 1280, 1420, 1450, 1180, 870};
            int[] delays = {60, 250, 0, 480, 310, 30, 460, 780, 450, 0, 480, 310, 30, 460, 780, 450};

            for (int i = 0; i < 16; i++) {
                final int index = i;
                ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.5f, 1);
                scaleAnim.setDuration(durations[i]);
                scaleAnim.setRepeatCount(ValueAnimator.INFINITE);
                scaleAnim.setStartDelay(delays[i]);
                addUpdateListener(scaleAnim, animation -> {
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                });

                ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 210, 122, 255);
                alphaAnim.setDuration(durations[i]);
                alphaAnim.setRepeatCount(ValueAnimator.INFINITE);
                alphaAnim.setStartDelay(delays[i]);
                addUpdateListener(alphaAnim, animation -> {
                    alphas[index] = (int) animation.getAnimatedValue();
                    postInvalidate();
                });
                animators.add(scaleAnim);
                animators.add(alphaAnim);
            }
            return animators;
        }

        @Override
        public void start() {
            ensureAnimators();

            if (mAnimators == null) {
                return;
            }

            // If the animators has not ended, do nothing.
            if (isStarted()) {
                return;
            }
            startAnimators();
            invalidateSelf();
        }

        private void startAnimators() {
            for (int i = 0; i < mAnimators.size(); i++) {
                ValueAnimator animator = mAnimators.get(i);

                //when the animator restart , add the updateListener again because they
                // was removed by animator stop .
                ValueAnimator.AnimatorUpdateListener updateListener = mUpdateListeners.get(animator);
                if (updateListener != null) {
                    animator.addUpdateListener(updateListener);
                }
                animator.start();
                animator.setCurrentPlayTime(pauseTime);
            }
        }

        private void stopAnimators() {
            if (mAnimators != null) {
                for (ValueAnimator animator : mAnimators) {
                    if (animator != null && animator.isStarted()) {
                        animator.removeAllUpdateListeners();
                        pauseTime = animator.getCurrentPlayTime();
                        animator.cancel();
                    }
                }
            }
        }

        private void ensureAnimators() {
            if (!mHasAnimators) {
                mAnimators = onCreateAnimators();
                mHasAnimators = true;
            }
        }

        @Override
        public void stop() {
            stopAnimators();
        }

        private boolean isStarted() {
            for (ValueAnimator animator : mAnimators) {
                return animator.isStarted();
            }
            return false;
        }

        @Override
        public boolean isRunning() {
            for (ValueAnimator animator : mAnimators) {
                return animator.isRunning();
            }
            return false;
        }

        private void addUpdateListener(ValueAnimator animator, ValueAnimator.AnimatorUpdateListener updateListener) {
            mUpdateListeners.put(animator, updateListener);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            setDrawBounds(bounds);
        }

        private void setDrawBounds(int left, int top, int right, int bottom) {
            this.drawBounds = new Rect(left, top, right, bottom);
        }

        private void postInvalidate() {
            invalidateSelf();
        }

        private void setDrawBounds(Rect drawBounds) {
            setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom);
        }

        private int getWidth() {
            return drawBounds.width();
        }
    }
}