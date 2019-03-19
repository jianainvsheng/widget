package com.source.sdk.widget.leve;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.source.sdk.widget.R;

/**
 * 等级进度条
 * Created by bfshao on 2017/11/22.
 */

public class LevelProgressBar extends ProgressBar {
    private final int EMPTY_MESSAGE = 1;

    /*xml中的自定义属性*/
    private int progressStartColor;
    private int progressEndColor;
    private int progressBgColor;
    private int progressHeight;

    /*代码中需要设置的属性*/
    private int levels;
    private int currentLevel;
    private int animInterval; // 动画时间间隔
    private int animMaxTime;  // 动画最大时长
    private int targetProgress;

    private Paint mPaint;
    private int mTotalWidth;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = getProgress();
            // 小于目标值时增加进度，大于目标值时减小进度
            if (progress < targetProgress) {
                setProgress(++progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else if (progress > targetProgress){
                setProgress(--progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else {
                handler.removeMessages(EMPTY_MESSAGE);
            }
        }
    };
    private ValueAnimator animator;

    public LevelProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取xml中设置的属性值
        obtainStyledAttributes(attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LevelProgressBar);
        progressStartColor = a.getColor(R.styleable.LevelProgressBar_progressStartColor, 0xCCFFCC);
        progressEndColor = a.getColor(R.styleable.LevelProgressBar_progressEndColor, 0x00FF00);
        progressBgColor = a.getColor(R.styleable.LevelProgressBar_progressBgColor, 0x000000);
        progressHeight = (int) a.getDimension(R.styleable.LevelProgressBar_progressHeight, dpTopx(20));
        a.recycle();
    }

    private int dpTopx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    // 设置等级数
    public void setLevels(int levels) {
        this.levels = levels;
    }

    // 设置当前等级
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        this.targetProgress = (int) (level * 1f / levels * getMax());
    }

    // 设置动画间隔，每隔animInterval秒进度+1或-1
    public void setAnimInterval(final int animInterval) {
        this.animInterval = animInterval;
        handler.sendEmptyMessage(EMPTY_MESSAGE);
    }

    public void setProgress(int progressStartColor,int progressEndColor){

        this.progressStartColor = progressStartColor;
        this.progressEndColor = progressEndColor;
        invalidate();
    }
    // 设置动画时长，动画从0到最高级所需的时间
    public void setAnimMaxTime(int animMaxTime) {
        this.animMaxTime = animMaxTime;
        animator = ValueAnimator.ofInt(getProgress(), targetProgress);
        animator.setDuration(animMaxTime * Math.abs(getProgress() - targetProgress) / getMax());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = getProgress();
                // 小于目标值时增加进度，大于目标值时减小进度
                if (progress < targetProgress || progress > targetProgress) {
                    setProgress((Integer) animation.getAnimatedValue());
                }
            }
        });
        animator.start();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // layout_height为wrap_content时计算View的高度
        if (heightMode != MeasureSpec.EXACTLY) {
            // 10dp为等级文字和进度条之间的间隔
            height = getPaddingTop() + getPaddingBottom() + progressHeight;
        }

        setMeasuredDimension(width, height);

        mTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // 留出padding的位置
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        int lineY =  progressHeight / 2;

        // 绘制进度条底部
        mPaint.setColor(progressBgColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(progressHeight);
        canvas.drawLine(0 + progressHeight / 2, lineY, mTotalWidth - progressHeight / 2, lineY, mPaint);

        // 绘制进度条
        int reachedPartEnd = (int) (getProgress() * 1.0f / getMax() * mTotalWidth);
        if (reachedPartEnd > 0) {
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            // 设置进度条的渐变色
            Shader shader = new LinearGradient(0, lineY,
                    getWidth(), lineY,
                    progressStartColor, progressEndColor, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            int accurateEnd = reachedPartEnd - progressHeight / 2;
            int accurateStart = 0 + progressHeight / 2;
            if (accurateEnd > accurateStart) {
                canvas.drawLine(accurateStart, lineY, accurateEnd, lineY, mPaint);
            } else {
                canvas.drawLine(accurateStart, lineY, accurateStart, lineY, mPaint);
            }
            mPaint.setShader(null);
        }

        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
