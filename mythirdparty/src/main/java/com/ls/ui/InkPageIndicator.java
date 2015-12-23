package com.ls.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Created by user on 15-11-23.
 */
public class InkPageIndicator extends View implements ViewPager.OnPageChangeListener {

    //default
    private static final int DEFAULT_DOT_SIZE = 8;                      // dp
    private static final int DEFAULT_GAP = 12;                          // dp
    private static final int DEFAULT_UNSELECTED_COLOUR = 0x80ffffff;    // 50% white
    private static final int DEFAULT_SELECTED_COLOUR = 0xffffffff;      // 100% white

    // constants
    private static final float INVALID_FRACTION = -1f;
    private static final float MINIMAL_REVEAL = 0.00001f;

    //viewpager
    private ViewPager mViewPager;
    private int mPageCount;
    private boolean mIsAttachToWindow;

    //attributes
    private int dotRadius;
    private int halfDotRadius;
    private int dotDiameter;
    private int gap;

    //drawing
    private final Paint unselectedPaint;
    private final Paint selectedPaint;
    private final Path combinedUnselectedPath;
    private final Path unselectedDotPath;
    private final Path unselectedDotLeftPath;
    private final Path unselectedDotRightPath;
    private final RectF rectF;


    //state
    private int currentPage;
    private int previousPage;
    private boolean pageChanging;
    private float[] dotCenterX;
    private float dotCenterY;
    private float dotTopY;
    private float dotBottomY;
    private float selectedDotX;
    private boolean selectedDotInPosition;
    private float[] joiningFractions;
    private float[] dotRevealFractions;
    private float retreatingJoinX1;
    private float retreatingJoinX2;

    // working values for beziers
    float endX1;
    float endY1;
    float endX2;
    float endY2;
    float controlX1;
    float controlY1;
    float controlX2;
    float controlY2;



    public InkPageIndicator(Context context) {
        this(context, null, 0);
    }

    public InkPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public InkPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final int density = (int) context.getResources().getDisplayMetrics().density;

        dotDiameter = DEFAULT_DOT_SIZE * density;
        dotRadius = dotDiameter / 2;
        halfDotRadius = dotRadius / 2;
        gap = DEFAULT_GAP * density;

        unselectedPaint = new Paint();
        unselectedPaint.setColor(DEFAULT_UNSELECTED_COLOUR);
        unselectedPaint.setAntiAlias(true);
        selectedPaint = new Paint();
        selectedPaint.setColor(DEFAULT_SELECTED_COLOUR);
        selectedPaint.setAntiAlias(true);

