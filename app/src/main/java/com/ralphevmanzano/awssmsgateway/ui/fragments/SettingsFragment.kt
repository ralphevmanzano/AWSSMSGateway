package com.ralphevmanzano.awssmsgateway.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import com.ralphevmanzano.awssmsgateway.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
    preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    initSummary(preferenceScreen)
  }

  override fun onStop() {
    super.onStop()
    preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(null)
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    updatePreference(findPreference(key!!))
  }

  private fun updatePreference(preference: Preference?) {
    if (preference is EditTextPreference) {
      preference.summary = preference.text
    }
  }

  private fun initSummary(p: Preference) {
    if (p is PreferenceGroup) {
      for (i in 0 until p.preferenceCount) {
        initSummary(p.getPreference(i))
      }
    } else {
      updatePreference(p)
    }
  }

}
