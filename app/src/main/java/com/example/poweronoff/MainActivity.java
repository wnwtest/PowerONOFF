package com.example.poweronoff;


import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;



import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.poweronoff.customer.MyTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;



import com.example.poweronoff.customer.MyToasty;
import com.example.poweronoff.model.AlarmRequst;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PERMISSION_POWER_OFF_ALARM =   "org.codeaurora.permission.POWER_OFF_ALARM";
    //@BindView(R.id.action_bar_title)
    public TextView action_bar_title;
    //@BindView(R.id.action_bar_right)
    //public TextView action_bar_right;

    //@BindView(R.id.ll_poweron)
    LinearLayout ll_startt;
    //@BindView(R.id.ll_poweroff)
    LinearLayout ll_endt;
    //@BindView(R.id.tv_startt)
    TextView tv_startt;
    //@BindView(R.id.tv_endt)
    TextView tv_endt;

    TimePickerView StartTimePicker, EndTimePicker;
    MyToasty toasty;
    public RxPermissions  rxPermissions = new RxPermissions(this);

    AlarmRequst poweroff;
    AlarmRequst poweron;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toasty = new MyToasty(this);
        poweroff = new AlarmRequst(this);
        poweron =new AlarmRequst(this) ;
        getPermissions();
        setContentView(R.layout.activity_addtem);
        action_bar_title =findViewById(R.id.action_bar_title);
        //action_bar_right = findViewById(R.id.action_bar_right);
        action_bar_title.setText("定时开关机演示");
        //action_bar_right.setText("完成");
        ll_startt =findViewById(R.id.ll_poweron);
        ll_endt =findViewById(R.id.ll_poweroff);
        tv_startt =findViewById(R.id.tv_startt);
        tv_endt =findViewById(R.id.tv_endt);

        ll_startt.setOnClickListener(this);
        ll_endt.setOnClickListener(this);
        /**
        action_bar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("ok");
            }
        });
         **/

    }


    @Override
    public void onClick(View v) {
        MyTimePicker myTimePicker = new MyTimePicker();
        switch (v.getId()) {
            case R.id.ll_poweron:

                StartTimePicker = myTimePicker.getPicker(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        tv_startt.setText(df.format(date));

                        poweron.setPowerON(date.getTime());
                    }
                });
                StartTimePicker.show();
                break;
            case R.id.ll_poweroff:
                //结束时间选择

                EndTimePicker = myTimePicker.getPicker(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        tv_endt.setText(df.format(date));
                        poweroff.setPowerOFF(date.getTime());
                    }
                });
                EndTimePicker.show();
                break;
        }

    }
    /**
     * 获取权限
     */
    private void getPermissions() {
        rxPermissions
                .requestEachCombined(PERMISSION_POWER_OFF_ALARM
                        )
                .subscribe(permission -> { // will emit 1 Permission object
                    if (permission.granted) {
                        //startActivityForResult(new Intent(mContext, GetPicOrMP4Activity.class), 200);
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        //有至少一个权限没有同意
                        showToast("请同意全部权限");
                    } else {
                        //有至少一个权限没有同意且勾选了不在提示
                        showToast("请在权限管理中打开关机闹钟权限");
                    }
                });
    }
  public  void  showToast(String s){

      toasty.showError(s);
  }


    public void goBack(View view) {
        finish();
    }
}