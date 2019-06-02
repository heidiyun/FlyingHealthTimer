package xyz.flyinghealthtimer;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StopWatchFragment extends Fragment {

    public StopWatchFragment() {

    }


    TextView mEllapse;

    TextView mSplit;

    Button mBtnStart;

    Button mBtnSplit;

    //스톱워치의 상태를 위한 상수

    final static int IDLE = 0;

    final static int RUNNING = 1;

    final static int PAUSE = 2;

    int mStatus = IDLE;//처음 상태는 IDLE

    long mBaseTime;

    long mPauseTime;

    int mSplitCount;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        mEllapse = (TextView) view.findViewById(R.id.ellapse);

        mSplit = (TextView) view.findViewById(R.id.split);

        mBtnStart = (Button) view.findViewById(R.id.btnstart);
        mBtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClick(v);
            }
        });

        mBtnSplit = (Button) view.findViewById(R.id.btnsplit);
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

                    //IDLE상태이면

                    case IDLE:

                        //현재 값을 세팅해주고

                        mBaseTime = SystemClock.elapsedRealtime();

                        //핸드러로 메시지를 보낸다

                        mTimer.sendEmptyMessage(0);

                        //시작을 중지로 바꾸고

                        mBtnStart.setText("중지");

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

                        mBtnStart.setText("시작");

                        mBtnSplit.setText("초기화");

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

                        mBtnStart.setText("중지");

                        mBtnSplit.setText("기록");

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

                        sSplit += String.format("%d => %s\n", mSplitCount, getEllapse());


                        //텍스트뷰의 값을 바꿔줌

                        mSplit.setText(sSplit);

                        mSplitCount++;

                        break;

                    case PAUSE://여기서는 초기화버튼이 됨

                        //핸들러를 없애고

                        mTimer.removeMessages(0);


                        //처음상태로 원상복귀시킴

                        mBtnStart.setText("시작");

                        mBtnSplit.setText("기록");

                        mEllapse.setText("00:00:00");

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



