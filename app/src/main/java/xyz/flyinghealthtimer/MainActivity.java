package xyz.flyinghealthtimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import trikita.log.Log;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentController.mFragmentManager = getSupportFragmentManager();
        FragmentController.actionbar = getSupportActionBar();

        FragmentController.newFragment(new MainFragment(), R.layout.fragment_main, false);
        onNewIntent(getIntent());


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
        if(getIntent() != null && getIntent().getExtras() != null){
            int id = getIntent().getIntExtra("timer_id", -1);
            boolean stop = getIntent().getBooleanExtra("stop", false);
            Log.d("onNewIntent id", id, "stop", stop);

            if(stop){
                stopService(new Intent(this, TimerService.class));
                FragmentController.backFragmet();
                return;
            }
            List<TimerModel> timerModelList = TimerApi.getListTimers(this);
            for( TimerModel tm : timerModelList){
                if(tm.id == id){
                    if(getSupportFragmentManager().findFragmentByTag(R.layout.fragment_timer+"") == null) {
                        FragmentController.newFragment(new TimerFragment(tm), R.layout.fragment_timer, true);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utils.hideKeyboard(this);
                FragmentController.backFragmet();
                break;
        }

        return super.onOptionsItemSelected(item);
    }






}
