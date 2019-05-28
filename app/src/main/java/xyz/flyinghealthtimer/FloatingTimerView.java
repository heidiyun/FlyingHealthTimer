package xyz.flyinghealthtimer;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingTimerView extends FloatingView {


    private TextView mTimerView;
    private long mMillisRemaining = 0;
    private FrameLayout mLayout;
    private boolean mIsStarted = false;

    public FloatingTimerView(Context context) {
        super(context);
        inflate(context, R.layout.countdown_timer_view, this);
        mTimerView=(TextView)findViewById(R.id.timer_text_view);
        mLayout = (FrameLayout) findViewById(R.id.frame);
        mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - event.getDownTime() < 150) {
                    touchButton(event);
                }
                break;
        }
        return false;
    }

    private void touchButton(MotionEvent event){
        if (event.getY() > this.getHeight() / 2) {
            if (mIsStarted) {
                stopTimer();
//            } else if(hourSecondMinuteToMilli()!=0){
                startTimer();
            }
        } else {
            if(mIsStarted||mMillisRemaining!=0){
                resetTimer();
            }else{
                getContext().stopService(new Intent(getContext(),FloatingService.class));
            }
        }
    }

    private void startTimer() {
        mIsStarted = true;
//        if(mMillisRemaining==0){
//            mCountDownTimer = new MyCountDownTimer(hourSecondMinuteToMilli(), mTimerView);
//        }else{
//            mCountDownTimer = new MyCountDownTimer(mMillisRemaining, mTimerView);
//        }
//        mPickers.setVisibility(View.INVISIBLE);
//        mCountDownTimer.start();
        mTimerView.setVisibility(VISIBLE);
        mLayout.setBackgroundResource(R.drawable.ic_timer_started);
    }

    private void stopTimer() {
        mIsStarted = false;
//        mMillisRemaining=mCountDownTimer.getMillisRemaining();
//        mCountDownTimer.cancel();
        mLayout.setBackgroundResource(R.drawable.ic_timer_stopped);
    }

    private void resetTimer() {
        mIsStarted = false;
        mMillisRemaining = 0;
//        if(mCountDownTimer!=null)mCountDownTimer.cancel();
//        mPickers.setVisibility(View.VISIBLE);
        mTimerView.setVisibility(INVISIBLE);
//        mHourPicker.reset();
//        mSecondPicker.reset();
//        mMinutePicker.reset();
        mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
    }
}
