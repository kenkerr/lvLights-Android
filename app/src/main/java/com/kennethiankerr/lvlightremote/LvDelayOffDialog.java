package com.kennethiankerr.lvlightremote;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Ken on 7/9/2016.
 */
public class LvDelayOffDialog extends Activity {

        private static final String TAG = "LvDelayOffDialog";

        private static int mTimeOff;

        String displayText;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.delaytime_popup);
            Log.i(TAG, "in LvDelayOffDialog");

            Bundle delayBundle = getIntent().getExtras();
            int mOffTimeHr  = delayBundle.getInt("turnOffHour");
            int mOffTimeMin = delayBundle.getInt("turnOffMin");

            Resources res = getResources ();

            final TextView delayOffTimeText = (TextView) findViewById(R.id.delayOffTime);
            if ( mOffTimeHr > 11) {
                if (12==mOffTimeHr) mOffTimeHr += 12;  // makes sure 12:xx is not displayed as 00:xx
                displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr-12, mOffTimeMin, "pm");
            } else {
                displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr, mOffTimeMin, "am");
            }
            delayOffTimeText.setText(displayText);

            // OnClickListener for the Time OFF button, calls showTimePickerDialog() to
            // show the time dialog

            final Button delayTimeOffPickerButton = (Button) findViewById(R.id.delayTimeOff_picker_button);
            delayTimeOffPickerButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDelayTimeOffPickerDialog();
                }
            });

            // Set up OnClickListener for the Submit Button

            final Button doneButton = (Button) findViewById(R.id.delayDoneButton);
            doneButton.setOnClickListener(new View.OnClickListener() {

                // Call enterClicked() when pressed

                @Override
                public void onClick(View v) {
                    onDelayDoneButtonClick();
                }
            });
        }

        public void onDelayDoneButtonClick() {

            // Get the current bundle

            Bundle intentData = new Bundle();
            intentData.putInt ("timeOff", mTimeOff);

            // Create a new intent and save the input from the EditText field as an extra
            Intent configReturnIntent = this.getIntent();
            configReturnIntent.putExtras(intentData);

            // Set Activity's result with result code RESULT_OK
            this.setResult(RESULT_OK, configReturnIntent);

            finish();
        }

        // DialogFragment used to pick a Turn-Off time
        public static class DelayTimeOffPickerFragment extends DialogFragment implements
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
                LvDelayOffDialog.mTimeOff = hourOfDay*60 + minute;
            }
        }

        private void showDelayTimeOffPickerDialog() {
            DialogFragment newFragment = new DelayTimeOffPickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
}