        combinedUnselectedPath = new Path();
        unselectedDotPath = new Path();
        unselectedDotLeftPath = new Path();
        unselectedDotRightPath = new Path();
        rectF = new RectF();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachToWindow = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultHeight = getDefaultHeight();
        int height;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY: //wrap
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST: //fill, match
                height = Math.min(defaultHeight, MeasureSpec.getSize(heightMeasureSpec));
                break;
            default:
                height = defaultHeight;
                break;
        }
        int defaultWidth = getDefaultWidth();
        int width;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(defaultWidth, MeasureSpec.getSize(widthMeasureSpec));
                break;
            default: // MeasureSpec.UNSPECIFIED
                width = defaultWidth;
                break;
        }
        setMeasuredDimension(width, height);
        calculateDotePosition(width, height);
    }

    private int getDefaultHeight() {
        return getPaddingTop() + getPaddingBottom() + dotDiameter;
    }

    private int getDefaultWidth() {
        return getPaddingLeft() + getPaddingRight() + getRequiredWidth();
    }

    private int getRequiredWidth() {
        return mPageCount * dotDiameter + (mPageCount - 1) * gap;
    }

    private void calculateDotePosition(int width, int height) {
        int left = getPaddingLeft();
        int right = width - getPaddingRight();
        int top = getPaddingTop();
        int bottom = height - getPaddingBottom();

        int requiredWidth = getRequiredWidth();
        int startLeft = left + dotRadius + ((right - left - requiredWidth) / 2);
        dotCenterX = new float[mPageCount];
        for (int i = 0; i < mPageCount; i++) {
            dotCenterX[i] = startLeft + (dotDiameter + gap) * i;
        }

        dotTopY = top;
        dotCenterY = top + dotRadius;
        dotBottomY = top + dotDiameter;

        setCurrentImmediate();
    }

    private void setCurrentImmediate() {
        if (mViewPager != null) {
            currentPage = mViewPager.getCurrentItem();
        } else {
            currentPage = 0;
        }
        if (dotCenterX != null) {
            selectedDotX = dotCenterX[currentPage];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mViewPager == null || mPageCount == 0) {
            return;
        }

        //绘制未选中的点
        drawUnselected(canvas);
        //绘制选中的点
        drawSelected(canvas);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void drawUnselected(Canvas canvas) {

        combinedUnselectedPath.rewind();
        for (int i = 0; i < mPageCount; i++) { //遍历
            int nextXIndex = i == mPageCount - 1 ? i : i + 1;  // 这里为啥？
            combinedUnselectedPath.op(drawUnselected(i,
                    dotCenterX[i],
                    dotCenterX[nextXIndex],
                    i == mPageCount - 1 ? INVALID_FRACTION : joiningFractions[i],
                    dotRevealFractions[i]), Path.Op.UNION);
        }
        canvas.drawPath(combinedUnselectedPath, unselectedPaint);
    }

    /**
     * 没有被选中的点有6种状态
     * #1 初始状态
     * #2 和相邻点合并ing，还未连接
     * #3 和相邻点合并， 结合处有弧度
     * #4 和相邻点合并， 平行
     * #5
     * #6 重新出现
     * @return
     */
    private Path drawUnselected(int page,
                                float centerX,
                                float nextCenterX,
                                float joiningFraction,
                                float dotRevealFraction) {
        unselectedDotPath.rewind();

        if ((joiningFraction == 0f || joiningFraction == INVALID_FRACTION)
                && dotRevealFraction == 0f
                && !(page == currentPage && selectedDotInPosition)) {
            // case #1
            unselectedDotPath.addCircle(dotCenterX[page], dotCenterY, dotRadius, Path.Direction.CW/*方向*/); // CW / CCW 顺时针/逆时针画圆
        }

        if (joiningFraction > 0f && joiningFraction <=0.5f
                && retreatingJoinX1 == INVALID_FRACTION) {
            //case #2
            //从左侧圆点开始
            unselectedDotLeftPath.rewind();

            //从bottomY && centerX 开始画 ,画左侧半圆
            unselectedDotLeftPath.moveTo(centerX, dotBottomY);
            rectF.set(centerX - dotRadius,dotTopY, centerX + dotRadius, dotBottomY);
            unselectedDotLeftPath.arcTo(rectF, 90, 180, true);

            // cubic to the right middle
            endX1 = centerX + dotRadius + (joiningFraction * gap);
            endY1 = dotCenterY;
            controlX1 = centerX + halfDotRadius;
            controlY1 = dotTopY;
            controlX2 = endX1;
            controlY2 = endY1 - halfDotRadius;
            unselectedDotLeftPath.cubicTo(controlX1, controlY1,
                    controlX2, controlY2,
                    endX1, endY1);


        }

        return unselectedDotPath;
    }

    private void drawSelected(Canvas canvas) {
        canvas.drawCircle(selectedDotX, dotCenterY, dotRadius, selectedPaint);
    }

    public void setupViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        setPageCount(mViewPager.getAdapter().getCount());
        mViewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setPageCount(mViewPager.getAdapter().getCount());
            }
        });
    }

    private void resetState(){
        joiningFractions = new float[mPageCount -1];
        Arrays.fill(joiningFractions, 0);
        dotRevealFractions = new float[mPageCount];
        Arrays.fill(dotRevealFractions, 0f);
    }

    private void setPageCount(int count) {
        mPageCount = count;
        resetState();
        //绘制dote
    }

    private void setSelectedPage(int position) {
        if (currentPage == position) {
            return;
        }
        pageChanging = true;
        setCurrentImmediate();
        postInvalidate();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIsAttachToWindow) {
            float fraction = positionOffset;
            //int correntPosition =
        }
    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("onPageSelected======"+position);
        setSelectedPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
