package xyz.flyinghealthtimer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import org.w3c.dom.Text;

public class FloatingTimerView extends FloatingView {


    private TextView mTimerView;
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
        mTimerView=(TextView)findViewById(R.id.timer_text_view);
        mLayout = (FrameLayout) findViewById(R.id.frame);
        mLayout.setBackgroundResource(R.drawable.ic_timer_prestart);
        this.timerModel = timerModel;

        context.registerReceiver(receiver, new IntentFilter(TimerService.NOTIFICATION));

        rest = timerModel.timeRest;
        run = timerModel.timeRun;
        pause = timerModel.timePause;
        maxRest = timerModel.timeRest;
        maxRun = timerModel.timeRun;
        maxPause = timerModel.timePause;

        count = (TextView) findViewById(R.id.timer_round);


        progressBar = (CircleProgressBar) findViewById(R.id.circleProgressbar_floating);
        progressBar.setProgressFormatter(new TimerFragment.MyProgressFormatter());

        if (!isMyServiceRunning(TimerService.class)) {
            Intent i = new Intent(getContext(), TimerService.class);
            i.putExtra("rest", timerModel.timeRest);
            i.putExtra("run", timerModel.timeRun);
            i.putExtra("pause", timerModel.timePause);
            i.putExtra("count", timerModel.timerCount);
            i.putExtra("id", timerModel.id);
            trikita.log.Log.d("id dd", timerModel.id);
            context.startService(i);
            isRunning = true;

        }

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
//            fabStop.setImageResource(R.drawable.ic_pause_button);
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

    private void touchButton(MotionEvent event){
        if (event.getY() > this.getHeight() / 2) {
            if (mIsStarted) {
                getContext().unregisterReceiver(receiver);
                getContext().stopService(new Intent(getContext(), TimerService.class));
            }
//         else if(hourSecondMinuteToMilli()!=0){
//                startTimer();
//            }
        } else {
            if(mIsStarted||mMillisRemaining!=0){
                resetTimer();
            }else{
                getContext().stopService(new Intent(getContext(), TimerService.class));
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
