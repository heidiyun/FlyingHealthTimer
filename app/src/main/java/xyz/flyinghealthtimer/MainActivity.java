package xyz.flyinghealthtimer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import trikita.log.Log;
import xyz.flyinghealthtimer.fragment.MainFragment;
import xyz.flyinghealthtimer.fragment.SettingFragment;
import xyz.flyinghealthtimer.fragment.StopWatchFragment;
import xyz.flyinghealthtimer.fragment.TimerFragment;
import xyz.flyinghealthtimer.fragment.adapter.SectionPageAdapter;
import xyz.flyinghealthtimer.service.TimerService;
import xyz.flyinghealthtimer.utils.TimerApi;
import xyz.flyinghealthtimer.utils.TimerModel;
import xyz.flyinghealthtimer.utils.Utils;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    public final static int REQUEST_CODE = 3333;

    private SectionPageAdapter sectionPagAdatper;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermissionAndStartService(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();


        navView.setNavigationItemSelectedListener(this);

        FragmentController.mFragmentManager = getSupportFragmentManager();
        FragmentController.actionbar = getSupportActionBar();

        sectionPagAdatper = new SectionPageAdapter(FragmentController.mFragmentManager);
        FragmentController.adapter = sectionPagAdatper;

        FragmentController.newFragment(new MainFragment(), R.layout.fragment_main, false);
        onNewIntent(getIntent());





//        TabLayout tabLayout = findViewById(R.id.tabLayout);
//        ViewPager viewPager = findViewById(R.id.viewPager);
//        setupViewPager(viewPager);
//
//
//        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {

        Fragment timerListFragment = new MainFragment();

        sectionPagAdatper.addFragment(timerListFragment, "list");

        viewPager.setAdapter(sectionPagAdatper);
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
                FragmentController.backFragment();
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
                FragmentController.backFragment();
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

        } else {
            if (Settings.canDrawOverlays(this)) {
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                FragmentController.newFragment(new MainFragment(), 1, false);
                break;
            case R.id.nav_pullrequest:
                FragmentController.newFragment(new StopWatchFragment(), R.layout.fragment_stopwatch, false);
                break;
            case R.id.nav_issue:
                FragmentController.newFragment(new SettingFragment(), R.layout.fragment_setting, true);
                break;
//            case R.id.nav_record:
//                FragmentController.newFragment(new RecordFragment(), R.layout.fragment_record, true);
//                break;



        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
