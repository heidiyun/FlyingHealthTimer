package xyz.flyinghealthtimer.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class TimerModel implements Parcelable {

    public static final int COUNT_SINGLE_TIMER = -10;

    public int id;
    public String name = "";
    public int timeRest;
    public int timeRun;
    public int timePause;
    public int timerCount;

    public TimerModel() {

    }

    protected TimerModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        timeRest = in.readInt();
        timeRun = in.readInt();
        timePause = in.readInt();
        timerCount = in.readInt();
    }

    public static final Creator<TimerModel> CREATOR = new Creator<TimerModel>() {
        @Override
        public TimerModel createFromParcel(Parcel in) {
            return new TimerModel(in);
        }

        @Override
        public TimerModel[] newArray(int size) {
            return new TimerModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(timeRest);
        dest.writeInt(timeRun);
        dest.writeInt(timePause);
        dest.writeInt(timerCount);
    }
}
