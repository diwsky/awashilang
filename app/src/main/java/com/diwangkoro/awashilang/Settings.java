package com.diwangkoro.awashilang;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.diwangkoro.awashilang.Alerting.M_RecAlarmCharger;
import com.diwangkoro.awashilang.Alerting.Service_Alarm;
import com.diwangkoro.awashilang.Monitoring.M_CallLogs;
import com.diwangkoro.awashilang.Monitoring.M_capture;
import com.diwangkoro.awashilang.Monitoring.M_message;
import com.diwangkoro.awashilang.Tracking.MyLocation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 4/17/2016.
 */
public class Settings extends Activity implements OnClickListener {

    Context context;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
    private String curDate = sdf.format(new Date());

    Switch sDeviceManager, sHidden, sAlarmCharger,sService;
    Button changePass,changeAttempt,changeHidden,changeLog,changeSMS,TEKANAKU,changeAlarm;
    CheckBox checkBox;

    DrawerLayout drawerLayout;
    LinearLayout layDrawerSamping, layMnus;

    SharedPreferences mPref;
    TextView testtext,usernametext;
    ImageView tvNav;
    StringBuffer emailString = new StringBuffer();

    public M_RecAlarmCharger m_recAlarmCharger;
    public static ComponentName compName;
    static final int RESULT_ENABLE = 1;
    public static DevicePolicyManager deviceManager;
    public static ActivityManager activityManager;
    static String curPhoneNum="";

    String LOCK_PASS, LAUNCHER_NUMBER, coordinat="";
    boolean ICON_STATUS, ACTIVE_ALARM_CHARGE, EMAIL_RECEIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        /* ngetes testext
        M_inbox2 m_inbox2 = new M_inbox2();
        testtext = (TextView) findViewById(R.id.testext);
        testtext.setText(m_inbox2.getSMS(this));
        */
        usernametext = (TextView)findViewById(R.id.usernametext);
        usernametext.setText("E-mail pengguna: \n"+mPref.getString("EMAIL",""));

        /********Keep screen ON************/
//        getWindow().addFlags( WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        /*********************************/
        if (!(mPref.getString("LAUNCHER_NUMBER", "").length()>0)) {
            mPref.edit().putString("LAUNCHER_NUMBER","12345").apply();
        }

        AddDeviceAdmin();
        checkServices();
        //checkPhoneNumber();

        /*****INISIALISASI PAIRING BUTTON & SWITCH ******/
        sService = (Switch)findViewById(R.id.sService);
        sDeviceManager = (Switch)findViewById(R.id.sDeviceManager);
        sHidden = (Switch) findViewById(R.id.sHidden);
        sAlarmCharger = (Switch) findViewById(R.id.sAlarmCharger);
        changePass = (Button) findViewById(R.id.setpass);
        changeAttempt = (Button) findViewById(R.id.maxattempt);
        changeHidden = (Button) findViewById(R.id.changeHidden);
        changeLog = (Button) findViewById(R.id.changeLog);
        changeSMS = (Button) findViewById(R.id.changeSMS);
        changeAlarm = (Button) findViewById(R.id.AlarmMessage);
//        TEKANAKU = (Button)findViewById(R.id.serbabisa);

        tvNav = (ImageView) findViewById(R.id.tvNav);
        tvNav.setOnClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        layDrawerSamping = (LinearLayout) findViewById(R.id.drawerKiri);
        layMnus = (LinearLayout) findViewById(R.id.layMenus);
        daftarMenu();
        drawerLayout.closeDrawer(layDrawerSamping);

