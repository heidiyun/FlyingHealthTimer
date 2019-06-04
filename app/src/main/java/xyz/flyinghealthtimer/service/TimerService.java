package xyz.flyinghealthtimer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import trikita.log.Log;
import xyz.flyinghealthtimer.MainActivity;
import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.fragment.TimerFragment;
import xyz.flyinghealthtimer.utils.TimerModel;


public class TimerService extends Service {
    private static final String LOG_TAG = "TimerService";
    public static final String NOTIFICATION = "ru.littlebrains.timer.receiver";

    private static final int REST = 0;
    private static final int RUN = 1;
    private static final int PAUSE = 2;
    private static final int FINISH = 3;

    private TextToSpeech tts;
    public static TimerModel timerModel = new TimerModel();
    private int rest;
    private int run;
    private int pause;
    private int nowRepeat = 0;
    private int nowStatus = 0;
    private MediaPlayer player;
    private Vibrator vib;
    private AssetFileDescriptor afd;
    private boolean pauseTimer = false;
    private Timer timer;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;
    private Boolean isNotification;
    private Boolean isSound;
    private Boolean isTTS;
    private Boolean isVibrator;

    private final IBinder mBinder = new LocalBinder();

    class LocalBinder extends Binder {

        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("xyz.heidiyun", MODE_PRIVATE);
        isNotification = sp.getBoolean("notification", false);
        isSound = sp.getBoolean("sound", false);
        isTTS = sp.getBoolean("tts", false);
        isVibrator = sp.getBoolean("vibrator", false);

        Log.d(LOG_TAG, "onStartCommand");
        int status = intent.getIntExtra("status", 0);
        if (status == 0) {
        timerModel.timeRest = intent.getIntExtra("rest", 0);
            timerModel.timeRun = intent.getIntExtra("run", 0);
            timerModel.timePause = intent.getIntExtra("pause", 0);
            timerModel.timerCount = intent.getIntExtra("count", 0);
            timerModel.id = intent.getIntExtra("id", -1);
            Log.d("id", timerModel.id);
            rest = timerModel.timeRest;
            run = timerModel.timeRun;
            pause = timerModel.timePause;
        } else {
            timerModel.timeRest = intent.getIntExtra("rest", 0);
            timerModel.timeRun = intent.getIntExtra("run", 0);
            timerModel.timePause = intent.getIntExtra("pause", 0);
            timerModel.timerCount = intent.getIntExtra("count", 0);
            timerModel.id = intent.getIntExtra("id", -1);
            rest = intent.getIntExtra("nowRest", 0);
            run = intent.getIntExtra("nowRun", 0);
            pause = intent.getIntExtra("nowPause", 0);
            nowRepeat = intent.getIntExtra("nowCount", 0);
            nowStatus = intent.getIntExtra("nowStatus", 0);
            Log.d("id", timerModel.id);


        }


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        tts.setPitch(0.7f);
        tts.setSpeechRate(1.2f);

        someTask();
//        if (isNotification)
//            notification();


        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        //if(handlerTimer != null) handlerTimer.removeCallbacksAndMessages(null);
        if (runnableTimer != null) runnableTimer.cancel();
        if (timer != null) timer.cancel();
        if (notificationManager != null) notificationManager.cancelAll();

    }

    void someTask() {

        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

       /* handlerTimer = new Handler();
        handlerTimer.postDelayed(runnableTimer, 1000);*/

        //if (runnableTimer != null) runnableTimer.cancel();
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(runnableTimer, 1000, 1000);
    }

