package xyz.flyinghealthtimer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import xyz.flyinghealthtimer.R;

public class StopWatchFragment extends BaseFragment {

    public StopWatchFragment() {

    }


    TextView mEllapse;

    TextView mSplit;

    ImageButton mBtnStart;

    TextView mBtnSplit;
    ImageButton mBtnReset;

    //스톱워치의 상태를 위한 상수

    final static int IDLE = 0;

    final static int RUNNING = 1;

    final static int PAUSE = 2;

    int mStatus = IDLE;//처음 상태는 IDLE

    long mBaseTime;

    long mPauseTime;

    int mSplitCount = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setTitle(R.string.title_stopwatch);

        if (rootView != null) return rootView;

        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        mEllapse = (TextView) view.findViewById(R.id.ellapse);

        mSplit = (TextView) view.findViewById(R.id.split);
        mSplit.setMovementMethod(new ScrollingMovementMethod());

        mBtnStart = (ImageButton) view.findViewById(R.id.btnstart);
        mBtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClick(v);
            }
        });

        mBtnSplit = (TextView) view.findViewById(R.id.btnsplit);
        mBtnSplit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClick(v);
            }
        });
        mBtnReset = (ImageButton) view.findViewById(R.id.btnreset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClick(v);
            }
        });

        return view;
    }


    //스톱워치는 위해 핸들러를 만든다.

    Handler mTimer = new Handler() {


        //핸들러는 기본적으로 handleMessage에서 처리한다.

        public void handleMessage(android.os.Message msg) {

            //텍스트뷰를 수정해준다.

            mEllapse.setText(getEllapse());

            //메시지를 다시 보낸다.

            mTimer.sendEmptyMessage(0);//0은 메시지를 구분하기 위한 것

        }

        ;

    };


    @Override
    public void onDestroy() {


        mTimer.removeMessages(0);//메시지를 지워서 메모리릭 방지

        super.onDestroy();

    }


    public void mOnClick(View v) {

        switch (v.getId()) {
            //play버튼
            case R.id.btnstart:
                switch (mStatus) {
                    // 초기화된 상태에서
                    case IDLE:
                        mBaseTime = SystemClock.elapsedRealtime();
                        mTimer.sendEmptyMessage(0);

                        mBtnStart.setImageResource(R.drawable.ic_pause_button);
                        mBtnSplit.setVisibility(View.VISIBLE);

                        mStatus = RUNNING;

                        break;

                    // 실행중일 때
                    case RUNNING:
                        mTimer.removeMessages(0);
                        mPauseTime = SystemClock.elapsedRealtime();

                        mBtnStart.setImageResource(R.drawable.ic_play_button);
                        mBtnSplit.setVisibility(View.INVISIBLE);
                        mBtnReset.setVisibility(View.VISIBLE);

                        mStatus = PAUSE;//상태를 멈춤으로 표시

                        break;

                        // 중지상태일 때
                    case PAUSE:
                        long now = SystemClock.elapsedRealtime();

                        mBaseTime += (now - mPauseTime);

                        mTimer.sendEmptyMessage(0);

                        mBtnStart.setImageResource(R.drawable.ic_pause_button);
                        mBtnReset.setVisibility(View.INVISIBLE);
                        mBtnSplit.setVisibility(View.VISIBLE);

                        mStatus = RUNNING;

                        break;
                }

                break;

            //기록버튼
            case R.id.btnsplit:

                switch (mStatus) {

                    //RUNNING 상태일 때.
                    case RUNNING:
                        String sSplit = mSplit.getText().toString();
                        sSplit += String.format("%02d    %s\n", mSplitCount, getEllapse());
                        mSplit.setText(sSplit);

                        mSplitCount++;

                        final int scrollAmount = mSplit.getLayout().getLineTop(mSplit.getLineCount()) - mSplit.getHeight();

                        if (scrollAmount > 0)
                            mSplit.scrollTo(0, scrollAmount);
                        else
                            mSplit.scrollTo(0, 0);

                        break;
                }

            //reset버튼
            case R.id.btnreset :
                switch (mStatus) {
                    case PAUSE:
                        mTimer.removeMessages(0);

                        mBtnStart.setImageResource(R.drawable.ic_play_button);
                        mBtnSplit.setVisibility(View.INVISIBLE);
                        mBtnReset.setVisibility(View.INVISIBLE);

                        mEllapse.setText("00:00:00");
                        mSplitCount = 1;
                        mStatus = IDLE;

                        mSplit.setText("");

                        break;

                }
                break;

        }

    }


    String getEllapse() {

        long now = SystemClock.elapsedRealtime();

        long ell = now - mBaseTime;//현재 시간과 지난 시간을 빼서 ell값을 구하고

        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.

        String sEll = String.format("%02d:%02d:%02d", ell / 1000 / 60, (ell / 1000) % 60, (ell % 1000) / 10);

        return sEll;

    }

}



