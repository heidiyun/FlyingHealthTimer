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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import xyz.flyinghealthtimer.R;
import xyz.flyinghealthtimer.fragment.BaseFragment;

public class StopWatchFragment extends BaseFragment {

    public StopWatchFragment() {

    }


    TextView mEllapse;

    TextView mSplit;

    ImageButton mBtnStart;

    ImageButton mBtnSplit;

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

        mBtnSplit = (ImageButton) view.findViewById(R.id.btnsplit);
        mBtnSplit.setOnClickListener(new View.OnClickListener() {

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


            //시작 버튼이 눌리면

            case R.id.btnstart:

                switch (mStatus) {
                    case IDLE:
                        mBaseTime = SystemClock.elapsedRealtime();
                        mTimer.sendEmptyMessage(0);


                        mBtnStart.setImageResource(R.drawable.ic_pause_button);
                        mBtnSplit.setImageResource(R.drawable.ic_stat_image_timer);

                        //옆버튼의 Enable을 푼 다음

                        mBtnSplit.setEnabled(true);

                        //상태를 RUNNING으로 바꾼다.

                        mStatus = RUNNING;

                        break;


                    //버튼이 실행상태이면

                    case RUNNING:

                        //핸들러 메시지를 없애고

                        mTimer.removeMessages(0);


                        //멈춘 시간을 파악

                        mPauseTime = SystemClock.elapsedRealtime();


                        //버튼 텍스트를 바꿔줌
                        mBtnStart.setImageResource(R.drawable.ic_play_button);

                        mBtnSplit.setImageResource(R.drawable.ic_reset);

                        mStatus = PAUSE;//상태를 멈춤으로 표시

                        break;

                    //멈춤이면

                    case PAUSE:

                        //현재값 가져옴

                        long now = SystemClock.elapsedRealtime();

                        //베이스타임 = 베이스타임 + (now - mPauseTime)

                        //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로..

                        mBaseTime += (now - mPauseTime);


                        mTimer.sendEmptyMessage(0);


                        //텍스트 수정

                        mBtnStart.setImageResource(R.drawable.ic_pause_button);

                        mBtnSplit.setImageResource(R.drawable.ic_stat_image_timer);

                        mStatus = RUNNING;

                        break;

                }

                break;


            case R.id.btnsplit:

                switch (mStatus) {

                    //RUNNING 상태일 때.

                    case RUNNING:


                        //기존의 값을 가져온뒤 이어붙이기 위해서

                        String sSplit = mSplit.getText().toString();


                        //+연산자로 이어붙임

                        sSplit += String.format("%d  >>  %s\n", mSplitCount, getEllapse());


                        //텍스트뷰의 값을 바꿔줌

                        mSplit.setText(sSplit);

                        mSplitCount++;

                        final int scrollAmount = mSplit.getLayout().getLineTop(mSplit.getLineCount()) - mSplit.getHeight();
                        // if there is no need to scroll, scrollAmount will be <=0
                        if (scrollAmount > 0)
                            mSplit.scrollTo(0, scrollAmount);
                        else
                            mSplit.scrollTo(0, 0);


                        break;

                    case PAUSE://여기서는 초기화버튼이 됨

                        //핸들러를 없애고

                        mTimer.removeMessages(0);


                        //처음상태로 원상복귀시킴

                        mBtnStart.setImageResource(R.drawable.ic_play_button);

                        mBtnSplit.setImageResource(R.drawable.ic_reset);

                        mEllapse.setText("00:00:00");
                        mSplitCount = 1;
                        mStatus = IDLE;

                        mSplit.setText("");

                        mBtnSplit.setEnabled(false);

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



