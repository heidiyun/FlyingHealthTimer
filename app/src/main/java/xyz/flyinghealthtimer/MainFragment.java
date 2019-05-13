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
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(false);

       // if(rootView != null) return  rootView;

        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);


        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener((view)->{
            FragmentController.newFragment(new AddTimerFragment(), R.layout.fragment_edittimer, true);
        });


        if(TimerApi.getListTimers(mActivity).size() == 0){
            TimerModel timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.flying_health_timer);
            timer.timeRest = 10;
            timer.timeRun = 20;
            timer.timePause = 10;
            timer.timerCount = 8;
            TimerApi.addTimer(mActivity, timer);

            timer = new TimerModel();
            timer.id = (int) new Date().getTime();
            timer.name = getResources().getString(R.string.flying_health_timer);
            timer.timeRest = 10;
            timer.timeRun = 50;
            timer.timePause = 20;
            timer.timerCount = 6;
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
                if(position == parent.getCount()) return;
                TimerModel timer = ((TimerAdapter) parent.getAdapter()).getItem(position);

                FragmentController.newFragment(new TimerFragment(timer), R.layout.fragment_timer, true);
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
