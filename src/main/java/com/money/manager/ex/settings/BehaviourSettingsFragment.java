/*
 * Copyright (C) 2012-2016 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.settings;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.money.manager.ex.Constants;
import com.money.manager.ex.R;
import com.money.manager.ex.common.AmountInputDialog;
import com.money.manager.ex.common.events.AmountEnteredEvent;
import com.money.manager.ex.core.Core;
import com.money.manager.ex.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;
import info.javaperformance.money.Money;

/**
 * Look & feel settings.
 */
public class BehaviourSettingsFragment
    extends PreferenceFragmentCompat {

    private static final String KEY_NOTIFICATION_TIME = "NotificationTime";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity());

        initializeNotificationTime();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // use either setPreferenceScreen(PreferenceScreen) or addPreferencesFromResource(int).

        addPreferencesFromResource(R.xml.settings_behaviour);
    }

    @Override
    public void onStart() {
        super.onStart();

//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

//        EventBus.getDefault().unregister(this);
    }

    // Private

    private void initializeNotificationTime() {
        Preference preference = findPreference(getString(PreferenceConstants.PREF_REPEATING_TRANSACTION_CHECK));
        if (preference == null) return;

        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimePicker();
                return true;
            }
        };
        preference.setOnPreferenceClickListener(listener);
    }

    private void showTimePicker() {
        final BehaviourSettings settings = new BehaviourSettings(getActivity());
        final SimpleDateFormat formatter = new SimpleDateFormat(Constants.TIME_FORMAT);

        RadialTimePickerDialogFragment.OnTimeSetListener timeSetListener = new RadialTimePickerDialogFragment.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                String value = String.format("%02d:%02d", hourOfDay, minute);
                settings.setNotificationTime(value);
            }
        };

        // get time to display (current setting)
        String timeString = settings.getNotificationTime();
        CalendarUtils utils = new CalendarUtils();

        Date currentValue;
        try {
            currentValue = formatter.parse(timeString);
            utils.setTime(currentValue);
        } catch (ParseException ex) {
            // use current time
            currentValue = null;
        }

        int hour = currentValue != null ? utils.getHour() : 8;
        int minute = currentValue != null ? utils.getMinute() : 0;

        RadialTimePickerDialogFragment timePicker = new RadialTimePickerDialogFragment()
            .setOnTimeSetListener(timeSetListener)
            .setStartTime(hour, minute)
            .setThemeDark();
        timePicker.show(getChildFragmentManager(), KEY_NOTIFICATION_TIME);
    }
}