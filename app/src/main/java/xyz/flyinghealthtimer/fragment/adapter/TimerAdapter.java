package xyz.flyinghealthtimer.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.flyinghealthtimer.FragmentController;
import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.fragment.TimerFragment;
import xyz.flyinghealthtimer.utils.TimerModel;
import xyz.flyinghealthtimer.service.FloatingService;

public class TimerAdapter extends BaseAdapter{
	
	private List<TimerModel> list;
	private Context mContext;
	private Boolean isActivity;

	public TimerAdapter(Context mContext, List<TimerModel> list, Boolean isActivity){
		this.list = list;
		this.mContext = mContext;
		this.isActivity = isActivity;
	}
	
	public void setList(List<TimerModel> list){
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size() ;
	}

	@Override
	public TimerModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View container = convertView;
		if(container == null){
			container = LayoutInflater.from(mContext).inflate(R.layout.item_timer_list, null);
		}

		final View root = container.findViewById(R.id.root);
		if(position == getCount()){
			root.setVisibility(View.GONE);
			ViewGroup.LayoutParams lp = container.getLayoutParams();

			return container;
		}
		root.setVisibility(View.VISIBLE);
		TimerModel item = list.get(position);

		TextView textRun = (TextView) container.findViewById(R.id.time_run);
		TextView title = (TextView) container.findViewById(R.id.title);
//		ImageButton playButton = (ImageButton) container.findViewById(R.id.play_button);
		textRun.setText(item.timeRun + " " + getNumEnding(item.timeRun));
		if(item.timerCount != TimerModel.COUNT_SINGLE_TIMER) {
			title.setText(item.name);
			TextView textPause = (TextView) container.findViewById(R.id.time_pause);
			textPause.setText(item.timePause + " " + getNumEnding(item.timePause));
			TextView textCount = (TextView) container.findViewById(R.id.timer_count);
			textCount.setText("" + item.timerCount);
		}	else{
			title.setText(item.name);
			container.findViewById(R.id.timer_count).setVisibility(View.GONE);
			container.findViewById(R.id.text_timer_count).setVisibility(View.GONE);
			container.findViewById(R.id.text_time_pause).setVisibility(View.GONE);
			container.findViewById(R.id.time_pause).setVisibility(View.GONE);
		}

		Button floatingTimerButton  = (Button) root.findViewById(R.id.floatingTimerButton);
		Button intervalTimerButton  = (Button) root.findViewById(R.id.intervalTimerButton);

		if (isActivity) {
			floatingTimerButton.setVisibility(View.GONE);
			intervalTimerButton.setVisibility(View.GONE);
		}
		final TimerModel timer= getItem(position);

		floatingTimerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(root.getContext(), FloatingService.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("timer", (Parcelable) timer);
				intent.putExtra("timer", bundle);
				root.getContext().startService(intent);
				Toast.makeText(mContext, "floating", Toast.LENGTH_SHORT).show();
			}
		});

		intervalTimerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentController.newFragment(new TimerFragment(timer), R.layout.fragment_timer, true);
				Toast.makeText(mContext, "Interval", Toast.LENGTH_SHORT).show();
			}
		});



		return container;
	}


	private String getNumEnding(int t)
	{
		t = t % 100;
		if (t>=11 && t<=19) {
			return mContext.getString(R.string.second);
		}

		int i = t % 10;
		switch (i)
		{
			case 1: return mContext.getString(R.string.seconda);
			case 2:
			case 3:
			case 4: return mContext.getString(R.string.seconds);
			default: return mContext.getString(R.string.second);
		}
	}
}
