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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.goodsnackbar.R;

import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.HID;
import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.SHOWING;
import static com.example.goodsnackbar.mysnackbar.GoodSnackbar.Status.USING;

/**
 * Created by Administrator on 2017/3/9.
 */

public class GoodSnackbar {

    private static final String TAG = "GoodSnackbar";
    private static final int ANIM_END = 100;
    OnActionButtonClickListener onActionButtonClickListener;

    /**
     * 弹出view的宽度 or 高度
     */
    int snackbarHeight;
    /**
     * -1 ,animation is start
     * 0 ,showing
     * 1 ,hid
     */
    private Status status = HID;
    private From from;

    ViewGroup parent;
    String showMsg;
    int duration = 2500;
    private View myView;
    private TextView tv_msg;
    private TextView bt_action;


    static enum Status {
        SHOWING, HID, USING
    }

    public static enum From {
        LEFT(Gravity.LEFT), TOP(Gravity.TOP), RIGHT(Gravity.RIGHT), BOTTOM(Gravity.BOTTOM);

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
                    if (getStatus() == SHOWING) {
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

    private GoodSnackbar(ViewGroup parent, String msg, int duration) {
        this.parent = parent;
        this.showMsg = msg;
        this.duration = duration;
        this.snackbarHeight = dp2px(parent.getContext(), 50);
        baseLayout = new MySnackbarBaseLayout(parent.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        baseLayout.setLayoutParams(layoutParams);
        myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mysnackbar_layout, parent, false);
        setMessage();
        baseLayout.addView(myView);
    }

    public static GoodSnackbar make(FrameLayout parent, String msg, int duration) {
        return new GoodSnackbar(parent, msg, duration);
    }

    /**
     * 获取一个 goodSnackbar 实例
     * @param parent 父控件
     * @param msg 信息
     * @param duration 显示时间
     * @return
     */
    public static GoodSnackbar make(CoordinatorLayout parent, String msg, int duration) {
        return new GoodSnackbar(parent, msg, duration);
    }

    public GoodSnackbar setMessage(String... msg) {
        if (msg != null && msg.length > 0) {
            showMsg = msg[0];
        }
        if (myView != null) {
            if (tv_msg == null) {
                tv_msg = (TextView) myView.findViewById(R.id.tv_msg);
                bt_action = (TextView) myView.findViewById(R.id.bt_action);
            }
            tv_msg.setText(showMsg);
        } else {
            new RuntimeException("myView is null");
        }
        return this;
    }

    public GoodSnackbar setWhereFrom(From from) {
        this.from = from;
        return this;
    }

    public GoodSnackbar setMessageGravity(int gravity) {
        tv_msg.setGravity(gravity);
        return this;
    }

    public GoodSnackbar setAction(final OnActionButtonClickListener listener) {
        if (myView != null) {
            if (tv_msg == null) {
                tv_msg = (TextView) myView.findViewById(R.id.tv_msg);
                bt_action = (TextView) myView.findViewById(R.id.bt_action);
            }
            bt_action.setVisibility(View.VISIBLE);
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick();
                }
            });
        } else {
            new RuntimeException("myView is null");
        }
        return this;
    }

    public View getView() {
        return myView;
    }

    public GoodSnackbar show() {
        parent.addView(baseLayout);
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

    /**
     * 根据from 属性设置view的位置
     */
    private void snackbarLocal() {
        switch (from) {
            case LEFT:
            case RIGHT:
                ViewGroup.LayoutParams params = baseLayout.getLayoutParams();
                params.width = snackbarHeight;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                if (parent instanceof FrameLayout) {
                    ((FrameLayout.LayoutParams) params).gravity = from.gravity;
                } else if (parent instanceof CoordinatorLayout) {
                    ((CoordinatorLayout.LayoutParams) params).gravity = from.gravity;
                }
                baseLayout.setLayoutParams(params);

                if (myView instanceof LinearLayout) {
                    ((LinearLayout) myView).setOrientation(LinearLayout.VERTICAL);
                }
                break;
            case TOP:
            case BOTTOM:
                ViewGroup.LayoutParams params1 = baseLayout.getLayoutParams();
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params1.height = snackbarHeight;
                if (parent instanceof FrameLayout) {
                    ((FrameLayout.LayoutParams) params1).gravity = from.gravity;
                } else if (parent instanceof CoordinatorLayout) {
                    ((CoordinatorLayout.LayoutParams) params1).gravity = from.gravity;
                }
                baseLayout.setLayoutParams(params1);
                if (myView instanceof LinearLayout) {
                    ((LinearLayout) myView).setOrientation(LinearLayout.HORIZONTAL);
                }
                break;
        }
    }

    /**
     * 根据from 属性设置弹出动画
     */
    private void showLayoutIn() {
        status = USING;
        ViewPropertyAnimatorCompat compat = ViewCompat.animate(myView)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(250);

        switch (from) {
            case LEFT:
                ViewCompat.setTranslationX(myView, -myView.getWidth());
                compat.translationX(0);
                break;
            case TOP:
                ViewCompat.setTranslationY(myView, -myView.getHeight());
                compat.translationY(0);
                break;
            case RIGHT:
                ViewCompat.setTranslationX(myView, myView.getWidth());
                compat.translationX(0);
                break;
            case BOTTOM:
                ViewCompat.setTranslationY(myView, myView.getHeight());
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
                status = SHOWING;
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
        status = USING;
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
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                    }
                });
        switch (from) {
            case LEFT:
                compat.translationX(-myView.getWidth());
                break;
            case TOP:
                compat.translationY(-myView.getWidth());
                break;
            case RIGHT:
                compat.translationX(myView.getWidth());
                break;
            case BOTTOM:
                compat.translationY(myView.getWidth());
                break;
        }
        compat.start();
    }


    public static int dp2px(Context context, float dpValue) {
        if (dpValue <= 0) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static interface OnActionButtonClickListener {
        void onClick();
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
