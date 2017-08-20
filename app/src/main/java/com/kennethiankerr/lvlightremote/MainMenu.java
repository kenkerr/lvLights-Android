package com.kennethiankerr.lvlightremote;

import com.kennethiankerr.lvlightremote.util.SystemUiHider;
import com.kennethiankerr.lvlightremote.util.TcpClient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.wimpi.modbus.procimg.SimpleRegister;

import java.util.ArrayList;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */

public class MainMenu extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;
    static TcpClient mTcpClient;

    // IDs for menu items
    private static final int MENU_SETTINGS = Menu.FIRST;

    private static final int CONFIGURE_REQUEST        = 1;
    private static final int ABS_ON_OFF_TIMES_REQUEST = 2;
    private static final int DELAY_OFF_TIME_REQUEST   = 3;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private static final String TAG = "MainMenu";
    private ControllerComm cc;
    private ArrayList<Integer> registers;

    private String mServerIP = "192.168.8.51";
    private int mAbsOnTime;
    private int mAbsOffTime;

    static final int MANUAL_OFF   = 1;
    static final int MANUAL_ON    = 2;
    static final int MANUAL_BLINK = 3;

    // Values for the following modes are offset by 10 so as not to conflict with MANUAL commands above
    static final int MODE_MANUAL         = 11;
    static final int MODE_ABS_ON_OFF     = 12;
    static final int MODE_DUSK_TO_DAWN   = 13;
    static final int MODE_DUSK_TO_OFFSET = 14;

    // Misc send commands
    static final int SEND_ON_TIME        = 21;
    static final int SEND_OFF_TIME       = 22;

    public MainMenu() {
        cc = new ControllerComm();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView  = findViewById(R.id.fullscreen_content);

        registers = new ArrayList<>();

        cc.connect(mServerIP);
        cc.getRegisters(registers);
        setUIValues(registers);

/*
        // Set up an instance of SystemUiHider to control the system UI for
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

            // Cached values.
            int mControlsHeight;
            int mShortAnimTime;

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)

            public void onVisibilityChange(boolean visible) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    // If the ViewPropertyAnimator API is available
                    // (Honeycomb MR2 and later), use it to animate the
                    // in-layout UI controls at the bottom of the
                    // screen.
                    if (mControlsHeight == 0) {
                        mControlsHeight = controlsView.getHeight();
                    }
                    if (mShortAnimTime == 0) {
                        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                    }
                    controlsView.animate()
                            .translationY(visible ? 0 : mControlsHeight)
                            .setDuration(mShortAnimTime);
                } else {
                    // If the ViewPropertyAnimator APIs aren't
                    // available, simply show or hide the in-layout UI
                    // controls.
                    controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                if (visible && AUTO_HIDE) {
                    // Schedule a hide().
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener (new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
*/
    }