    TimerTask runnableTimer = new TimerTask() {
        @Override
        public void run() {
            Log.d("runnableTimer", "runnableTimer");
            if (pauseTimer) return;
            if (nowStatus == REST) {
                if (rest > 0) rest--;
                if (rest < 4 && rest > 0) {
                    if (isSound)
                        playBeep(false);
                    if (isTTS)
                        speakSec(rest);
                }
                if (rest == 0) {
                    nowStatus = RUN;
                    if (isSound)
                        playBeep(true);
                    nowRepeat++;
                }
            } else if (nowStatus == RUN) {
                if (run > 0) run--;
                if (run < 4 && run > 0) {
                    if (isSound)
                        playBeep(false);
                    if (isTTS)
                        speakSec(run);
                }
                if (run == 0) {
                    if (timerModel.timerCount == TimerModel.COUNT_SINGLE_TIMER) {
                        nowStatus = FINISH;
                    } else {
                        nowStatus = PAUSE;
                        run = timerModel.timeRun;
                        if (nowRepeat == timerModel.timerCount) {
                            nowStatus = FINISH;
                        } else {
                            nowRepeat++;
                        }
                    }
                    if (isSound)
                        playBeep(true);

                }
            } else if (nowStatus == PAUSE) {
                if (pause > 0) pause--;
                if (pause < 4 && pause > 0) {
                    if (isSound)
                        playBeep(false);
                    if (isTTS)
                        speakSec(pause);
                }
                if (pause == 0) {
                    nowStatus = RUN;
                    if (timerModel.timePause != 0) {
                        if (isSound)
                            playBeep(true);
                    }
                    pause = timerModel.timePause;
                }
            }
            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra("status", nowStatus);
            intent.putExtra("rest", rest);
            intent.putExtra("run", run);
            intent.putExtra("pause", pause);
            intent.putExtra("nowRepeat", nowRepeat);
            sendBroadcast(intent);
            if (TimerFragment.isRunning && !TimerFragment.isForeground) {
                Log.i("SSS", "here");
                if (isNotification)
                    notification();
            }
            if (nowStatus == FINISH) {
                wakeLock.release();
                if (timer != null) timer.cancel();
                stopSelf();
            }
        }
    };

    private void speakSec(int sec) {

        String secTo = Integer.toString(sec);
        tts.speak(secTo, TextToSpeech.QUEUE_FLUSH, null, null);


    }

    private void playBeep(boolean isLong) {
        try {
            if (player != null && vib != null) {
                player.stop();
                player.reset();
                player.release();
                vib.cancel();
            }
            if (isLong) {
                if (nowStatus == PAUSE) {
                    afd = getAssets().openFd("rest.wav");
                }
                if (nowStatus == FINISH) {
                    afd = getAssets().openFd("finish.wav");
                }
                if (nowStatus == RUN) {
                    afd = getAssets().openFd("start.mp3");
                }
            } else {
                afd = getAssets().openFd("beep-07.mp3");
            }
            vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (isVibrator)
                vib.vibrate(500);
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("open");
        intent.putExtra("timer_id", timerModel.id);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channer description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0L});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);


            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_view);


            Notification notification = new Notification.Builder(this, "channel_id")
                    .setContentTitle(getTextStatus() + " " + getTime() + getString(R.string.sec))
                    .setContentText(timerModel.timerCount == TimerModel.COUNT_SINGLE_TIMER ?
                            "" :
                            nowRepeat + "/" + timerModel.timerCount)
                    .setSmallIcon(R.drawable.ic_stat_image_timer)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();


//            Intent i = new Intent(this, MainActivity.class);
//            i.setAction("stop");
//            i.putExtra("stop", true);
//            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
//            notification.addAction(R.drawable.ic_action_av_stop, getString(R.string.stop), pi);

            notificationManager.notify(1, notification);

//            startForeground(456772, notification.build());
        }
    }

    private String getTime() {
        switch (nowStatus) {
            case REST:
                return rest + "";
            case PAUSE:
                return pause + "";
            case RUN:
                return run + "";
            default:
                return "";
        }
    }

    private String getTextStatus() {
        switch (nowStatus) {
            case REST:
                return getResources().getString(R.string.rest);
            case PAUSE:
                return getResources().getString(R.string.pausa);
            case RUN:
                return getResources().getString(R.string.run);
            case FINISH:
                return getResources().getString(R.string.finish);
            default:
                return "";
        }
    }


}
