package com.diwangkoro.awashilang;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.Window;

/**
 * Created by user on 4/22/2016.
 */
public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences(getPackageName().toString(), Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.prefs);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        initSummary(getPreferenceScreen());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {

        EditTextPreference editTextPref = (EditTextPreference) p;
        /*if (p.getSummary()=="")
        {
            setSummaryDefault(p);
        }
        else {*/
            p.setSummary(editTextPref.getText());
        //}


    }

    private void setSummaryDefault(Preference p){
        String pKey = p.getKey().toString();
        String sum="";
        switch (pKey){
            case "Track":
                sum = "track";
                break;
            case "Alert":
                sum = "alert";
                break;
            case "Lock":
                sum = "lock";
                break;
            case "Inbox":
                sum = "inbox";
                break;
            case "Sent":
                sum = "sent";
                break;
            case "Capture":
                sum ="capture";
                break;
            case "Battery":
                sum = "battery";
                break;
            case "Incoming":
                sum = "incoming";
                break;
            case "Outgoing":
                sum = "outgoing";
                break;
            case "Missed":
                sum = "missed";
                break;
            case "Hide":
                sum = "hide";
                break;

        }
        p.setSummary(sum);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        boolean ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
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
}
