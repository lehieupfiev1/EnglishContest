package com.pfiev.englishcontest.ui.experimental;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalSettingsBinding;
import com.pfiev.englishcontest.service.SoundBackgroundService;
import com.pfiev.englishcontest.utils.AppConfig;

import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "Settings Fragment";
    private FragmentExperimentalSettingsBinding binding;
    private AppConfig appConfig;
    private Boolean isSoundOn = false;
    private Boolean isSoundEffectOn = false;

    public SettingsFragment() {
// Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
// TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExperimentalSettingsBinding.inflate(inflater, container, false);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        Log.d("Settings Fragment xdpi", String.valueOf(displayMetrics.widthPixels/displayMetrics.density));
        this.appConfig = new AppConfig(getActivity());
        this.bindingBackButton();
        this.createLanguageSpinner();
        this.bindingSoundCheckBox();
        this.bindingSoundEffectCheckBox();
        return binding.getRoot();
    }

    private void bindingBackButton() {
        // Back to main when touch return button
        binding.experimentalSettingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, MainFragment.newInstance())
                        .commitNow();
            }
        });
    }

    private void createLanguageSpinner() {
        // Change language when select on dropdown list (spinner)
        String[] allLocale = appConfig.getAllLocale();
        String[] allLocaleShow = appConfig.getAllLocaleShow();
        String displayLocale = appConfig.getDisplayLanguage();

        // Apply the adapter to the spinner
//        Toast.makeText(getContext(), "Default " + Locale.getDefault().getDisplayLanguage(), Toast.LENGTH_LONG).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_items_experimental_language,
                allLocaleShow);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.experimentalSettingsLanguageSpinner.setAdapter(adapter);
        // Selection and set listener
        binding.experimentalSettingsLanguageSpinner
                .setSelection(Arrays.asList(allLocale).indexOf(displayLocale));

        binding.experimentalSettingsLanguageSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(TAG, " selected " + allLocale[i]);
                        appConfig.setDisplayLanguage(allLocale[i]);
                        Locale locale = new Locale(allLocale[i]);
                        Locale.setDefault(locale);
                        Log.d(TAG, " selected __ " + locale.getDisplayLanguage());
                        Configuration config = new Configuration();
                        config.setLocale(locale);
                        getActivity().onConfigurationChanged(config);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
    }

    private void bindingSoundCheckBox() {
        LottieAnimationView lottie = binding.experimentalSettingsSoundCheckbox;
        CheckboxAnimation checkboxAnimation = new CheckboxAnimation();
        checkboxAnimation.setLottie(lottie);
        if (appConfig.isSoundOn()) {
            checkboxAnimation.on();
            this.isSoundOn = true;
        }
        lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSoundOn) {
                    isSoundOn = false;
                    appConfig.setSoundOn(false);
                    getActivity().stopService(new Intent(getActivity().getApplicationContext(), SoundBackgroundService.class));
                    checkboxAnimation.off();
                } else {
                    isSoundOn = true;
                    appConfig.setSoundOn(true);
                    checkboxAnimation.on();
                    getActivity().startService(new Intent(getActivity().getApplicationContext(), SoundBackgroundService.class));
                }
            }
        });
    }

    private void bindingSoundEffectCheckBox() {
        LottieAnimationView lottie = binding.experimentalSettingsSoundEffectCheckbox;
        CheckboxAnimation checkboxAnimation = new CheckboxAnimation();
        checkboxAnimation.setLottie(lottie);
        if (appConfig.isBackgroundMusicEffectOn()) {
            checkboxAnimation.on();
            this.isSoundEffectOn = true;
        }
        lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSoundEffectOn) {
                    isSoundEffectOn = false;
                    appConfig.setBackgroundMusicEffectOn(false);
                    checkboxAnimation.off();
                } else {
                    isSoundEffectOn = true;
                    appConfig.setBackgroundMusicEffectOn(true);
                    checkboxAnimation.on();
                }
            }
        });
    }

    static class CheckboxAnimation {
        private LottieAnimationView lottie;

        public CheckboxAnimation() {
        }

        public void setLottie(LottieAnimationView lottie) {
            this.lottie = lottie;
        }

        public void on() {
            lottie.setMinFrame(0);
            lottie.setMaxFrame(25);
            lottie.playAnimation();
        }

        public void off() {
            lottie.setMinFrame(25);
            lottie.setMaxFrame(45);
            lottie.playAnimation();
        }
    }

}