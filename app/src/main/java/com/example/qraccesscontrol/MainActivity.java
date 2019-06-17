package com.example.qraccesscontrol;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

public class MainActivity extends AppCompatActivity implements OnDateSetListener {

    private EditText qrEditer;
    private ImageView qr;
    TimePickerDialog mDialogAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timePickDialog();
        qrEditer = findViewById(R.id.qr_edit);
        qr = findViewById(R.id.qr_img);
    }


    public void createQr(View view) {
        String message = "010502000000000019061710511319062710511303921001921002921003";
        qrEditer.setText(message);
        Bitmap bitmap = ZXingUtils.createQRCode(qrEditer.getText().toString(), 400,  400);
        qr.setImageBitmap(bitmap);
    }


    private void timePickDialog() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("Cancel")
                .setSureStringId("Sure")
                .setTitleStringId("TimePicker")
                .setYearText("Year")
                .setMonthText("Month")
                .setDayText("Day")
                .setHourText("Hour")
                .setMinuteText("Minute")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void startTime(View view) {
        mDialogAll.show(getSupportFragmentManager(), "all");
    }
}
