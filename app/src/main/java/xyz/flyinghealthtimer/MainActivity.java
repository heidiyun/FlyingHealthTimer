package xyz.flyinghealthtimer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import trikita.log.Log;


public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    public final static int REQUEST_CODE = 3333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermissionAndStartService(true);

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
        if (getIntent() != null && getIntent().getExtras() != null) {
            int id = getIntent().getIntExtra("timer_id", -1);
            boolean stop = getIntent().getBooleanExtra("stop", false);
            Log.d("onNewIntent id", id, "stop", stop);

            if (stop) {
                stopService(new Intent(this, TimerService.class));
                FragmentController.backFragmet();
                return;
            }
            List<TimerModel> timerModelList = TimerApi.getListTimers(this);
            for (TimerModel tm : timerModelList) {
                if (tm.id == id) {
                    if (getSupportFragmentManager().findFragmentByTag(R.layout.fragment_timer + "") == null) {
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

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            getPermissionAndStartService(false);
        }
    }
    private void getPermissionAndStartService(boolean isShowOverlayPermission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            startService();
        } else {
            if (Settings.canDrawOverlays(this)) {
//                startService();
                return;
            }
            if (isShowOverlayPermission) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }


    private void startService() {

//        finishAndRemoveTask();
    }


}
