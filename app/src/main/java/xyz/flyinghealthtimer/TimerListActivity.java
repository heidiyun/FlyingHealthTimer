package xyz.flyinghealthtimer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;

import xyz.flyinghealthtimer.fragment.TimerFragment;
import xyz.flyinghealthtimer.fragment.adapter.TimerAdapter;
import xyz.flyinghealthtimer.service.FloatingService;
import xyz.flyinghealthtimer.service.TimerService;
import xyz.flyinghealthtimer.utils.TimerApi;
import xyz.flyinghealthtimer.utils.TimerModel;

public class TimerListActivity extends AppCompatActivity {

    private ListView listTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);


        if(TimerApi.getListTimers(this).size() == 0){
            TimerModel timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.flying_health_timer);
            timer.timeRest = 10;
            timer.timeRun = 20;
            timer.timePause = 10;
            timer.timerCount = 8;
            TimerApi.addTimer(this, timer);

            timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.flying_health_timer);
            timer.timeRest = 10;
            timer.timeRun = 50;
            timer.timePause = 20;
            timer.timerCount = 6;
            TimerApi.addTimer(this, timer);
        }

        listTimer = (ListView) findViewById(R.id.listView_timer);
        final TimerAdapter adapter = new TimerAdapter(this, TimerApi.getListTimers(this), true);
        listTimer.setAdapter(adapter);

        listTimer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isMyServiceRunning(TimerService.class)) {
                    TimerListActivity.this.stopService(new Intent(TimerListActivity.this, TimerService.class));
                }
                if(position == parent.getCount()) return;
                TimerModel timer = ((TimerAdapter) parent.getAdapter()).getItem(position);

                Intent intent = new Intent(TimerListActivity.this, FloatingService.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("timer", (Parcelable) timer);
                intent.putExtra("timer", bundle);
                TimerListActivity.this.startService(intent);

                finish();
//

            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
