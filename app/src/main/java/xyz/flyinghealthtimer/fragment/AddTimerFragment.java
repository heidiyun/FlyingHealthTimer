package xyz.flyinghealthtimer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.Date;

import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.fragment.model.TimerModel;
import xyz.flyinghealthtimer.utils.FragmentController;
import xyz.flyinghealthtimer.utils.TimerApi;
import xyz.flyinghealthtimer.utils.Utils;


public class AddTimerFragment extends BaseFragment {

    private EditText timeRun;
    private EditText timePause;
    private EditText timerCount;
    private EditText nameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setTitle(R.string.title_create_timer);

        if(rootView != null) return  rootView;

        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_addtimer, container, false);

        nameText = (EditText) rootView.findViewById(R.id.setName);
        timeRun = (EditText) rootView.findViewById(R.id.setExercise);
        timePause = (EditText) rootView.findViewById(R.id.setRest);
        timerCount = (EditText) rootView.findViewById(R.id.setCount);
        //btnSave = (Button) rootView.findViewById(R.id.button_save);

        /*TimeDialog timeRunDialog = new TimeDialog(getActivity(), timeRun);
        timeRun.setOnClickListener(timeRunDialog);
        timeRun.setOnFocusChangeListener(timeRunDialog);

        TimeDialog timePauseDialog = new TimeDialog(getActivity(), timePause);
        timePause.setOnClickListener(timePauseDialog);
        timePause.setOnFocusChangeListener(timePauseDialog);*/
        /*
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeRun.getText().length() == 0 || timerCount.getText().length() == 0 ||
                        timePause.getText().length() == 0 || nameText.getText().length() == 0) {
                    showToast(R.string.toast_empty_field);
                    return;
                }
                try {

                    TimerModel timer = new TimerModel();
                    timer.id = (int) new Date().getTime();
                    timer.name = nameText.getText().toString();
                    timer.timeRest = 10;
                    timer.timeRun = (Integer.valueOf(timeRun.getText().toString()));
                    timer.timePause = (Integer.valueOf(timePause.getText().toString()));
                    timer.timerCount = (Integer.valueOf(timerCount.getText().toString()));
                    btnSave.setEnabled(false);
                    TimerApi.addTimer(mActivity, timer);
                    showToast(R.string.toast_timer_created);
                    Utils.hideKeyboard(mActivity);
                    FragmentController.backFragmet();
                }catch (NumberFormatException e) {
                    timeRun.setText("");
                    timePause.setText("");
                    timerCount.setText("");
                }
            }
        });
        */

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (timeRun.getText().length() == 0 || timerCount.getText().length() == 0 ||
                        timePause.getText().length() == 0 || nameText.getText().length() == 0) {
                    showToast(R.string.toast_empty_field);
                    break;
                }
                try {

                    TimerModel timer = new TimerModel();
                    timer.id = (int) new Date().getTime();
                    timer.name = nameText.getText().toString();
                    timer.timeRest = 4;
                    timer.timeRun = (Integer.valueOf(timeRun.getText().toString()));
                    timer.timePause = (Integer.valueOf(timePause.getText().toString()));
                    timer.timerCount = (Integer.valueOf(timerCount.getText().toString()));
                    TimerApi.addTimer(mActivity, timer);
                    showToast(R.string.toast_timer_created);
                    Utils.hideKeyboard(mActivity);
                    FragmentController.backFragment();
                }catch (NumberFormatException e) {
                    timeRun.setText("");
                    timePause.setText("");
                    timerCount.setText("");
                }
        }
        return true;
    }
}
