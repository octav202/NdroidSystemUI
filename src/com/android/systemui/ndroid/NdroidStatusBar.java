package com.android.systemui.ndroid;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class NdroidStatusBar extends RelativeLayout {

    private static final String TAG = "Ndroid_StatusBar";

    private Context mContext;

    // Dimensions
    private int SETTINGS_HEIGHT = 920;
    private int BAR_HEIGHT = 85;
    private int BUTTON_MARGIN = 80;
    private int BUTTON_SIZE = 100;
    private int ICON_SIZE = 55;
    private int ICON_MARGIN = 7;
    private int ICON_MARGIN_END = 35;
    private int SETTINGS_TOP_MARGIN_COLLAPSED = -920;
    private int LAYOUT_MARGIN = 15;
    private int BAR_ICON_MARGIN = 45;
    private int GRID_HEIGHT = 400;
    private int GRID_MARGIN_START_END = 100;

    private int mLastTopMargin;

    // [_____ Settings _____]
    private RelativeLayout mSettingsLayout;
    private int mSettingsLayoutId = 5;

    // Buttons
    private RelativeLayout mButtonsLayout;
    private Button mWifiButton;
    private Button mMobileDataButton;
    private Button mRingtoneButton;
    private Button mBluetoothButton;
    private Button mOrientationButton;
    private Button mLocationButton;
    private Button mNfcButton;
    private Button mAirplaneButton;
    private int BUTTONS_LAYOUT_ID = 10;
    private int WIFI_BUTTON_ID = 101;
    private int MOBILE_DATA_BUTTON_ID = 102;
    private int RINGTONE_BUTTON_ID = 103;
    private int BLUETOOTH_BUTTON_ID = 104;
    private int ORIENTATION_BUTTON_ID = 105;
    private int LOCATION_BUTTON_ID = 106;
    private int NFC_BUTTON_ID = 107;
    private int AIRPLANE_BUTTON_ID = 108;
   
    // Function status
    private boolean mWifi = false;
    private boolean mMobileData = false;
    private int mRingtone = 1;
    private boolean mBluetooth = false;
    private boolean mOrientation = false;
    private boolean mLocation = false;
    private boolean mNfc = false;
    private boolean mAirplane = false;
    
    // Brightness
    private RelativeLayout mBrightnessLayout;
    private int mBrightnessLayoutId = 11;
    private SeekBar mBrightnessBar;
    private View mBrightnessStart;
    private int mBrightnessStartId = 12;
    private View mBrightnessEnd;
    private int mBrightnessEndId = 13;
    private TextView mBrightnessText;

    // Ringtone
    private RelativeLayout mRingtoneLayout;
    private int mRingtoneLayoutId = 14;
    private SeekBar mRingtoneBar;
    private View mRingtoneStart;
    private int mRingtoneStartId = 15;
    private View mRingtoneEnd;
    private int mRingtoneEndId = 16;
    private TextView mRingtoneText;

    // Modes - Personal Assistant
    private RelativeLayout mModesLayout;
    private int MODES_LAYOUT_ID = 30;
    private GridView mModesGrid;
    private int MODES_GRID_ID = 31;
    private int MODES_GRID_NUM_COLUMNS = 4;
    private int MODES_GRID_VERTICAL_SPACE = 10;
    private int MODES_GRID_HORIZONTAL_SPACE = 10;


    // [_____ Status Bar _____] //
    private RelativeLayout mIconLayout;
    private int mIconLayoutId = 20;

    // Carrier
    private TextView mCarrier;
    // Battery
    private int mBatteryLevel = 100;
    private View mBatteryView;
    // Clock
    private TextView mClockText;
    private int mClockId = 21;
    // Status Bar Icons
    private View mWifiIcon;
    private int mWifiIconId = 22;
    private View mBluetoothIcon;
    private int mBluetoothIconId = 23;
    private View mRingtoneIcon;
    private int mRingtoneIconId = 24;
    private View mMobileDataIcon;
    private int mMobileDataIconId = 25;
    private View mOrientationIcon;
    private int mOrientationIconId = 26;
    private View mLocationIcon;
    private int mLocationIconId = 27;
    private View mNfcIcon;
    private int mNfcIconId = 28;
    private View mAirplaneIcon;
    private int mAirplaneIconId = 29;

    // Ringtone Values
    private static final int ON = 1;
    private static final int OFF = 2;
    private static final int VIBRATE = 3;
    RingtoneVolumeObserver mVolumeObserver;
    WindowManager mWindowManager;

    // Touch Events
    private float mStartPoint;
    private int mOffset = 0;
    private static final int MIN_OFFSET = -920;
    private static final int MAX_OFFSET = 920;

    public NdroidStatusBar(Context context) {
        super(context);
        Log.d(TAG, "NdroidStatusBar()");
        mContext = context;
        initSettingsLayout();
        initIconLayout();
        initListeners();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        setBackgroundColor(Color.TRANSPARENT);
        addView(mSettingsLayout);
        addView(mIconLayout);
        setBatteryLevel(0);

        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(this, params);

        mVolumeObserver = new RingtoneVolumeObserver(mContext, new Handler());
        mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI,
                true, mVolumeObserver);

        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!n.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
            return;
        }
    }

    // Init Settings Layout
    private void initSettingsLayout() {
        mSettingsLayout = new RelativeLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, SETTINGS_HEIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.topMargin = SETTINGS_TOP_MARGIN_COLLAPSED;
        mSettingsLayout.setLayoutParams(params);
        mSettingsLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.status_bar_background));
        mSettingsLayout.setId(mSettingsLayoutId);

        initButtonsLayout();
        initBrightnessLayout();
        initRingtoneLayout();
        initModesLayout();
    }

    private void initButtonsLayout() {
        // Buttons Layout
        mButtonsLayout = new RelativeLayout(mContext);
        LayoutParams buttonsParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        buttonsParams.setMargins(LAYOUT_MARGIN,LAYOUT_MARGIN,LAYOUT_MARGIN,LAYOUT_MARGIN);
        buttonsParams.addRule(CENTER_HORIZONTAL, TRUE);
        mButtonsLayout.setLayoutParams(buttonsParams);
        mButtonsLayout.setBackgroundColor(Color.TRANSPARENT);
        mButtonsLayout.setId(BUTTONS_LAYOUT_ID);

        // Wifi
        mWifiButton = new Button(mContext);
        mWifiButton.setId(WIFI_BUTTON_ID);
        LayoutParams wParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        mWifiButton.setLayoutParams(wParams);
        wParams.setMarginStart(BUTTON_MARGIN);
        wParams.setMarginEnd(BUTTON_MARGIN);
        wParams.addRule(ALIGN_PARENT_START, TRUE);
        mWifiButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi));
        mWifiButton.setGravity(Gravity.CENTER);
        mButtonsLayout.addView(mWifiButton);

        // Mobile Data
        mMobileDataButton = new Button(mContext);
        mMobileDataButton.setId(MOBILE_DATA_BUTTON_ID);
        LayoutParams mParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        mParams.setMarginStart(BUTTON_MARGIN);
        mParams.setMarginEnd(BUTTON_MARGIN);
        mParams.addRule(END_OF, WIFI_BUTTON_ID);
        mMobileDataButton.setLayoutParams(mParams);
        mMobileDataButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_mobile_data));
        mButtonsLayout.addView(mMobileDataButton);

        // Ringtone
        mRingtoneButton = new Button(mContext);
        mRingtoneButton.setId(RINGTONE_BUTTON_ID);
        LayoutParams rParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        rParams.setMarginStart(BUTTON_MARGIN);
        rParams.setMarginEnd(BUTTON_MARGIN);
        rParams.addRule(END_OF, MOBILE_DATA_BUTTON_ID);
        mRingtoneButton.setLayoutParams(rParams);
        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
        mButtonsLayout.addView(mRingtoneButton);

        // Bluetooth
        mBluetoothButton = new Button(mContext);
        mBluetoothButton.setId(BLUETOOTH_BUTTON_ID);
        LayoutParams bParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        bParams.setMarginStart(BUTTON_MARGIN);
        bParams.setMarginEnd(BUTTON_MARGIN);
        bParams.addRule(END_OF, RINGTONE_BUTTON_ID);
        mBluetoothButton.setLayoutParams(bParams);
        mBluetoothButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth));
        mButtonsLayout.addView(mBluetoothButton);

        // Screen Rotation
        mOrientationButton = new Button(mContext);
        mOrientationButton.setId(ORIENTATION_BUTTON_ID);
        LayoutParams oParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        oParams.setMargins(BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN);
        oParams.addRule(BELOW, WIFI_BUTTON_ID);
        oParams.addRule(ALIGN_PARENT_START, TRUE);
        mOrientationButton.setLayoutParams(oParams);
        mOrientationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_screen_lock_rotation));
        mButtonsLayout.addView(mOrientationButton);

        // Location
        mLocationButton = new Button(mContext);
        mLocationButton.setId(LOCATION_BUTTON_ID);
        LayoutParams locParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        locParams.setMargins(BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN);
        locParams.addRule(BELOW, MOBILE_DATA_BUTTON_ID);
        locParams.addRule(END_OF, ORIENTATION_BUTTON_ID);
        mLocationButton.setLayoutParams(locParams);
        mLocationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_location));
        mButtonsLayout.addView(mLocationButton);

        // NFC
        mNfcButton = new Button(mContext);
        mNfcButton.setId(NFC_BUTTON_ID);
        LayoutParams nParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        nParams.setMargins(BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN);
        nParams.addRule(END_OF, LOCATION_BUTTON_ID);
        nParams.addRule(BELOW, RINGTONE_BUTTON_ID);
        mNfcButton.setLayoutParams(nParams);
        mNfcButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_nfc));
        mButtonsLayout.addView(mNfcButton);

        // Airplane
        mAirplaneButton = new Button(mContext);
        mAirplaneButton.setId(AIRPLANE_BUTTON_ID);
        LayoutParams aParams = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        aParams.setMargins(BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN);
        aParams.addRule(BELOW, BLUETOOTH_BUTTON_ID);
        aParams.addRule(END_OF, NFC_BUTTON_ID);
        mAirplaneButton.setLayoutParams(aParams);
        mAirplaneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_airplanemode));
        mButtonsLayout.addView(mAirplaneButton);

        // Initial states - all disabled
        mAirplaneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_airplanemode_disabled));
        mLocationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_location_disabled));
        mOrientationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_screen_lock_rotation_disabled));
        mWifiButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi_disabled));
        mMobileDataButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_mobile_data_disabled));
        mBluetoothButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth_disabled));
        mNfcButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_nfc_disabled));

        mSettingsLayout.addView(mButtonsLayout);
    }

    private void initBrightnessLayout() {
        // Brightness Layout
        mBrightnessLayout = new RelativeLayout(mContext);
        mBrightnessLayout.setId(mBrightnessLayoutId);
        mBrightnessLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams brParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        brParams.addRule(BELOW, BUTTONS_LAYOUT_ID);
        brParams.setMargins(LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN);
        mBrightnessLayout.setLayoutParams(brParams);

        // Brightness Start Icon
        mBrightnessStart = new View(mContext);
        LayoutParams brStartParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        brStartParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        brStartParams.setMarginStart(BAR_ICON_MARGIN);
        mBrightnessStart.setLayoutParams(brStartParams);
        mBrightnessStart.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_brightness_bar_low));
        mBrightnessStart.setId(mBrightnessStartId);
        mBrightnessLayout.addView(mBrightnessStart);

        // Brightness End Icon
        mBrightnessEnd = new View(mContext);
        LayoutParams brEndParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        brEndParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        brEndParams.setMarginEnd(BAR_ICON_MARGIN);
        mBrightnessEnd.setLayoutParams(brEndParams);
        mBrightnessEnd.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_brightness_bar_high));
        mBrightnessEnd.setId(mBrightnessEndId);
        mBrightnessLayout.addView(mBrightnessEnd);

        // Brightness TextView
        mBrightnessText = new TextView(mContext);
        mBrightnessText.setText(R.string.brightness);
        mBrightnessText.setBackgroundColor(Color.TRANSPARENT);
        mBrightnessText.setTextColor(ContextCompat.getColor(mContext, R.color.divider));
        LayoutParams bTextParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        bTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mBrightnessText.setLayoutParams(bTextParams);
        mBrightnessLayout.addView(mBrightnessText);

        // Brightness SeekBar
        mBrightnessBar = new SeekBar(mContext);
        LayoutParams barParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        barParams.setMargins(ICON_MARGIN, ICON_MARGIN, ICON_MARGIN, ICON_MARGIN);
        barParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        barParams.addRule(RelativeLayout.BELOW, mBrightnessStartId);
        barParams.addRule(RelativeLayout.BELOW, mBrightnessEndId);
        mBrightnessBar.setLayoutParams(barParams);
        mBrightnessLayout.addView(mBrightnessBar);

        int brightnessValue = Settings.System.getInt(
                mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        mBrightnessBar.setMax(255);
        mBrightnessBar.setProgress(brightnessValue);

        mSettingsLayout.addView(mBrightnessLayout);
    }

    private void initRingtoneLayout() {
        // Ringtone Layout
        mRingtoneLayout = new RelativeLayout(mContext);
        mRingtoneLayout.setId(mRingtoneLayoutId);
        mRingtoneLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams rParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        rParams.addRule(BELOW, mBrightnessLayoutId);
        rParams.setMargins(LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN);
        mRingtoneLayout.setLayoutParams(rParams);

        // Ringtone Start Icon
        mRingtoneStart = new View(mContext);
        LayoutParams rStartParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        rStartParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        rStartParams.setMarginStart(BAR_ICON_MARGIN);
        mRingtoneStart.setLayoutParams(rStartParams);
        mRingtoneStart.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off_bar));
        mRingtoneStart.setId(mRingtoneStartId);
        mRingtoneLayout.addView(mRingtoneStart);

        // Ringtone End Icon
        mRingtoneEnd = new View(mContext);
        LayoutParams rEndParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        rEndParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        rEndParams.setMarginEnd(BAR_ICON_MARGIN);
        mRingtoneEnd.setLayoutParams(rEndParams);
        mRingtoneEnd.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up_bar));
        mRingtoneEnd.setId(mRingtoneEndId);
        mRingtoneLayout.addView(mRingtoneEnd);

        // Ringtone TextView
        mRingtoneText = new TextView(mContext);
        mRingtoneText.setText(R.string.ringtone);
        mRingtoneText.setBackgroundColor(Color.TRANSPARENT);
        mRingtoneText.setTextColor(ContextCompat.getColor(mContext, R.color.divider));
        LayoutParams rTextParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mRingtoneText.setLayoutParams(rTextParams);
        mRingtoneLayout.addView(mRingtoneText);

        // Ringtone SeekBar
        mRingtoneBar = new SeekBar(mContext);
        LayoutParams barParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        barParams.setMargins(ICON_MARGIN, ICON_MARGIN, ICON_MARGIN, ICON_MARGIN);
        barParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        barParams.addRule(RelativeLayout.BELOW, mRingtoneStartId);
        barParams.addRule(RelativeLayout.BELOW, mRingtoneEndId);
        mRingtoneBar.setLayoutParams(barParams);
        mRingtoneLayout.addView(mRingtoneBar);

        // Init Ringtone progress
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mRingtoneBar.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_RING));
        mRingtoneBar.setProgress(audio.getStreamVolume(AudioManager.STREAM_RING));

        mSettingsLayout.addView(mRingtoneLayout);
    }

    private void initModesLayout() {
        // Modes Layout
        mModesLayout = new RelativeLayout(mContext);
        mModesLayout.setId(MODES_LAYOUT_ID);
        mModesLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mParams.addRule(BELOW, mRingtoneLayoutId);
        mParams.setMargins(LAYOUT_MARGIN, 0, LAYOUT_MARGIN, 0);
        mModesLayout.setLayoutParams(mParams);

        mModesGrid = new GridView(mContext);
        LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                GRID_HEIGHT);
        mModesGrid.setBackgroundColor(Color.TRANSPARENT);
        gParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        gParams.setMargins(GRID_MARGIN_START_END, 0, ICON_MARGIN, GRID_MARGIN_START_END);
        mModesGrid.setLayoutParams(gParams);
        mModesGrid.setId(MODES_GRID_ID);
        mModesLayout.addView(mModesGrid);
        mModesGrid.setNumColumns(MODES_GRID_NUM_COLUMNS);
        mModesGrid.setVerticalSpacing(MODES_GRID_VERTICAL_SPACE);
        mModesGrid.setHorizontalSpacing(MODES_GRID_HORIZONTAL_SPACE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Modes Adapter
                List<Mode> modes = getModes();
                List<String> modeNames = new ArrayList<String >();
                for (Mode m:modes) {
                    modeNames.add(m.getName());
                }
                final ModesAdapter adapter = new ModesAdapter(mContext, modes);
                mModesGrid.setAdapter(adapter);

                // Grid Item listener
                mModesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (adapter != null) {
                            adapter.onItemSelected(position);
                        }
                    }
                });
            }
        }, 2000);

        mSettingsLayout.addView(mModesLayout);
    }

    // Init Status Bar Layout
    private void initIconLayout() {
        LayoutParams iparam = new LayoutParams(LayoutParams.MATCH_PARENT, BAR_HEIGHT);
        iparam.addRule(RelativeLayout.BELOW, mSettingsLayoutId);
        mIconLayout = new RelativeLayout(mContext);
        mIconLayout.setLayoutParams(iparam);
        mIconLayout.setId(mIconLayoutId);
        mIconLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.status_bar_shape));


        // Battery Level
        mBatteryView = new View(mContext);
        mBatteryView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                mBatteryLevel));
        mBatteryView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.battery_view_green));
        mIconLayout.addView(mBatteryView);

        // Carrier
        mCarrier = new TextView(mContext);
        mCarrier.setText(R.string.carrier);
        LayoutParams cParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        cParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        cParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        mCarrier.setLayoutParams(cParams);
        cParams.setMarginStart(ICON_MARGIN_END);
        mCarrier.setTextColor(ContextCompat.getColor(mContext, R.color.textColor));
        mCarrier.setBackgroundColor(Color.TRANSPARENT);
        mIconLayout.addView(mCarrier);

        // Clock
        mClockText = new TextView(mContext);
        mClockText.setText("12:33");
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mClockText.setLayoutParams(params);
        mClockText.setBackgroundColor(Color.TRANSPARENT);
        mClockText.setTextColor(ContextCompat.getColor(mContext, R.color.textColor));
        mClockText.setTypeface(mClockText.getTypeface(), Typeface.BOLD);
        mClockText.setId(mClockId);
        mIconLayout.addView(mClockText);


        // Ringtone
        mRingtoneIcon = new View(mContext);
        LayoutParams rParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        rParams.setMarginEnd(ICON_MARGIN_END);
        mRingtoneIcon.setLayoutParams(rParams);
        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
        mRingtoneIcon.setId(mRingtoneIconId);
        mIconLayout.addView(mRingtoneIcon);

        // Wifi
        mWifiIcon = new View(mContext);
        LayoutParams wParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        wParams.addRule(RelativeLayout.START_OF, mRingtoneIconId);
        wParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        wParams.setMarginEnd(ICON_MARGIN);
        mWifiIcon.setLayoutParams(wParams);
        mWifiIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi));
        mWifiIcon.setId(mWifiIconId);
        mIconLayout.addView(mWifiIcon);

        // Bluetooth
        mBluetoothIcon = new View(mContext);
        LayoutParams bParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        bParams.addRule(RelativeLayout.START_OF, mWifiIconId);
        bParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        bParams.setMarginEnd(ICON_MARGIN);
        mBluetoothIcon.setLayoutParams(bParams);
        mBluetoothIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth));
        mBluetoothIcon.setId(mBluetoothIconId);
        mIconLayout.addView(mBluetoothIcon);

        // Mobile Data
        mMobileDataIcon = new View(mContext);
        LayoutParams mParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        mParams.addRule(RelativeLayout.START_OF, mBluetoothIconId);
        mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mParams.setMarginEnd(ICON_MARGIN);
        mMobileDataIcon.setLayoutParams(mParams);
        mMobileDataIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_mobile_data));
        mMobileDataIcon.setId(mMobileDataIconId);
        mIconLayout.addView(mMobileDataIcon);

        // Screen Rotation
        mOrientationIcon = new View(mContext);
        LayoutParams oParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        oParams.addRule(RelativeLayout.START_OF, mMobileDataIconId);
        oParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        oParams.setMarginEnd(ICON_MARGIN);
        mOrientationIcon.setLayoutParams(oParams);
        mOrientationIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_screen_lock_rotation));
        mOrientationIcon.setId(mOrientationIconId);
        mIconLayout.addView(mOrientationIcon);

        // Location
        mLocationIcon = new View(mContext);
        LayoutParams lParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        lParams.addRule(RelativeLayout.START_OF, mOrientationIconId);
        lParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        lParams.setMarginEnd(ICON_MARGIN);
        mLocationIcon.setLayoutParams(lParams);
        mLocationIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_location));
        mLocationIcon.setId(mLocationIconId);
        mIconLayout.addView(mLocationIcon);

        // Nfc
        mNfcIcon = new View(mContext);
        LayoutParams nParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        nParams.addRule(RelativeLayout.START_OF, mLocationIconId);
        nParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        nParams.setMarginEnd(ICON_MARGIN);
        mNfcIcon.setLayoutParams(nParams);
        mNfcIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_nfc));
        mNfcIcon.setId(mNfcIconId);
        mIconLayout.addView(mNfcIcon);

        // Airplane Mode
        mAirplaneIcon = new View(mContext);
        LayoutParams aParams = new LayoutParams(ICON_SIZE, ICON_SIZE);
        aParams.addRule(RelativeLayout.START_OF, mNfcIconId);
        aParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        aParams.setMarginEnd(ICON_MARGIN);
        mAirplaneIcon.setLayoutParams(aParams);
        mAirplaneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_airplanemode));
        mAirplaneIcon.setId(mAirplaneIconId);
        mIconLayout.addView(mAirplaneIcon);

        // Initial States - all disabled
        mWifiIcon.setVisibility(GONE);
        mMobileDataIcon.setVisibility(GONE);
        mBluetoothIcon.setVisibility(GONE);
        mOrientationIcon.setVisibility(GONE);
        mWifiIcon.setVisibility(GONE);
        mLocationIcon.setVisibility(GONE);
        mNfcIcon.setVisibility(GONE);
        mAirplaneIcon.setVisibility(GONE);
    }

    /**
     * Touch Events
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LayoutParams par = (LayoutParams)
                        mSettingsLayout.getLayoutParams();
                mLastTopMargin = par.topMargin;
                mStartPoint = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                LayoutParams params = (LayoutParams)
                        mSettingsLayout.getLayoutParams();
                int margin = params.topMargin;

                // Expand
                if (margin >= SETTINGS_TOP_MARGIN_COLLAPSED / 2) {
                    mOffset = 0;
                    expandStatusBar();
                } else if (margin <= SETTINGS_TOP_MARGIN_COLLAPSED / 2) {
                    // Collapse
                    mOffset = MIN_OFFSET;
                    collapseStatusBar();
                }

                // Reset values
                mStartPoint = 0;
                mOffset = 0;

                break;
            case MotionEvent.ACTION_MOVE:
                mOffset = Math.round(event.getY() - mStartPoint);
                // Check Offset limits
                if (mOffset < MIN_OFFSET) {
                    mOffset = MIN_OFFSET;
                } else if (mOffset >= MAX_OFFSET) {
                    mOffset = MAX_OFFSET;
                }
                updateLayoutOffset(mOffset);
                break;
        }

        return true;
    }

    /**
     * Update StatusBarLayout - Expand/Collapse
     *
     * @param offset
     */
    private void updateLayoutOffset(int offset) {
        LayoutParams params = (LayoutParams)
                mSettingsLayout.getLayoutParams();

        int topMargin = mLastTopMargin;
        topMargin += offset;

        if (topMargin <= SETTINGS_TOP_MARGIN_COLLAPSED) {
            topMargin = MIN_OFFSET;
        } else if (topMargin >= 0) {
            topMargin = 0;
        }
        params.topMargin = topMargin;
        mSettingsLayout.setLayoutParams(params);
    }

    /**
     * Expand Status Bar and show Settings.
     */
    private void expandStatusBar() {
        LayoutParams params = (LayoutParams)
                mSettingsLayout.getLayoutParams();
        params.topMargin = 0;
        mSettingsLayout.setLayoutParams(params);
    }

    /**
     * Collapse Status bar and hide Settings.
     */
    private void collapseStatusBar() {
        LayoutParams params = (LayoutParams)
                mSettingsLayout.getLayoutParams();
        params.topMargin = SETTINGS_TOP_MARGIN_COLLAPSED;
        mSettingsLayout.setLayoutParams(params);
    }

    /**
     * Button actions
     */

    private void initListeners() {
        mWifiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifi = !mWifi;
                setWifi(mWifi);
            }
        });

        mMobileDataButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobileData = !mMobileData;
                if (mMobileData) {
                    mMobileDataIcon.setVisibility(VISIBLE);
                    mMobileDataButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_mobile_data));
                } else {
                    mMobileDataIcon.setVisibility(GONE);
                    mMobileDataButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_mobile_data_disabled));
                }
            }
        });

        mRingtoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRingtone += 1;
                if (mRingtone > VIBRATE) {
                    mRingtone = ON;
                }

                mIconLayout.removeView(mRingtoneIcon);
                switch (mRingtone) {
                    case ON:
                        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                        break;
                    case OFF:
                        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                        break;
                    case VIBRATE:
                        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_vibrate));
                        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_vibrate));
                        break;
                    default:
                        break;
                }
                mIconLayout.addView(mRingtoneIcon);
            }
        });

        mBluetoothButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetooth =! mBluetooth;
                setBluetooth(mBluetooth);
            }
        });

        mOrientationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrientation =! mOrientation;
                if (mOrientation) {
                    mOrientationIcon.setVisibility(View.VISIBLE);
                    mOrientationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_screen_lock_rotation));
                } else {
                    mOrientationIcon.setVisibility(View.GONE);
                    mOrientationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_screen_lock_rotation_disabled));
                }
            }
        });

        mLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation =! mLocation;
                if (mLocation) {
                    mLocationIcon.setVisibility(View.VISIBLE);
                    mLocationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_location));
                } else {
                    mLocationIcon.setVisibility(View.GONE);
                    mLocationButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_location_disabled));
                }
            }
        });

        mNfcButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNfc =! mNfc;
                if (mNfc) {
                    mNfcIcon.setVisibility(View.VISIBLE);
                    mNfcButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_nfc));
                } else {
                    mNfcIcon.setVisibility(View.GONE);
                    mNfcButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_nfc_disabled));
                }
            }
        });

        mAirplaneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAirplane =! mAirplane;
                if (mAirplane) {
                    mAirplaneIcon.setVisibility(View.VISIBLE);
                    mAirplaneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_airplanemode));
                } else {
                    mAirplaneIcon.setVisibility(View.GONE);
                    mAirplaneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_airplanemode_disabled));
                }
            }
        });

        mRingtoneBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, " Ringtone - onProgressChanged : " + progress + ", fromUser : " + fromUser);

                switch (progress) {
                    case 0:
                        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                    default:
                        mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                        mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                        break;
                }

                if (fromUser) {
                    AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    try {
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Failed set stream volume - Do not disturb mode active",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(progress, fromUser);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public void setBatteryLevel(int level) {
        Log.d(TAG, "setBatteryLevel() " + level);

        mBatteryLevel = level;
        mIconLayout.removeView(mBatteryView);
        mBatteryView = new View(mContext);
        mBatteryView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                mBatteryLevel));
        if (mBatteryLevel >= 60) {
            mBatteryView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.battery_view_green));
        } else if (mBatteryLevel >= 30) {
            mBatteryView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.battery_view_yellow));
        } else {
            mBatteryView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.battery_view_red));
        }
        mIconLayout.addView(mBatteryView);

        // Brint text and icons on top of battery level
        bringViewsToFront();
    }

    private void setWifi(boolean status) {
        Log.d(TAG, "setWifi() " + status);
        mWifi = status;
        if (mWifi) {
            mWifiIcon.setVisibility(VISIBLE);
            mWifiButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi));
        } else {
            mWifiIcon.setVisibility(GONE);
            mWifiButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi_disabled));
        }
    }

    private void setBluetooth(boolean status) {
        Log.d(TAG, "setBluetooth() " + status);
        mBluetooth = status;
        if (mBluetooth) {
            mBluetoothIcon.setVisibility(View.VISIBLE);
            mBluetoothButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth));
        } else {
            mBluetoothIcon.setVisibility(View.GONE);
            mBluetoothButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth_disabled));
        }
    }


    private void setRingtone(int type) {
        Log.d(TAG, "setRingtone() " + type);
        switch (type) {
            case ON:
                mRingtoneBar.setProgress(7);
                mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_up));
                break;
            case OFF:
                mRingtoneBar.setProgress(0);
                mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_off));
                break;
            case VIBRATE:
                mRingtoneBar.setProgress(0);
                mRingtoneIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_vibrate));
                mRingtoneButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_vibrate));
                break;
            default:
                break;
        }
    }

    private void setBrightness(int progress, boolean fromUser) {
        Log.d(TAG, "setBrightness() " + progress + "fromUser: " + fromUser);
        mBrightnessBar.setProgress(progress);

        if (fromUser) {
            // TODO - Add System Permission
            try {
                Settings.System.putInt(
                        mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        progress
                );
            } catch (Exception e) {
                Toast.makeText(mContext, "Failed set Brightness - No Permission",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bringViewsToFront() {
        mCarrier.bringToFront();
        mClockText.bringToFront();
        mWifiIcon.bringToFront();
        mRingtoneIcon.bringToFront();
        mBluetoothIcon.bringToFront();
    }


    // Mandatory Constructors
    public NdroidStatusBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NdroidStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NdroidStatusBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Observer for Volume Changes
    public class RingtoneVolumeObserver extends ContentObserver {
        Context context;

        public RingtoneVolumeObserver(Context c, Handler handler) {
            super(handler);
            context=c;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "onChange() Ringtone");
            AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mRingtoneBar.setProgress(audio.getStreamVolume(AudioManager.STREAM_RING));
        }
    }

    /**
     * Modes
     */
    private List<Mode> getModes() {

        String PROVIDER_NAME = "com.example.octav.proiect";
        String URL = "content://" + PROVIDER_NAME + "/modes";

        Uri modes = Uri.parse(URL);

        Cursor cursor = mContext.getContentResolver().query(modes, null, null, null, null);
        List<Mode> modeList = new ArrayList<Mode>();
        if (cursor != null) {
            Log.d(TAG, "Cursor count " +cursor.getCount());
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                boolean wifi = cursor.getInt(4) > 0;
                String ringtone = cursor.getString(5);
                String brightness = cursor.getString(8);
                String bluetooth = cursor.getString(9);

                // Ringtone
                int ring = 0;
                if (ringtone.equals("Mute"))
                    ring = OFF;
                if (ringtone.equals("Normal"))
                    ring = ON;
                if (ringtone.equals("Vibrate"))
                    ring = VIBRATE;

                // Brightness
                int bright = 0;
                if (brightness.equals("Low")) {
                    bright = 0;
                } else if (brightness.equals("Medium")) {
                    bright = 127;
                } else if (brightness.equals("High")) {
                    bright = 128;
                }

                // Bluetooth
                boolean bt = false;
                if (bluetooth.equals("Off")) {
                    bt = false;
                } else if (bluetooth.equals("On")) {
                    bt = true;
                }

                Mode m = new Mode(id, name, false, wifi, ring, bright, bt);
                Log.d(TAG, "Mode " + m);
                modeList.add(m);
                cursor.moveToNext();
            }
        } else {
            Log.e(TAG, "Cursor is null");
        }
        return modeList;
    }

    private class Mode {
        private int id;
        private String name;
        private boolean selected;
        boolean wifi;
        int ringtone;
        int brightness;
        boolean bluetooth;

        public Mode(int id, String name, boolean selected, boolean wifi, int ringtone, int brightness, boolean bluetooth) {
            this.id = id;
            this.name = name;
            this.selected = selected;
            this.wifi = wifi;
            this.ringtone = ringtone;
            this.brightness = brightness;
            this.bluetooth = bluetooth;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isWifi() {
            return wifi;
        }

        public void setWifi(boolean wifi) {
            this.wifi = wifi;
        }

        public int getRingtone() {
            return ringtone;
        }

        public void setRingtone(int ringtone) {
            this.ringtone = ringtone;
        }

        public int getBrightness() {
            return brightness;
        }

        public void setBrightness(int brightness) {
            this.brightness = brightness;
        }

        public boolean isBluetooth() {
            return bluetooth;
        }

        public void setBluetooth(boolean bluetooth) {
            this.bluetooth = bluetooth;
        }

        @Override
        public String toString() {
            return "Mode{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", selected=" + selected +
                    ", wifi=" + wifi +
                    ", ringtone='" + ringtone + '\'' +
                    ", brightness='" + brightness + '\'' +
                    ", bluetooth='" + bluetooth + '\'' +
                    '}';
        }
    }

    private void setMode(Mode mode) {
        Log.d(TAG, "setMode() : " + mode);
        setWifi(mode.isWifi());
        setBluetooth(mode.isBluetooth());
        setRingtone(mode.getRingtone());
        setBrightness(mode.getBrightness(), false);
    }

    public class ModesAdapter extends BaseAdapter {

        private List<Mode> mModes;
        private Context mContext;

        public ModesAdapter(Context c, List<Mode> data) {
            mContext = c;
            mModes = data;
        }

        public int getCount() {
            return mModes.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            FileHolder holder = new FileHolder();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.mode_grid_item, null);
                holder.modeTextView = (TextView) v.findViewById(R.id.modeTextView);
                v.setTag(holder);
            } else {
                holder = (FileHolder) v.getTag();
            }

            Mode mode = mModes.get(position);
            holder.modeTextView.setText(mode.getName());
            holder.modeTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            if (mode.isSelected()) {
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.mode_background_selected));
            } else {
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.mode_background));
            }
            return v;
        }

        // Called when an item is selected
        public void onItemSelected(int position) {
            Log.d(TAG, "onItemSelected() " + position);
            // Update mode list and set mode selected
            for (int i = 0; i < mModes.size(); i++) {
                Mode mode = mModes.get(i);
                if (i == position) {
                    mode.setSelected(true);
                    setMode(mode);
                } else {
                    mode.setSelected(false);
                }
            }

            // Notify Personal Assistant app
            final Intent intent = new Intent();
            intent.setAction("com.ndroid.ndroidstatusbar");
            intent.putExtra("ModeId", mModes.get(position).getId());
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            mContext.sendBroadcast(intent);

            // Refresh Grid to apply background to selected item
            notifyDataSetChanged();
        }

        public class FileHolder {
            TextView modeTextView;
        }

    }

}
