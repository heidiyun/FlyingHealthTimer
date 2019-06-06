package xyz.flyinghealthtimer.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import trikita.log.Log;
import xyz.flyinghealthtimer.FragmentController;
import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.service.FloatingService;
import xyz.flyinghealthtimer.service.TimerService;
import xyz.flyinghealthtimer.utils.TimerApi;
import xyz.flyinghealthtimer.utils.TimerModel;

@SuppressLint("ValidFragment")
public class TimerFragment extends BaseFragment {


    private static final int REST = 0;
    private static final int RUN = 1;
    private static final int PAUSE = 2;
    private static final int FINISH = 3;

    public static boolean isForeground = true;
    private FloatingService floatingService = new FloatingService();

    private TimerService timerService;
    public static Boolean isRunning = false;
    private TimerModel timerModel;
    private TextView statusView;
    private TextView time;
    private TextView count;
    private CircleProgressBar circleProgressBar;
    private CircleProgressBar progressBar;
    private int rest;
    private int run;
    private int pause;
    private int maxRest;
    private int maxRun;
    private int maxPause;
    private int nowRepeat = 0;
    private int nowStatus = 0;
    private MediaPlayer player;
    private AssetFileDescriptor afd;
    private boolean pauseTimer = false;
    private Handler handlerTimer;
    private Intent i;
    private ImageButton fabStop;
    private ImageButton fabStart;
    private boolean isService = false;


    public TimerFragment() {
    }

    public TimerFragment(TimerModel timer) {
        this.timerModel = timer;

    }

    public static final class MyProgressFormatter implements CircleProgressBar.ProgressFormatter {
        private static final String DEFAULT_PATTERN = "%d";

        @Override
        public CharSequence format(int progress, int max) {
            return String.format(DEFAULT_PATTERN, progress + 1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isForeground = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        isForeground = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (timerModel == null) {
            FragmentController.newFragment(new MainFragment(), R.layout.fragment_main, false);
            return null;
        }
        setTitle("");


        actionBar.setElevation(0);
        if (rootView != null) return rootView;

        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_timer, container, false);

        statusView = (TextView) rootView.findViewById(R.id.status);
        progressBar = (CircleProgressBar) rootView.findViewById(R.id.circleProgressbar);
        progressBar.setProgressFormatter(new MyProgressFormatter());
//        time = (TextView) rootView.findViewById(R.id.time);
        count = (TextView) rootView.findViewById(R.id.count);
        fabStop = (ImageButton) rootView.findViewById(R.id.fab);
        fabStart = (ImageButton) rootView.findViewById(R.id.fab1);
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    mActivity.stopService(new Intent(getContext(), TimerService.class));
                    isRunning = false;
                } else {
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

                        Log.d("id dd", timerModel.id);
                        getContext().startService(i);


//                        Intent intent = new Intent(rootView.getContext(), timerService.getClass());

//                        rootView.getContext().startService(intent);
                    }
                    isRunning = true;
                }
                updateScreen();


//                FragmentController.backFragmet();
            }
        });

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.stopService(new Intent(getContext(), TimerService.class));
                nowStatus = REST;
                rest = maxRest;
                run = maxRun;
                pause = maxPause;
                nowRepeat = 0;
                isRunning = false;
                updateScreen();
            }
        });


        rest = timerModel.timeRest;
        run = timerModel.timeRun;
        pause = timerModel.timePause;
        maxRest = timerModel.timeRest;
        maxRun = timerModel.timeRun;
        maxPause = timerModel.timePause;

        if (!isMyServiceRunning(TimerService.class)) {
            Intent i = new Intent(getContext(), TimerService.class);
            i.putExtra("rest", timerModel.timeRest);
            i.putExtra("run", timerModel.timeRun);
            i.putExtra("pause", timerModel.timePause);
            i.putExtra("count", timerModel.timerCount);
            i.putExtra("id", timerModel.id);

            rootView.getContext().startService(i);
//            rootView.getContext().bindService(i, connection, Context.BIND_AUTO_CREATE);

//            Intent intent = new Intent(rootView.getContext(), floatingService.getClass());
//              service 객체에서 받아온 정보를 intent에 넣어주면 되나?

//            rootView.getContext().startService(intent);

        }

        updateScreen();
        isRunning = true;

        return rootView;
    }

//    ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
//            timerService = binder.getService();
//            isService = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isService = false;
//
//        }
//    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("service true");
                return true;
            }
        }
        Log.d("service false");
        return false;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.d(bundle);
                Log.d(intent);
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
            fabStop.setImageResource(R.drawable.ic_pause_button);
        } else {
            fabStop.setImageResource(R.drawable.ic_play_button);
        }

        if (timerModel.timerCount != TimerModel.COUNT_SINGLE_TIMER) {
            count.setText(nowRepeat + " / " + timerModel.timerCount);
        } else {
            count.setVisibility(View.GONE);
        }
        if (!isAdded()) return;
        Resources res = getActivity().getResources();
        switch (nowStatus) {
            case REST:
                statusView.setText(R.string.rest);
//                statusView.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.yellow));
                progressBar.setMax(maxRest);
                progressBar.setProgress(rest - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.colorAccent));
                progressBar.setProgressTextColor(ContextCompat.getColor(rootView.getContext(), R.color.colorAccent));


                break;
            case RUN:
                progressBar.setMax(maxRun);
                statusView.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.green));
                statusView.setText(R.string.run);
                progressBar.setProgress(run - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.green));
                progressBar.setProgressTextColor(ContextCompat.getColor(rootView.getContext(), R.color.green));


                break;
            case PAUSE:
                progressBar.setMax(maxPause);
                statusView.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.red));
                statusView.setText(R.string.pausa);
                progressBar.setProgress(pause - 1);
                progressBar.setProgressBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.red));
                progressBar.setProgressTextColor(ContextCompat.getColor(rootView.getContext(), R.color.red));


                break;
            case FINISH:
                mActivity.stopService(new Intent(getContext(), TimerService.class));
                statusView.setText(R.string.finish);
//                progressBar.setProgressBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.yellow));
                progressBar.setVisibility(View.INVISIBLE);
                fabStop.setImageResource(R.drawable.ic_play_button);
                count.setVisibility(View.INVISIBLE);
                break;
        }

    }


    private void setStatusBarColor(int idColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(idColor));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.registerReceiver(receiver, new IntentFilter(TimerService.NOTIFICATION));
        pauseTimer = false;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        updateScreen();
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(receiver);
        pauseTimer = true;
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        setStatusBarColor(R.color.colorPrimaryDark);
    }

    @Override
    public void onDestroy() {
        setStatusBarColor(R.color.colorPrimaryDark);
        if (toolbar != null) {
            toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        }

        if (isService) {
            isService = false;
        }

        rootView.getContext().stopService(new Intent(getContext(), TimerService.class));
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                pauseTimer = true;
                FragmentController.newFragment(new EditTimerFragment(timerModel), R.layout.fragment_edittimer, true);
                if (isService) {
                    isService = false;
                }

                rootView.getContext().stopService(new Intent(getContext(), TimerService.class));
                break;
            case R.id.action_delete:
                pauseTimer = true;
                FragmentController.backFragment();

                //FragmentController.backFragment();
                TimerApi.deleteTimer(mActivity, timerModel);
                if (isService) {
                    isService = false;
                }

                rootView.getContext().stopService(new Intent(getContext(), TimerService.class));
                break;
        }
        return true;
    }
}