/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SETTINGS:
                startConfigure();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startConfigure() {
        Log.i(TAG, "Entered startConfigure()");
        Intent lvConfigureIntent = new Intent(this, LvConfigure.class);

        // Start an Activity using that intent and the request code defined above
        startActivityForResult(lvConfigureIntent, CONFIGURE_REQUEST);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //------------------------------------------------------------
    // Mode selection radio buttons in second section of display
    //------------------------------------------------------------
    public void onManualClicked (View view) {
        Log.i(TAG, "Manual mode clicked");
        cc.sendRequest(MODE_MANUAL);
        cc.getRegisters(registers);
        setUIValues(registers);
    }
    public void onAbsOnOffClicked (View view) {
        Log.i(TAG, "Absolute On/Off mode clicked");

        getAbsOnOffTimes();
    }
    private void getAbsOnOffTimes() {
        Log.i(TAG, "Calling activity to handle time selections...");
        Intent lvAbsOnOffDialogIntent = new Intent(this, LvAbsOnOffDialog.class);

        // Bundle up current on and off times for display in abs on/off dialog
        cc.getRegisters(registers);
        int mOnTimeHr      = (registers.get(2) / 60);
        int mOnTimeMin     =  registers.get(2) % 60;
        int mOffTimeHr     = (registers.get(3) / 60);
        int mOffTimeMin    =  registers.get(3) % 60;

        Log.i(TAG, "    onHr: " + mOnTimeHr + "  onMin: " + mOnTimeMin + "  offHr: " + mOffTimeHr+ "  offMin: " + mOffTimeMin);


        Bundle absBundle = new Bundle();
        absBundle.putInt("turnOnHour",  mOnTimeHr);
        absBundle.putInt("turnOnMin",   mOnTimeMin);

        absBundle.putInt("turnOffHour", mOffTimeHr);
        absBundle.putInt("turnOffMin",  mOffTimeMin);

        lvAbsOnOffDialogIntent.putExtras(absBundle);

        // Start an Activity using that intent and the request code defined above
        startActivityForResult(lvAbsOnOffDialogIntent, ABS_ON_OFF_TIMES_REQUEST);
    }
    public void onDuskDawnClicked (View view) {
        Log.i(TAG, "Dusk/Dawn mode clicked");
        cc.sendRequest(MODE_DUSK_TO_DAWN);
        cc.getRegisters(registers);
        setUIValues(registers);
    }

    public void onDuskDelayClicked (View view) {
        Log.i(TAG, "Dusk-Delay mode clicked");

        getDelayOffTime();
    }
    private void getDelayOffTime() {
        Log.i(TAG, "Calling activity to handle delay time selection...");
        Intent lvDelayOffDialogIntent = new Intent(this, LvDelayOffDialog.class);

        cc.getRegisters(registers);
        int mOffTimeHr     = (registers.get(3) / 60);
        int mOffTimeMin    =  registers.get(3) % 60;

        Bundle delayBundle = new Bundle();
        delayBundle.putInt("turnOffHour", mOffTimeHr);
        delayBundle.putInt("turnOffMin", mOffTimeMin);
        lvDelayOffDialogIntent.putExtras(delayBundle);

        // Start an Activity using that intent and the request code defined above
        startActivityForResult(lvDelayOffDialogIntent, DELAY_OFF_TIME_REQUEST);
    }

    //------------------------------------------------------------
    // Direct-action manual buttons in fourth section of display
    //------------------------------------------------------------
    public void lightsON (View view) {
        Log.i(TAG, "Turning lights ON");
        cc.sendRequest(MANUAL_ON);
        cc.getRegisters(registers);
        setUIValues(registers);
    }

    public void lightsOFF (View view) {
        Log.i(TAG, "Turning lights OFF");
        cc.sendRequest(MANUAL_OFF);
        cc.getRegisters(registers);
        setUIValues(registers);
    }

    public void lightsBLINK (View view) {
        Log.i(TAG, "Blinking lights");
        cc.sendRequest(MANUAL_BLINK);
        cc.getRegisters(registers);
        setUIValues(registers);
    }

    public void setUIValues (ArrayList <Integer> registers) {
        int mMode;
        int mOnTimeHr, mOnTimeMin;
        int mOffTimeHr, mOffTimeMin;
        int mCurrentState;

        String displayText;

        mMode         = registers.get(0);
        mOnTimeHr     = (registers.get(2) / 60) % 12;
        if (mOnTimeHr==0) mOnTimeHr = 12;
        mOnTimeMin    = registers.get(2) % 60;
        mOffTimeHr    = (registers.get(3) / 60) % 12;
        if (mOffTimeHr==0) mOffTimeHr = 12;
        mOffTimeMin   = registers.get(3) % 60;
        mCurrentState = registers.get(16);

        Log.i(TAG, "mCurrentState = "+mCurrentState);

        // Set radio button to show the current mode
        RadioButton rb[] = new RadioButton[4];
        rb[0] = (RadioButton) findViewById(R.id.radio_manual);
        rb[1] = (RadioButton) findViewById(R.id.radio_onOffTime);
        rb[2] = (RadioButton) findViewById(R.id.radio_duskDawn);
        rb[3] = (RadioButton) findViewById(R.id.radio_duskDelay);
        for (int i=0; i<4; i++) {
            if (i == (mMode - 1)) {
                rb[i].setChecked(true);
            } else {
                rb[i].setChecked(false);
            }
        }

        Resources res = getResources ();

        final TextView textViewOnTime = (TextView) findViewById(R.id.onTime);
        if ( (registers.get(2) / 60) > 11) {
            displayText = String.format (res.getString(R.string.turnOnTimeValue), mOnTimeHr, mOnTimeMin, "pm");
        } else {
            displayText = String.format (res.getString(R.string.turnOnTimeValue), mOnTimeHr, mOnTimeMin, "am");
        }
        textViewOnTime.setText(displayText);

        final TextView textViewOffTime = (TextView) findViewById(R.id.offTime);
        if ( (registers.get(3) / 60) > 11) {
            displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr, mOffTimeMin, "pm");
        } else {
            displayText = String.format (res.getString(R.string.turnOffTimeValue), mOffTimeHr, mOffTimeMin, "am");
        }
        textViewOffTime.setText(displayText);

        final TextView textViewOnOffStatus = (TextView) findViewById(R.id.onOffStatus);
        if (1 == mCurrentState) {
            textViewOnOffStatus.setText("OFF");
        } else if (2 == mCurrentState) {
            textViewOnOffStatus.setText("ON");
        } else {
            textViewOnOffStatus.setText("UNKNOWN");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int startingRegisterNumber;

        Log.i(TAG, "Entered onActivityResult()");

        // RESULT_OK result code and a recognized request code
        // If so, update the Textview showing the user-entered text.
        if (requestCode == CONFIGURE_REQUEST  && resultCode == RESULT_OK) {
            Bundle configData = data.getExtras();
            mServerIP = configData.getString("IP");

            Log.i(TAG, "New IP address = "+mServerIP);

            // Disconnect the current connection and re-establish using the new IP
            cc.close();
            cc.connect(mServerIP);
        }
        else if (requestCode == ABS_ON_OFF_TIMES_REQUEST && resultCode == RESULT_OK) {

            Bundle configData = data.getExtras();
            mAbsOnTime  = configData.getInt("timeOn");
            mAbsOffTime = configData.getInt("timeOff");

            Log.i (TAG, "setting timeOn="+mAbsOnTime);

            SimpleRegister[] regArray = new SimpleRegister[2];
            regArray[0] = new SimpleRegister();
            regArray[0].setValue(mAbsOnTime);

            regArray[1] = new SimpleRegister();
            regArray[1].setValue(mAbsOffTime);

            startingRegisterNumber = 2;

            cc.setControllerRegisters(startingRegisterNumber, regArray);
            cc.sendRequest(MODE_ABS_ON_OFF);

            cc.getRegisters(registers);
            setUIValues(registers);
        }
        else if (requestCode == DELAY_OFF_TIME_REQUEST && resultCode == RESULT_OK) {

            Bundle configData = data.getExtras();
//            mAbsOnTime  = configData.getInt("timeOn");
            mAbsOffTime = configData.getInt("timeOff");

            Log.i (TAG, "setting timeOff="+mAbsOffTime);

            SimpleRegister[] regArray = new SimpleRegister[1];
            regArray[0] = new SimpleRegister();
            regArray[0].setValue(mAbsOffTime);

            startingRegisterNumber = 3;

            cc.setControllerRegisters(startingRegisterNumber, regArray);
            cc.sendRequest(MODE_DUSK_TO_OFFSET);

            cc.getRegisters(registers);
            setUIValues(registers);
        }
    }
}
