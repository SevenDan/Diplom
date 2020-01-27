package com.example.diplom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class UpdateEventActivity extends AppCompatActivity {

    private Switch switchTimeEvent, switchTimeEventremind;
    private FloatingActionButton fab;
    private EditText et_name, et_coment;
    private String timeT = " ";
    private String timeR = " ";
    private String strDate;

    private AlarmManager alarmManager;
    private Calendar cal;
    int h = -1;
    int m = -1;

    protected DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = new DBHelper(this, 1);
        et_name = (EditText) findViewById(R.id.et_upeventName);
        et_coment = (EditText) findViewById(R.id.Comment_upevent);
        switchTimeEventremind = (Switch) findViewById(R.id.sw_timeupEvent_reminder);
        switchTimeEvent = (Switch) findViewById(R.id.sw_timeupEvent);

        Toolbar toolbar = findViewById(R.id.toolbar_upevent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   Событие");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("message");
        String []event = db.viewEventID(id);
        et_name.setText(event[0]);
        et_coment.setText(event[3]);
        if(event[1].length() == 1) {
            switchTimeEventremind.setChecked(false);
        }
        else {
            switchTimeEventremind.setChecked(true);
            timeR = event[1];
            switchTimeEventremind.setText("Напоминания установлено на " + timeR);
        }

        if(event[2].length() == 1) {
            switchTimeEvent.setChecked(false);
        }
        else {
            switchTimeEvent.setChecked(true);
            timeT = event[2];
            switchTimeEvent.setText("Время события установлено на " + timeT);
        }


        switchTimeEventremind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTimeEventremind.isChecked())
                    openDialogReminder();
                else
                if (!(switchTimeEventremind.isChecked())) {
                    switchTimeEventremind.setText("Напоминание");
                    timeR = " ";
                    Toast.makeText(
                            getApplicationContext(),
                            "Напоминание отключено",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        switchTimeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTimeEvent.isChecked())
                    openDialogTime();
                else
                if (!(switchTimeEvent.isChecked())) {
                    switchTimeEvent.setText("Время события");
                    timeT = " ";
                    Toast.makeText(
                            getApplicationContext(),
                            "Отключено",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });


        fab = findViewById(R.id.fab_OK_upevent);

        //Нажатие на кнопку OK
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.updateEvent(id, et_name.getText().toString(), timeR, timeT, et_coment.getText().toString());

                if(switchTimeEventremind.isChecked()) {

                    cal = Calendar.getInstance();

                    cal = DS.setDate(DS.date);
                    cal.set(Calendar.HOUR_OF_DAY, h);
                    cal.set(Calendar.MINUTE, m);
                    cal.set(Calendar.SECOND, 0);

                    startAlarm(cal, Integer.valueOf(id));
                }
                else cancelAlarm(Integer.valueOf(id));

                Toast.makeText(
                        getApplicationContext(),
                        "Сохранено",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Toast.makeText(
                    getApplicationContext(),
                    "Отмена",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void openDialogReminder() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        h = hourOfDay;
                        m = minute;

                        if(hourOfDay < 10)
                            if(minute < 10) timeR = "0" + hourOfDay + ":0" + minute;
                            else timeR = "0" + hourOfDay + ":" + minute;
                        else
                            if(minute < 10) timeR = hourOfDay + ":0" + minute;
                            else timeR = hourOfDay + ":" + minute;
                        Toast.makeText(getApplicationContext(),timeR, Toast.LENGTH_SHORT).show();
                        switchTimeEventremind.setText("Напоминания установлено на " + timeR);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switchTimeEventremind.setChecked(false);
            }
        });
        timePickerDialog.show();
    }

    void openDialogTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if(hourOfDay < 10)
                    if(minute < 10) timeT = "0" + hourOfDay + ":0" + minute;
                    else timeT = "0" + hourOfDay + ":" + minute;
                else
                if(minute < 10) timeT = hourOfDay + ":0" + minute;
                else timeT = hourOfDay + ":" + minute;
                Toast.makeText(getApplicationContext(),timeT, Toast.LENGTH_SHORT).show();
                switchTimeEvent.setText("Время события установлено на " + timeT);
            }
        }, mHour, mMinute, true);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switchTimeEvent.setChecked(false);
            }
        });
        timePickerDialog.show();
    }

    private void startAlarm(Calendar c, int id){

        String t = timeT;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiverEvent.class);
        intent.putExtra("mes", et_name.getText().toString());
        intent.putExtra("idN", Integer.toString(id));
        intent.putExtra("time", t);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int id){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiverEvent.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
