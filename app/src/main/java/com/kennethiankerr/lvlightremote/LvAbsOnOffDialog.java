package com.kennethiankerr.lvlightremote;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ken on 5/30/2016.
 */
public class LvAbsOnOffDialog extends Activity {


    private static final String TAG = "LvAbsOnOffDialog";

    private static int mTimeOn;
    private static int mTimeOff;

    String displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstime_popup);
        Log.i(TAG, "in LvAbsOnOffDialog");

        Bundle delayBundle = getIntent().getExtras();
        int mOnTimeHr   = delayBundle.getInt("turnOnHour");
        int mOnTimeMin  = delayBundle.getInt("turnOnMin");
        int mOffTimeHr  = delayBundle.getInt("turnOffHour");
        int mOffTimeMin = delayBundle.getInt("turnOffMin");

        Log.i(TAG, "    onHr: " + mOnTimeHr + "  onMin: " + mOnTimeMin + "  offHr: " + mOffTimeHr+ "  offMin: " + mOffTimeMin);

        Resources res = getResources();

        final TextView absOnTimeText = (TextView) findViewById(R.id.absOnTime);
        if ( mOnTimeHr > 11) {
            if (12==mOnTimeHr) mOnTimeHr += 12;  // makes sure 12:xx is not displayed as 00:xx
            displayText = String.format (res.getString(R.string.turnOnTimeValue), mOnTimeHr-12, mOnTimeMin, "pm");
        } else {
            displayText = String.format (res.getString(R.string.turnOnTimeValue), mOnTimeHr, mOnTimeMin, "am");
        }
        absOnTimeText.setText(displayText);

        final TextView absOffTimeText = (TextView) findViewById(R.id.absOffTime);
        if ( mOffTimeHr > 11) {
            if (12==mOffTimeHr) mOffTimeHr += 12;  // makes sure 12:xx is not displayed as 00:xx
            displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr-12, mOffTimeMin, "pm");
        } else {
            displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr, mOffTimeMin, "am");
        }
        absOffTimeText.setText(displayText);


        // OnClickListener for the Time ON button, calls showTimePickerDialog() to
        // show the Time dialog
        final Button timeOnPickerButton = (Button) findViewById(R.id.timeOn_picker_button);
        timeOnPickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeOnPickerDialog();
            }
        });

        // OnClickListener for the Time OFF button, calls showTimePickerDialog() to
        // show the Date dialog

        final Button timeOffPickerButton = (Button) findViewById(R.id.timeOff_picker_button);
        timeOffPickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeOffPickerDialog();
            }
        });

        // Set up OnClickListener for the Submit Button

        final Button doneButton = (Button) findViewById(R.id.absDoneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {

            // Call enterClicked() when pressed

            @Override
            public void onClick(View v) {
                onDoneButtonClick();
            }
        });
    }

    public void onDoneButtonClick() {

        // Get the current bundle

        Bundle intentData = new Bundle();
        intentData.putInt("timeOn", mTimeOn);
        intentData.putInt ("timeOff", mTimeOff);

        // Create a new intent and save the input from the EditText field as an extra
        Intent configReturnIntent = this.getIntent();
        configReturnIntent.putExtras(intentData);

        // Set Activity's result with result code RESULT_OK
        this.setResult(RESULT_OK, configReturnIntent);

        finish();
    }

    // DialogFragment used to pick a Turn-On time
    public class TimeOnPickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour         = c.get(Calendar.HOUR_OF_DAY);
            int minute       = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mTimeOn = hourOfDay * 60 + minute;
        }
    }

    private void showTimeOnPickerDialog() {
        DialogFragment newFragment = new TimeOnPickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    // DialogFragment used to pick a Turn-Off time
    public static class TimeOffPickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour         = c.get(Calendar.HOUR_OF_DAY);
            int minute       = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            LvAbsOnOffDialog.mTimeOff = hourOfDay*60 + minute;
        }
    }

    private void showTimeOffPickerDialog() {
        DialogFragment newFragment = new TimeOffPickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}

