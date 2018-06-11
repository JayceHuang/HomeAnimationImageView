package com.example.shibin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

import com.example.shibin.R;

/**
 * 四个按钮后面的背景栏
 */
public class ImageHomeScrollTabStrip extends LinearLayout {

    private final PageListener pageListener = new PageListener();

    public OnPageChangeListener delegatePageListener;

    private ViewPager pager;

    private int tabCount;

    private OnTabInteractListener mOnTabInteractListener;

    private HomeAnimationImageView[] mHeadTitleViews;

    private final int DEFINE_SIZE;

    private Paint mPaint;

    private boolean initedIcons = false;

    private final float line_height;

    private final int defalultLineColor = Color.WHITE;

    private int currentPress = -1;
    private boolean isIconPress = false;
    private int lastPress;
    private float mLastY = 0;
    private boolean isTouching = false;

    public void setOnTabClickListener(OnTabInteractListener mOnTabListener) {
        this.mOnTabInteractListener = mOnTabListener;
    }

    public ImageHomeScrollTabStrip(Context context) {
        this(context, null);
    }

    public ImageHomeScrollTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageHomeScrollTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);

        DEFINE_SIZE = 4;
        final float density = getResources().getDisplayMetrics().density;
        //获取自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HomePageIndicator);
        int indicatorColor = a.getColor(R.styleable.HomePageIndicator_lineColor, defalultLineColor);
        a.getDimensionPixelSize(R.styleable.HomePageIndicator_triangleWidth, (int) (16 * density));
        a.getDimensionPixelSize(R.styleable.HomePageIndicator_triangleHeight, (int) (9 * density));
        line_height = a.getDimensionPixelSize(R.styleable.HomePageIndicator_lineHeight, (int) (1 * density));
        a.recycle();//保证以后使用该属性时的一致性

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(indicatorColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(line_height);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeadTitleViews = new HomeAnimationImageView[DEFINE_SIZE];
        mHeadTitleViews[0] = (HomeAnimationImageView) findViewById(R.id.icon_home_imageview);
        mHeadTitleViews[1] = (HomeAnimationImageView) findViewById(R.id.icon_video_imageview);
        mHeadTitleViews[2] = (HomeAnimationImageView) findViewById(R.id.icon_game_imageview);
        mHeadTitleViews[3] = (HomeAnimationImageView) findViewById(R.id.icon_app_imageview);
        for (int i = 0; i < DEFINE_SIZE; i++) {
            mHeadTitleViews[i].setIndex(this, i);
        }
    }

    public OnTabInteractListener getTabClickListener() {
        return mOnTabInteractListener;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        tabCount = pager.getAdapter().getCount();
        if (tabCount != DEFINE_SIZE) {
            throw new RuntimeException();
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!initedIcons) {
                    for (int i = 0; i < DEFINE_SIZE; i++) {
                        mHeadTitleViews[i].initWithSize(getWidth(), getHeight(), DEFINE_SIZE);
                    }
                    initedIcons = true;
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    //触摸事件分发
    public boolean dispatchTouchEvent(MotionEvent event) {
        float w = ((View) mHeadTitleViews[1].getParent()).getMeasuredWidth(), h = ((View) mHeadTitleViews[1].getParent()).getMeasuredHeight();
        currentPress = Math.min((int) (event.getX() / w), mHeadTitleViews.length - 1);
        currentPress = Math.max(0, currentPress);

        float density = getResources().getDisplayMetrics().density, maxDistance = 784 * density * density;// 28dp * 28dp
        float dx = (currentPress + 0.5f) * w - event.getX(), dy = 0.5f * h - event.getY();
        float distance_2 = (dx * dx + dy * dy);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                mLastY = event.getY();
                if (distance_2 < maxDistance) {
                    isIconPress = true;
                    mHeadTitleViews[currentPress].commitScaleIn();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float y = event.getY();
                float offsetY = y - mLastY;
                if (isTouching && Math.abs(offsetY) > 30) {
                    mOnTabInteractListener.onSwip(offsetY < 0);
                }
                isTouching = false;

                if (isIconPress) {
                    mOnTabInteractListener.onclick(currentPress);
                    if (lastPress == currentPress) {
                        mHeadTitleViews[currentPress].commitScaleOut();
                    }
                    lastPress = currentPress;
                    //Log.d("mzh", "ACTION_UP "+currentPress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (distance_2 > maxDistance && isIconPress) {
                    isIconPress = false;
                    mHeadTitleViews[currentPress].commitScaleOut();
                }
                if (isIconPress) return true;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode() || tabCount == 0 || !initedIcons) {
            return;
        }
        canvas.save();
        canvas.translate(0, getHeight() / 2);
        drawCircles(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < DEFINE_SIZE; i++) {
            mHeadTitleViews[i].draw(canvas, mPaint);
        }
    }

    private class PageListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            scroll(position, positionOffset);
            invalidate();
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
            for (int i = 0; i < DEFINE_SIZE; i++) {
                mHeadTitleViews[i].stateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
            for (int i = 0; i < DEFINE_SIZE; i++) {
                mHeadTitleViews[i].pageChanged(position);
            }
        }
    }

    /**
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        for (int i = 0; i < DEFINE_SIZE; i++) {
            mHeadTitleViews[i].update(position, offset);
        }
    }

    public interface OnTabInteractListener {
        void onclick(int pisition);

        void onSwip(boolean isUp);
    }
}