        /*********Start the service*********/
        if (isMyServiceRunning(Service_General.class)) {
            sService.setChecked(true);
        }
       sService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked) {
                   if (!isMyServiceRunning(Service_General.class)) {
                       Intent intentService = new Intent(Settings.this, Service_General.class);
                       startService(intentService);
                   }
               } else {
                   if (isMyServiceRunning(Service_General.class)) {
                       Intent intentService = new Intent(Settings.this, Service_General.class);
                       //Intent intentService = new Intent(this, Service_SMS.class);
                       stopService(intentService);
                   }
               }
           }
       });

        /**********************************/

        /****SETTING FITUR LOCKING***/
        compName = new ComponentName(this, com.diwangkoro.awashilang.Locking.myAdmin.class);
        deviceManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        LOCK_PASS = mPref.getString("LOCK_PASS", "");
        activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        changePass.setOnClickListener(this);
        changeAttempt.setOnClickListener(this);

        if (!deviceManager.isAdminActive(compName)) {
            // try to become active – must happen here in this activity, to get result
            AddDeviceAdmin();
            sDeviceManager.setChecked(true);
        }

        if (deviceManager.isAdminActive(compName)) {
            sDeviceManager.setChecked(true);
        } else {
            sDeviceManager.setChecked(false);
        }

        //--------Switch Enable Device Manager--------//
        sDeviceManager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!deviceManager.isAdminActive(compName)) {
                        AddDeviceAdmin();
                        sDeviceManager.setChecked(true);
                    }
                } else {
                    deviceManager.removeActiveAdmin(compName);
                    sDeviceManager.setChecked(false);
                }
            }
        });


        /****SETTING FITUR HIDDEN***/
        LAUNCHER_NUMBER = mPref.getString("LAUNCHER_NUMBER","");
        ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);
        if (ICON_STATUS) {
            //switchnya off, iconnya masih keliatan
            sHidden.setChecked(false);
        } else {
            //switchnya on, iconnya udah sembunyi
            sHidden.setChecked(true);
        }

        //--------Switch Hide the apps--------//
        sHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //kalo switchnya di turn on, hideLauncherIcon()
                if (isChecked) {
                    hideLauncherIcon();
                    mPref.edit().putBoolean("ICON_STATUS", false).apply();
                    Toast.makeText(Settings.this, "You hide the application!", Toast.LENGTH_SHORT).show();
                } else {
                    mPref.edit().putBoolean("ICON_STATUS", true).apply();
                    Toast.makeText(Settings.this, "You didn't activate hidden feature", Toast.LENGTH_SHORT).show();
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
            }
        });
        changeHidden.setOnClickListener(this);

        /***SETTING FITUR MONITORING***/
        ACTIVE_ALARM_CHARGE = mPref.getBoolean("ACTIVE_ALARM_CHARGER", false);
        if (ACTIVE_ALARM_CHARGE) {
            sAlarmCharger.setChecked(false);
        }

        //--------Checkbox kirim email------------------//
        checkBox = (CheckBox) findViewById(R.id.emailcheck);
        checkBox.setOnClickListener(this);
        EMAIL_RECEIVE = mPref.getBoolean("EMAIL_RECEIVE",false);
        if (EMAIL_RECEIVE) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        //--------Switch Activate Alarm Charger---------//
        sAlarmCharger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPref.edit().putBoolean("ACTIVE_ALARM_CHARGER", true).apply();
                    ACTIVE_ALARM_CHARGE = mPref.getBoolean("ACTIVE_ALARM_CHARGER", true);
                    Toast.makeText(getApplicationContext(), "You activated Charger Alarm", Toast.LENGTH_SHORT).show();
                    sAlarmCharger.setChecked(true);

                } else {
                    mPref.edit().putBoolean("ACTIVE_ALARM_CHARGER", false).apply();
                    ACTIVE_ALARM_CHARGE = mPref.getBoolean("ACTIVE_ALARM_CHARGER", true);
                    Toast.makeText(getApplicationContext(), "You deactivated Charger Alarm", Toast.LENGTH_SHORT).show();
                    sAlarmCharger.setChecked(false);
                }
            }
        });
        changeLog.setOnClickListener(this);
        changeSMS.setOnClickListener(this);
        changeAlarm.setOnClickListener(this);
//        TEKANAKU.setOnClickListener(this);

