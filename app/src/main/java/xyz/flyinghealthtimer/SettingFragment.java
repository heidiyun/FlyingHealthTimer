package xyz.flyinghealthtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingFragment extends BaseFragment {

    private Switch soundSwitch;
    private Switch vibratorSwitch;
    private Switch notificationSwitch;
    private Switch ttsSwitch;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setTitle(R.string.title_setting);

        if (rootView != null) return rootView;

        final View view = inflater.inflate(R.layout.fragment_setting, container, false);


        soundSwitch = view.findViewById(R.id.sound_switch);
        vibratorSwitch = view.findViewById(R.id.vibrator_switch);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        ttsSwitch = view.findViewById(R.id.tts_switch);

        soundSwitch.setChecked(true);
        vibratorSwitch.setChecked(false);
        notificationSwitch.setChecked(true);

        soundSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences sharedPreferences = view.getContext()
                                .getSharedPreferences("xyz.heidiyun", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("sound", isChecked);
                        editor.apply();

                    }
                }
        );

        vibratorSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences sharedPreferences = view.getContext()
                                .getSharedPreferences("xyz.heidiyun", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("vibrator", isChecked);
                        editor.apply();

                    }
                }
        );

        notificationSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences sharedPreferences = view.getContext()
                                .getSharedPreferences("xyz.heidiyun", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("notification", isChecked);
                        editor.apply();

                    }
                }
        );

        ttsSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences sharedPreferences = view.getContext()
                                .getSharedPreferences("xyz.heidiyun", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("tts", isChecked);
                        editor.apply();

                    }
                }
        );


        return view;
    }
}
