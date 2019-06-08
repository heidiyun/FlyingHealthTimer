package xyz.flyinghealthtimer.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.fragment.model.Record;
import xyz.flyinghealthtimer.utils.DBHelper;

public class RecordFragment extends BaseFragment {

    private DBHelper helper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private CalendarView calendar;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setTitle(R.string.title_record);

        if (rootView != null) return rootView;

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        helper = new DBHelper(view.getContext());
        SharedPreferences sharedPreferences = view.getContext()
                .getSharedPreferences("xyz.heidiyun", Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(cal.get(Calendar.YEAR));
        sb.append(cal.get(Calendar.MONTH));
        sb.append(cal.get(Calendar.DATE));

        date = sb.toString();
        final RecordAdapter adapter = new RecordAdapter();
        calendar = view.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(year);
                        sb.append(month);
                        sb.append(dayOfMonth);
                        date = sb.toString();
                        Log.i("sss", "date: " + date);
                        adapter.setList(helper.selectColumns(date));
                        Log.i("SSS", "list" + helper.selectColumns(date).size());
                        adapter.notifyDataSetChanged();

                    }
                }
        );
        recyclerView = view.findViewById(R.id.record_recycler_view);


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter.setList(helper.selectColumns(date));


        return view;
    }
}

class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView timerName;
    TextView timerCount;
    TextView timerRound;


    public RecordViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.item_record, parent, false));
        Log.i("SSS", "viewholder");

        timerCount = itemView.findViewById(R.id.record_timer_count);
        timerName = itemView.findViewById(R.id.timer_name);
        timerRound = itemView.findViewById(R.id.round);

    }


}

class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {

    private List<Record> dataSet = new ArrayList<>();

    public void setList(List<Record> list) {
        this.dataSet = list;

    }

    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new RecordViewHolder(viewGroup.getContext(), viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder viewHolder, int position) {

        viewHolder.timerName.setText(dataSet.get(position).getName());
        viewHolder.timerCount.setText("" + dataSet.get(position).getCount());
        viewHolder.timerRound.setText("(" + dataSet.get(position).getRound()+ " rounds)");



    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
