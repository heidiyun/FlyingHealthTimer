package xyz.flyinghealthtimer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

public class MainFragment extends BaseFragment {

    private ListView listTimer;
    private TextView btnTabata;
    private TextView btnSimple;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(false);

       // if(rootView != null) return  rootView;

        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);

        btnTabata = (TextView) rootView.findViewById(R.id.tabata_timer);
        btnSimple = (TextView) rootView.findViewById(R.id.simple_timer);
        btnTabata.setVisibility(View.GONE);
        btnSimple.setVisibility(View.GONE);

        btnTabata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentController.newFragment(new AddTimerFragment(), R.layout.fragment_addtimer, true);
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnTabata.getVisibility() == View.GONE){
                    btnTabata.setVisibility(View.VISIBLE);
                    btnSimple.setVisibility(View.VISIBLE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_close));
                }else{
                    btnTabata.setVisibility(View.GONE);
                    btnSimple.setVisibility(View.GONE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                }
            }
        });

        if(TimerApi.getListTimers(mActivity).size() == 0){
            TimerModel timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.tabata_timer);
            timer.timeRest = 10;
            timer.timeRun = 20;
            timer.timePause = 10;
            timer.timerCount = 8;
            TimerApi.addTimer(mActivity, timer);

            timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.tabata_timer);
            timer.timeRest = 10;
            timer.timeRun = 50;
            timer.timePause = 20;
            timer.timerCount = 6;
            TimerApi.addTimer(mActivity, timer);

            timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.simple);
            timer.timeRest = 5;
            timer.timeRun = 6*60;
            timer.timerCount = TimerModel.COUNT_SINGLE_TIMER;
            TimerApi.addTimer(mActivity, timer);
        }

        listTimer = (ListView) rootView.findViewById(R.id.listView_timer);
        final TimerAdapter adapter = new TimerAdapter(mActivity, TimerApi.getListTimers(mActivity));
        listTimer.setAdapter(adapter);

        listTimer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isMyServiceRunning(TimerService.class)) {
                    getActivity().stopService(new Intent(getContext(), TimerService.class));
                }
                if(position == parent.getCount()-1) return;
                TimerModel timer = ((TimerAdapter) parent.getAdapter()).getItem(position);

//                FragmentController.newFragment(new TimerFragment(timer), R.layout.fragment_timer, true);
            }
        });

        return rootView;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