//      //---------REGISTERING RECEIVERS -----------//
//              IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        m_recAlarmCharger = new M_RecAlarmCharger();
//        getApplicationContext().registerReceiver(m_recAlarmCharger, filter);
    }



    @Override
    public void onClick(View v) {

        if (v==tvNav) {
            drawerLayout.openDrawer(layDrawerSamping);
        }
        //-------Tombol Ganti Password-------//
        if (v==changePass){
            Log.d("pencet change pass","bener ora ki?");
            boolean active = deviceManager.isAdminActive(compName);
            if (active){
                PopUpChangePass();
            }
        }
        //-------Tombol Jumlah Password Salah-------//
        if (v==changeAttempt) {
            PopUpChangeAttempt();
        }
        //-------Tombol Ubah kode hidden-------//
        if (v==changeHidden) {
            PopUpChangeNumber();
        }
        //-------Tombol Jumlah Call Log---------//
        if (v==changeLog) {
            PopUpChangeLog();
        }
        //-------Tombol Jumlah SMS Log----------//
        if (v==changeSMS) {
            PopUpChangeSMS();
        }

        //-------Tombol Ganti Teks Alarm----------//
        if (v==changeAlarm) {
            PopUpChangeAlert();
        }

//        if (v==TEKANAKU) {
//            //sendAllData();
//            Intent iservice = new Intent(Settings.this, Service_Alarm.class);
//            startService(iservice);
//        }
        if (v==checkBox) {
            if (checkBox.isChecked()) {
                mPref.edit().putBoolean("EMAIL_RECEIVE",true).apply();
                Toast.makeText(context,"Anda mengaktifkan pengiriman via E-mail",Toast.LENGTH_SHORT).show();
            } else {
                mPref.edit().putBoolean("EMAIL_RECEIVE",false).apply();
                Toast.makeText(context,"Anda tidak mengaktifkan pengiriman via E-mail",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void hideLauncherIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Penting!");
        builder.setMessage("Kode hidden anda: " + LAUNCHER_NUMBER);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PackageManager p = getPackageManager();
                ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
                p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                Log.e("Diwang", "OK");
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    public void sendEmail(Context context, String subject, String body) {
        M_capture capture = new M_capture();

        //---------Sending image to email------//
        Mail m = new Mail("awashilang.apps@gmail.com", "diditganteng");
        String[] toArr = {mPref.getString("EMAIL","diwangkoro270@gmail.com")};
        m.setTo(toArr);
        m.setFrom("awashilang.apps@gmail.com");
        m.setSubject(subject);
        m.setBody(body);

        try  {

            m.addAttachment("/sdcard/Awas Hilang!/"+capture.getImageDate()+".png");

            if (m.send()) {
                Toast.makeText(context.getApplicationContext(), "Email was sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "Email was not sent", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ReceiverSMS", "Could not send email", e);
        }
    }

    private void PopUpChangeNumber() {

        final View dialog = LayoutInflater.from(this).inflate(R.layout.h_edit_access_code, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        // set the custom dialog components - text, image and button
        final EditText change_launcher_number = (EditText) dialog.findViewById(R.id.change_launcher_number);
        Button button_change_launcher_number = (Button) dialog.findViewById(R.id.button_change_launcher_number);
        TextView current_launcher_code = (TextView) dialog.findViewById(R.id.info_current_code);
        current_launcher_code.setText("Kode hidden sekarang: " + LAUNCHER_NUMBER);
        button_change_launcher_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLauncherNumber = change_launcher_number.getText().toString();
                if (!newLauncherNumber.equalsIgnoreCase("")) {
                    LAUNCHER_NUMBER = newLauncherNumber;
                    mPref.edit().putString("LAUNCHER_NUMBER", newLauncherNumber).apply();
                }
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();
    }

    private void PopUpChangeAttempt() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.l_change_attempt, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        final EditText editText = (EditText) dialog.findViewById(R.id.newattempt);
        Button button = (Button) dialog.findViewById(R.id.gantiattempt);
        TextView info_current = (TextView) dialog.findViewById(R.id.info_current_attempt);
        info_current.setText("Maksimal percobaan: "+mPref.getInt("ATTEMPT",3));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if (!temp.equalsIgnoreCase("")) {
                    mPref.edit().putInt("ATTEMPT",Integer.parseInt(temp)).apply();
                }
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();

    }

    private void PopUpChangePass() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.l_change_pass, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        // set the custom dialog components - text, image and button
        final EditText editText = (EditText) dialog.findViewById(R.id.newpass);
        Button button = (Button) dialog.findViewById(R.id.gantipass);
        TextView info_current = (TextView) dialog.findViewById(R.id.info_current_pass);
        LOCK_PASS = mPref.getString("LOCK_PASS","");
        info_current.setText("Password sekarang: "+LOCK_PASS);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if (!temp.equalsIgnoreCase("")) {
                    LOCK_PASS = temp;
                    mPref.edit().putString("LOCK_PASS", temp).apply();
                }
                deviceManager.resetPassword(LOCK_PASS, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();
    }

    private void PopUpChangeLog() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.m_edit_jumlah_call, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        final EditText editText = (EditText) dialog.findViewById(R.id.newlog);
        Button button = (Button) dialog.findViewById(R.id.gantilog);
        TextView info_current = (TextView) dialog.findViewById(R.id.info_current_log);
        info_current.setText("Jumlah data panggilan: "+mPref.getInt("SUM_CALL",5));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if (!temp.equalsIgnoreCase("")) {
                    mPref.edit().putInt("SUM_CALL", Integer.parseInt(temp)).apply();
                }
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();
    }

    private void PopUpChangeSMS() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.m_edit_jumlah_sms, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        final EditText editText = (EditText) dialog.findViewById(R.id.newsms);
        Button button = (Button) dialog.findViewById(R.id.gantisms);
        TextView info_current = (TextView) dialog.findViewById(R.id.info_current_sms);
        info_current.setText("Jumlah data SMS: "+mPref.getInt("SUM_SMS",5));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if (!temp.equalsIgnoreCase("")) {
                    mPref.edit().putInt("SUM_SMS", Integer.parseInt(temp)).apply();
                }
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();
    }

    private void PopUpChangeAlert() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.l_change_alarmtext, null);
        final AlertDialog ubahDialog = new AlertDialog.Builder(this).create();
        ubahDialog.setView(dialog);

        final EditText editText = (EditText) dialog.findViewById(R.id.newalert);
        Button button = (Button) dialog.findViewById(R.id.gantialert);
        TextView info_current = (TextView) dialog.findViewById(R.id.info_current_alert);
        info_current.setText("Teks Alerting: "+mPref.getString("ALERT_TEXT","AwasHilang!!!"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if (!temp.equalsIgnoreCase("")) {
                    mPref.edit().putString("ALERT_TEXT", temp).apply();
                }
                ubahDialog.dismiss();
            }
        });
        ubahDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("DeviceAdminSample", "Admin enabled!");
                } else {
                    Log.e("DeviceAdminSample", "Admin enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void daftarMenu() {
        String[] itemMenu = {
                "Home", "Command Setting", "Log Out"};
        int[] itemIcon = {R.drawable.home,
                R.drawable.settings,
                R.drawable.logout};

        for (int i = 0; i < itemMenu.length; i++) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_menusamping, null);
            row.setTag("item_" + i);
            TextView tvTitle = (TextView) row.findViewById(R.id.tvTitle);
            ImageView ivIcon = (ImageView) row.findViewById(R.id.ivIcon);
            tvTitle.setText(itemMenu[i]);
            ivIcon.setImageResource(itemIcon[i]);

            row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] vtagStr = v.getTag().toString().split("_");

                    int vtagInt = Integer.parseInt(vtagStr[1]);

                    switch (vtagInt) {
                        case 0:
                            //home
                            break;
                        case 1:
                            //Command Setting
                            Intent icommand = new Intent(getApplicationContext(),Preferences.class);
                            startActivity(icommand);
                            break;
                        case 2:
                            //logout
                            new AlertDialog.Builder(Settings.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Log Out")
                                    .setMessage("Anda yakin ingin Log Out?")
                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences.Editor edits = mPref.edit();
                                            edits.remove("EMAIL");
                                            edits.remove("LAUNCHER_NUMBER");
                                            edits.commit();

                                            Intent intent = new Intent(Settings.this, LoginPage.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    })
                                    .setNegativeButton("Tidak", null)
                                    .show();
                            break;
                    }
                    drawerLayout.closeDrawer(layDrawerSamping);
                }
            });
            layMnus.addView(row);
        }
    }

    private void checkServices() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fitur GPS tidak aktif");
            builder.setMessage("Silahkan aktifkan fitur GPS smartphone anda");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void AddDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should allow this application to use Locking feature");
        startActivityForResult(intent, Settings.RESULT_ENABLE);
    }

    public void sendAllData() {
        /**
         * Prosedur yang berfungsi untuk mengirimkan data-data smartphone ke email pengguna
         * saat smartphone baru dinyalakan
         */

        String emailUser = mPref.getString("EMAIL","diwangkoro270@gmail.com");
        if (!emailUser.equalsIgnoreCase("")) {

            M_CallLogs mCalLogs = new M_CallLogs();
            M_message mMessage = new M_message();
            M_capture mCapture = new M_capture();
            TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            emailString.append("\n\n==========PHONE NUMBER=========\n");
            curPhoneNum = tMgr.getLine1Number();
            if (curPhoneNum.equalsIgnoreCase("")) {
                curPhoneNum = "Nomor kartu anda belum terdaftar pada smartphone.";}
            emailString.append(curPhoneNum+"\n");
            emailString.append("\n\n===========TRACKING============\n");
            emailString.append(tracking()+"\n");
            emailString.append("\n\n===========INCOMING============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"incoming"));
            emailString.append("\n\n===========OUTGOING============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"outgoing"));
            emailString.append("\n\n============MISSED=============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"missed"));
            emailString.append("\n\n============INBOX==============\n");
            emailString.append(mMessage.getSMStoEmail(getApplicationContext(),"inbox"));
            emailString.append("\n\n============SENT===============\n");
            emailString.append(mMessage.getSMStoEmail(getApplicationContext(),"sent"));
            emailString.append("\n\n===========BATTERY=============\n\n");
            emailString.append(batteryLevel()+"\n");

            mCapture.takeSnapShots(context);
            sendEmail(getApplicationContext(),"AwasHilang! Data smartphone pada "+curDate,String.valueOf(emailString));

        }
    }

    public String batteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null,ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

        return String.valueOf(level);
    }

    public String tracking() {

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                String lat = String.valueOf(location.getLatitude());
                String lng = String.valueOf(location.getLongitude());
                float accuracy = location.getAccuracy();
                coordinat = "maps.google.com/maps?q="+ lat+","+lng+" with accuracy "+accuracy+" meters";
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getApplicationContext(), locationResult);

        if (coordinat.equalsIgnoreCase("")) {
            coordinat = "Lokasi smartphone anda tidak aktif";
        }

        return coordinat;
    }

    public void checkPhoneNumber() {
        //Akuisisi nomor telpon yang digunakan smartphone
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int tempLength = tMgr.getLine1Number().length();
        curPhoneNum = tMgr.getLine1Number().substring(tempLength-4, tempLength);

        //Akuisisi nomor telpon user
        String idPhonenum = mPref.getString("phoneNum","");
        int tempLength2 = idPhonenum.length();
        idPhonenum = idPhonenum.substring(tempLength2-4, tempLength2);

        if (curPhoneNum!=null) {
            Receiver_SMS rSMS = new Receiver_SMS();
            if (curPhoneNum != idPhonenum) {
                rSMS.sendSMS(getApplicationContext(), mPref.getString("emergencyPhone",""), "Your current phone num: "+curPhoneNum);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Keluar")
                .setMessage("Anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
