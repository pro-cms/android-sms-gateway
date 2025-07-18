package com.server.zepsonconnect.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.server.zepsonconnect.R

class MessagesSettingsFragment : BasePreferenceFragment() {

    override fun onResume() {
        super.onResume()

        onPreferenceChanged.onSharedPreferenceChanged(
            preferenceManager.sharedPreferences,
            "messages.limit_period"
        )
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(
            onPreferenceChanged
        )
    }

    override fun onPause() {
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
            onPreferenceChanged
        )

        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.messages_preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference.key == "messages.limit_value"
            || preference.key == "messages.log_lifetime_days"
            || preference.key == "messages.send_interval_min"
            || preference.key == "messages.send_interval_max"
        ) {
            (preference as EditTextPreference).setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
                it.setSelectAllOnFocus(true)
                it.selectAll()
            }
        }

        super.onDisplayPreferenceDialog(preference)
    }

    private val onPreferenceChanged =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "messages.limit_period") {
                findPreference<EditTextPreference>("messages.limit_value")?.isEnabled =
                    sharedPreferences?.getString(key, "Disabled") != "Disabled"
            }
        }
}