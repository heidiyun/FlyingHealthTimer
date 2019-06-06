package xyz.flyinghealthtimer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import xyz.flyinghealthtimer.fragment.TimerFragment;
import xyz.flyinghealthtimer.service.FloatingService;
import xyz.flyinghealthtimer.service.TimerService;
import xyz.flyinghealthtimer.utils.TimerModel;

public class FloatingTimerView extends FloatingView {


    static public TextView mTimerView;
    public TextView timerStatus;
    private long mMillisRemaining = 0;
    private LinearLayout mLayout;
    private boolean mIsStarted = false;
    static Boolean isRunning = false;


    private static final int REST = 0;
    private static final int RUN = 1;
    private static final int PAUSE = 2;
    private static final int FINISH = 3;

    private TextView count;
    private TextView statusView;
    private CircleProgressBar progressBar;

    private int rest;
    private int run;
    private int pause;
    private int maxRest;
    private int maxRun;
    private int maxPause;
    private int nowRepeat = 0;
    private int nowStatus = 0;
    private TimerModel timerModel;

    public FloatingTimerView(Context context, TimerModel timerModel) {
        super(context);
        inflate(context, R.layout.countdown_timer_view, this);
        Log.i(FloatingTimerView.class.getName(), "floatingTimerView");
        statusView = findViewById(R.id.timer_status);
        mLayout = (LinearLayout) findViewById(R.id.frame);
        mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
        this.timerModel = timerModel;

        startTimer();


    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                trikita.log.Log.d(bundle);
                trikita.log.Log.d(intent);
                nowStatus = bundle.getInt("status", 0);
                rest = bundle.getInt("rest", 0);
                run = bundle.getInt("run", 0);
                pause = bundle.getInt("pause", 0);
                nowRepeat = bundle.getInt("nowRepeat", 0);
                updateScreen();
            }
        }
    };

    private void updateScreen() {

        if (timerModel == null) return;
        if (isRunning) {

        } else {
//            fabStop.setImageResource(R.drawable.ic_play_button);
        }

        if (timerModel.timerCount != TimerModel.COUNT_SINGLE_TIMER) {
            count.setText(nowRepeat + " / " + timerModel.timerCount);
        } else {
            count.setVisibility(View.GONE);
        }
//        if (!isAdded()) return;
//        Resources res = getActivity().getResources();
        switch (nowStatus) {
            case REST:
                statusView.setText(R.string.rest);
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                progressBar.setMax(maxRest);
                progressBar.setProgress(rest - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                progressBar.setProgressTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
//                toolbar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.yellow)));
//                setStatusBarColor(R.color.yellow);
                break;
            case RUN:
                progressBar.setMax(maxRun);
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                statusView.setText(R.string.run);
                progressBar.setProgress(run - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                progressBar.setProgressTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
//                toolbar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.yellow)));
//                setStatusBarColor(R.color.yellow);
                break;
            case PAUSE:
                progressBar.setMax(maxPause);
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                statusView.setText(R.string.pausa);
                progressBar.setProgress(pause - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                progressBar.setProgressTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
//                toolbar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.yellow)));
//                setStatusBarColor(R.color.yellow);
                break;
            case FINISH:
                getContext().stopService(new Intent(getContext(), TimerService.class));
                statusView.setText(R.string.finish);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                break;
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                trikita.log.Log.d("service true");
                return true;
            }
        }
        trikita.log.Log.d("service false");
        return false;
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

    private void touchButton(MotionEvent event) {
        if (event.getY() > this.getHeight() / 2) {
            if (mIsStarted) {
                mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
//                getContext().unregisterReceiver(receiver);
                getContext().stopService(new Intent(getContext(), TimerService.class));
                mIsStarted = false;
            } else {
                reStartTimer();
                mIsStarted = true;
            }

        } else {
            if (mIsStarted) {
                resetTimer();
            } else {
                getContext().stopService(new Intent(getContext(), TimerService.class));
                getContext().stopService(new Intent(getContext(), FloatingService.class));
                getContext().unregisterReceiver(receiver);
            }
        }
    }

    private void reStartTimer() {
        if (!isMyServiceRunning(TimerService.class)) {

            Intent i = new Intent(getContext(), TimerService.class);
            i.putExtra("status", 1);
            i.putExtra("rest", maxRest);
            i.putExtra("run", maxRun);
            i.putExtra("pause", maxPause);
            i.putExtra("count", timerModel.timerCount);
            i.putExtra("id", timerModel.id);
            i.putExtra("nowRest", rest);
            i.putExtra("nowRun", run);
            i.putExtra("nowPause", pause);
            i.putExtra("nowCount", nowRepeat);
            i.putExtra("nowStatus", nowStatus);

            trikita.log.Log.d("id dd", timerModel.id);
            getContext().startService(i);


//                        Intent intent = new Intent(rootView.getContext(), timerService.getClass());

//                        rootView.getContext().startService(intent);
        }

        isRunning = true;

        mLayout.setBackgroundResource(R.drawable.ic_timer_started);
    }

    private void startTimer() {
        mIsStarted = true;

        rest = timerModel.timeRest;
        run = timerModel.timeRun;
        pause = timerModel.timePause;
        maxRest = timerModel.timeRest;
        maxRun = timerModel.timeRun;
        maxPause = timerModel.timePause;

        count = (TextView) findViewById(R.id.timer_round);


        progressBar = (CircleProgressBar) findViewById(R.id.circleProgressbar_floating);
        progressBar.setProgressFormatter(new TimerFragment.MyProgressFormatter());

        getContext().registerReceiver(receiver, new IntentFilter(TimerService.NOTIFICATION));
        if (!isMyServiceRunning(TimerService.class)) {
            Intent i = new Intent(getContext(), TimerService.class);
            i.putExtra("rest", timerModel.timeRest);
            i.putExtra("run", timerModel.timeRun);
            i.putExtra("pause", timerModel.timePause);
            i.putExtra("count", timerModel.timerCount);
            i.putExtra("id", timerModel.id);
            trikita.log.Log.d("id dd", timerModel.id);
            getContext().startService(i);
            isRunning = true;
            mIsStarted = true;
//            mLayout.setBackgroundResource(R.drawable.ic_timer_started);

        }


        // 여기서 원래는 타이머가 시작되는데 나는 이미 타이머가 시작된 상태이다.\
        // 그럼 TimerService에 내 레이아웃을 넘겨주저야 하는데, ./.... 그렇다면 생각을 좀 해보자.
        // service에서 View로 정보를 갖고오는 방법을 생각해보자.
        // Service와 어떻게 통신하는지 알아보자.
//        if(mMillisRemaining==0){
//            mCountDownTimer = new MyCountDownTimer(hourSecondMinuteToMilli(), mTimerView);
//        }else{
//            mCountDownTimer = new MyCountDownTimer(mMillisRemaining, mTimerView);
//        }
//        mPickers.setVisibility(View.INVISIBLE);
//        mCountDownTimer.start();
//        mTimerView.setVisibility(VISIBLE);
        mLayout.setBackgroundResource(R.drawable.ic_timer_started);
    }

    private void stopTimer() {
        mIsStarted = false;

        mLayout.setBackgroundResource(R.drawable.ic_timer_stopped);


    }

    private void resetTimer() {
        mIsStarted = false;


        getContext().stopService(new Intent(getContext(), TimerService.class));
        nowStatus = REST;
        rest = maxRest;
        run = maxRun;
        pause = maxPause;
        nowRepeat = 0;
        isRunning = false;
        mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
        updateScreen();

//        mMillisRemaining = 0;
//        if(mCountDownTimer!=null)mCountDownTimer.cancel();
//        mPickers.setVisibility(View.VISIBLE);
//        mTimerView.setVisibility(INVISIBLE);
//        mHourPicker.reset();
//        mSecondPicker.reset();
//        mMinutePicker.reset();
    }
}
