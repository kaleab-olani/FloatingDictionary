package com.app.kol.fd.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.kol.fd.R;

public class FloatingViewSettingsFragment extends PreferenceFragmentCompat {

    /**
     * FloatingViewSettingsFragmentを生成します。
     *
     * @return FloatingViewSettingsFragment
     */
    public static FloatingViewSettingsFragment newInstance() {
        final FloatingViewSettingsFragment fragment = new FloatingViewSettingsFragment();
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public FloatingViewSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_floatingview, null);
    }
}
