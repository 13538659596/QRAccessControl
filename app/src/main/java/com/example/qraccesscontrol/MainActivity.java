package com.example.qraccesscontrol;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements OnDateSetListener {

    private EditText qrEditer,startTime, endTime;
    private ImageView qr;
    private TimePickerDialog mDialogAll;
    private View tagView;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timePickDialog();
        initView();
    }


    private void initView() {
        qrEditer = findViewById(R.id.qr_edit);
        qr = findViewById(R.id.qr_img);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
    }


    public void createQr(View view) {
        String QRString = XXTEACAI.createQrString(null);
        qrEditer.setText(QRString);
        Bitmap bitmap = ZXingUtils.createQRCode(qrEditer.getText().toString(), 400,  400);
        qr.setImageBitmap(bitmap);
    }


    private void timePickDialog() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getResources().getString(R.string.cancel))
                .setSureStringId(getResources().getString(R.string.sure))
                .setTitleStringId(getResources().getString(R.string.timePicker))
                .setYearText(getResources().getString(R.string.year))
                .setMonthText(getResources().getString(R.string.month))
                .setDayText(getResources().getString(R.string.day))
                .setHourText(getResources().getString(R.string.hour))
                .setMinuteText(getResources().getString(R.string.minute))
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(14)
                .build();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        Log.e(">>>>>>>>>", "  millseconds "  + millseconds);
        ((EditText)tagView).setText(sf.format(millseconds));
    }

    public void startTime(View view) {
        tagView = startTime;
        mDialogAll.show(getSupportFragmentManager(), "all");
    }

    public void endTime(View view) {
        tagView = endTime;
        mDialogAll.show(getSupportFragmentManager(), "all");
    }
}
