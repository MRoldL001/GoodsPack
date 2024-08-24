package com.mroldl001.goodspack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        RadioGroup themeRadioGroup = view.findViewById(R.id.theme_radio_group);

        // Load saved theme preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String savedTheme = sharedPreferences.getString("theme", "Theme.GoodsPack");
        int selectedId = getRadioButtonId(savedTheme);
        themeRadioGroup.check(selectedId);

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedTheme = getThemeNameFromId(checkedId);
                saveThemePreference(selectedTheme);
                applyTheme();
            }
        });

        return view;
    }

    private int getRadioButtonId(String themeName) {
        if (themeName.equals("Theme.GoodsPack.Shobu")) {
            return R.id.theme_shobu;
        } else if (themeName.equals("Theme.GoodsPack.Momozome")) {
            return R.id.theme_momozome;
        } else if (themeName.equals("Theme.GoodsPack.Dynamic")) {
            return R.id.theme_dynamic;
        } else {
            return R.id.theme_default;
        }
    }

    private String getThemeNameFromId(int radioButtonId) {
        if (radioButtonId == R.id.theme_shobu) {
            return "Theme.GoodsPack.Shobu";
        } else if (radioButtonId == R.id.theme_momozome) {
            return "Theme.GoodsPack.Momozome";
        } else if (radioButtonId == R.id.theme_dynamic) {
            return "Theme.GoodsPack.Dynamic";
        } else {
            return "Theme.GoodsPack";
        }
    }

    private void saveThemePreference(String themeName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", themeName);
        editor.apply();
    }

    private void applyTheme() {
        requireActivity().recreate();
    }
}
