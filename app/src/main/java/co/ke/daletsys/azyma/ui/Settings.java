package co.ke.daletsys.azyma.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.prefs.PreferenceChangeEvent;

import co.ke.daletsys.azyma.R;

public class Settings extends AppCompatActivity {
    SharedPreferences.Editor pEditor;
    public SharedPreferences pSettings;
    PreferenceChangeEvent preferenceChangeListener;
    boolean aTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pSettings = getApplicationContext().getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        aTheme =  pSettings.getBoolean("aTheme", false);

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences.Editor pEditor;
            SharedPreferences pSettings = getContext().getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
            boolean aTheme = pSettings.getBoolean("gActive", false);

            boolean theme = ((SwitchPreferenceCompat) findPreference("theme")).isChecked();
            String signature = ((EditTextPreference) findPreference("signature")).getText();

            if(theme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                pEditor = pSettings.edit();
                pEditor.putBoolean("aTheme", true);
                pEditor.commit();
                pEditor.apply();

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                pEditor = pSettings.edit();
                pEditor.putBoolean("aTheme", false);
                pEditor.commit();
                pEditor.apply();
            }
        }
    }
}