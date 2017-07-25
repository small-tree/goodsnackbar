package com.example.goodsnackbar.mysnackbar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.HID;
import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.HIDING;
import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.SHOW;
import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.SHOWING;

/**
 * Created by Administrator on 2017/3/9.
 */

public class GoodSnackbar {

    private static final String TAG = "GoodSnackbar";
    private static final int ANIM_END = 100;


    OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    /**
     * SHOWING 显示动画正在执行
     * SHOW  snackbar 显示状态
     * HIDING  隐藏动画正在执行
     * HID snackbar 隐藏状态
     */
    private Status status = HID;
    private From from;
    ViewGroup parentView;
    int duration = 2500;
    private View myView;

    enum Status {
        SHOWING, SHOW, HIDING, HID
    }

    public enum From {
        TOP(Gravity.TOP), BOTTOM(Gravity.BOTTOM);

        From(int i) {
            gravity = i;
        }

        int gravity;
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ANIM_END:
                    if (getStatus() == SHOW) {
                        hideLayoutOut();
                    }
                    break;
            }
        }
    };

    private Status getStatus() {
        return status;
    }

    private static MySnackbarBaseLayout baseLayout;


    private GoodSnackbar(ViewGroup parentView) {
        this.parentView = parentView;
        baseLayout = new MySnackbarBaseLayout(parentView.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        baseLayout.setLayoutParams(layoutParams);
    }

    /**
     * set a view to display
     *
     * @param view GoodSnackBar's contentview.
     */
    public GoodSnackbar setMyView(View view) {
        myView = view;
        baseLayout.removeAllViews();
        baseLayout.addView(myView);
        return this;
    }

    /**
     *
     */
    public View getMyView() {
        return myView;
    }

    /**
     * set GoodSnackBar display time.
     *
     * @param duration set GoodSnackBar display time (millisecond).
     */
    public GoodSnackbar setDuration(int duration) {
        this.duration = duration;
        return this;
    }


    public static GoodSnackbar make(FrameLayout parent) {
        return new GoodSnackbar(parent);
    }

    public GoodSnackbar setWhereFrom(From from) {
        this.from = from;
        return this;
    }

    public View getView() {
        return myView;
    }

    public GoodSnackbar show() {
        if (myView == null) {
            throw new IllegalStateException("must add a view through setMyView() method.");
        }
        parentView.addView(baseLayout);
        snackbarLocal();
        if (ViewCompat.isLaidOut(baseLayout)) {
            showLayoutIn();
        } else {
            baseLayout.setOnLayoutListener(new MySnackbarBaseLayout.OnLayoutListener() {
                @Override
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
                    baseLayout.setOnLayoutListener(null);
                    showLayoutIn();
                }
            });
        }
        Log.i(TAG, "show: viewcompat.islaidout" + ViewCompat.isLaidOut(myView));
        return this;
    }

    public void close() {
        hideLayoutOut();
    }

    /**
     * 根据from 属性设置view的位置
     */
    private void snackbarLocal() {
        switch (from) {
            case TOP:
            case BOTTOM:
                ViewGroup.LayoutParams params1 = baseLayout.getLayoutParams();
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params1.height = getViewHeight(myView);
                if (parentView instanceof FrameLayout) {
                    ((FrameLayout.LayoutParams) params1).gravity = from.gravity;
                } else if (parentView instanceof CoordinatorLayout) {
                    ((CoordinatorLayout.LayoutParams) params1).gravity = from.gravity;
                } else {
                    throw new IllegalArgumentException("parent view not Fragment or CoordinatorLayout.");
                }
                baseLayout.setLayoutParams(params1);
                break;
        }
    }

    private int getViewHeight(View view) {
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return view.getMeasuredHeight();
    }

    /**
     * 根据from 属性设置弹出动画
     */
    private void showLayoutIn() {
        if (status == HID) {
            status = SHOWING;
        } else {
            return;
        }
        ViewPropertyAnimatorCompat compat = ViewCompat.animate(myView)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(250);

        switch (from) {
            case TOP:
                ViewCompat.setTranslationY(myView, -myView.getMeasuredHeight());
                compat.translationY(0);
                break;
            case BOTTOM:
                ViewCompat.setTranslationY(myView, myView.getMeasuredHeight());
                compat.translationY(0);
                break;
        }
        compat.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
            }

            @Override
            public void onAnimationEnd(View view) {
                Log.i(TAG, "onAnimationEnd: ");
                status = SHOW;
                handler.sendEmptyMessageDelayed(ANIM_END, duration);
            }

            @Override
            public void onAnimationCancel(View view) {
            }
        }).start();

    }

    /**
     * 根据form 属性设置出去动画
     */
    private void hideLayoutOut() {
        if (status == SHOW) {
            status = HIDING;
        } else {
            return;
        }
        ViewPropertyAnimatorCompat compat = ViewCompat.animate(myView)
                .setDuration(250)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        status = HID;
                        if (baseLayout != null) {
                            parentView.removeView(baseLayout);
                        }
                        if (onDismissListener != null) {
                            onDismissListener.dismiss();
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                    }
                });
        switch (from) {
            case TOP:
                compat.translationY(-myView.getMeasuredWidth());
                break;
            case BOTTOM:
                compat.translationY(myView.getMeasuredWidth());
                break;
        }
        compat.start();
    }


    public static int dp2px(Context context, float dpValue) {
        if (dpValue <= 0) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    interface OnDismissListener {
        void dismiss();
    }


    static class MySnackbarBaseLayout extends FrameLayout {

        private static final String TAG = "MySnackbarBaseLayout";

        OnAttachedToWindowListener onAttachedToWindowListener;
        OnDetachedFromWindowListener onDetachedFromWindowListener;
        OnLayoutListener onLayoutListener;

        public MySnackbarBaseLayout(Context context) {
            this(context, null);
        }

        public MySnackbarBaseLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MySnackbarBaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setOnAttachedToWindowListener(OnAttachedToWindowListener onAttachedToWindowListener) {
            this.onAttachedToWindowListener = onAttachedToWindowListener;
        }

        public void setOnDetachedFromWindowListener(OnDetachedFromWindowListener onDetachedFromWindowListener) {
            this.onDetachedFromWindowListener = onDetachedFromWindowListener;
        }

        public void setOnLayoutListener(OnLayoutListener onLayoutListener) {
            this.onLayoutListener = onLayoutListener;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Log.i(TAG, "onAttachedToWindow: ");
            if (onDetachedFromWindowListener != null) {
                onDetachedFromWindowListener.onDetachedFromWindow();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Log.i(TAG, "onDetachedFromWindow: ");
            if (onDetachedFromWindowListener != null) {
                onDetachedFromWindowListener.onDetachedFromWindow();
            }
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
            if (onLayoutListener != null) {
                onLayoutListener.onLayout(changed, left, top, right, bottom);
            }
        }

        interface OnAttachedToWindowListener {
            void onAttachedToWindow();
        }

        interface OnDetachedFromWindowListener {
            void onDetachedFromWindow();
        }

        interface OnLayoutListener {
            void onLayout(boolean changed, int left, int top, int right, int bottom);
        }

    }

}
