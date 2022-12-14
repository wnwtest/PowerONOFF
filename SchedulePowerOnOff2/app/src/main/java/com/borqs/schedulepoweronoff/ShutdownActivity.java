package com.borqs.schedulepoweronoff;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.borqs.schedulepoweronoff.utils.AlarmUtils;
import com.borqs.schedulepoweronoff.utils.BaseConstants;

@SuppressLint("NewApi")
public class ShutdownActivity extends Activity {
    private static final String TAG = "ShutdownActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_COUNT_DOWN_TIME = "count_down_time";
    private static final int TEN_SECONDS = 10;

    private AlertDialogFragment mFragment;
    private long mCurrentCountDownTime;

    private CountDownTimer timer;
    private static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";

    private static final String ACTION_REQUEST_SHUTDOWN
            = "com.android.internal.intent.action.REQUEST_SHUTDOWN";
    private static final String ACTION_SET_POWEROFF_ALARM =
            "org.codeaurora.poweroffalarm.action.SET_ALARM";
    private static final String ACTION_CANCEL_POWEROFF_ALARM =
            "org.codeaurora.poweroffalarm.action.CANCEL_ALARM";
    private static final String POWER_OFF_ALARM_PACKAGE =
            "com.qualcomm.qti.poweroffalarm";
    private static final String TIME = "time";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        mCurrentCountDownTime = TEN_SECONDS
                * BaseConstants.THOUSAND_MILLISECONDS;
        if (savedInstanceState != null) {
            mCurrentCountDownTime = savedInstanceState
                    .getLong(KEY_COUNT_DOWN_TIME);
        }

        timer = new CountDownTimer(TEN_SECONDS
                * BaseConstants.THOUSAND_MILLISECONDS,
                BaseConstants.THOUSAND_MILLISECONDS) {

            @Override
            public void onTick(long millisUntilFinished) {
                mCurrentCountDownTime = millisUntilFinished;
                setMessage(getResources()
                        .getString(
                                R.string.shutdown_dialog_message,
                                mCurrentCountDownTime
                                        / BaseConstants.THOUSAND_MILLISECONDS));
            }

            @Override
            public void onFinish() {
                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                // cancel the shut down operation when phone in-call
                if(manager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                    shutdown();
                }
                finish();
            }
        };

        showDialog();
        timer.start();
        AlarmUtils.acquireWakeLock(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_COUNT_DOWN_TIME, mCurrentCountDownTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        AlarmUtils.releaseWakeLock();
        super.onDestroy();
    }

    private void showDialog() {
        mFragment = AlertDialogFragment
                .newInstance(R.string.shutdown_dialog_title);
        mFragment.show(getFragmentManager(), "dialog");
    }

    private void setMessage(String msg) {
        ((AlertDialog) mFragment.getDialog()).setMessage(msg);
    }

    private void shutdown() {
        Log.i(TAG, "Request shut down the system");
        Intent intent = new Intent(ACTION_REQUEST_SHUTDOWN);
        intent.putExtra(EXTRA_KEY_CONFIRM, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private static class AlertDialogFragment extends DialogFragment {
        private ShutdownActivity mActivity;

        public static AlertDialogFragment newInstance(int titleId) {
            AlertDialogFragment fragment = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt(KEY_TITLE, titleId);
            fragment.setArguments(args);
            fragment.setCancelable(false);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mActivity = ((ShutdownActivity) getActivity());
            int titleId = getArguments().getInt(KEY_TITLE);
            Dialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(titleId)
                    .setMessage(
                            getResources()
                                    .getString(
                                            R.string.shutdown_dialog_message,
                                            mActivity.mCurrentCountDownTime
                                                    / BaseConstants.THOUSAND_MILLISECONDS))
                    .setPositiveButton(android.R.string.ok,
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mActivity.timer.cancel();
                                    mActivity.finish();
                                    mActivity.shutdown();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mActivity.timer.cancel();
                                    mActivity.finish();
                                }
                            })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}
