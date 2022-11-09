package com.pfiev.englishcontest.ui.experimental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalAboutBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    // Binding Variable
    private FragmentExperimentalAboutBinding binding;

    public AboutFragment() {
        // Required empty public constructor
    }


    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExperimentalAboutBinding.inflate(inflater, container, false);
        this.bindingBackButton();
        return binding.getRoot();
    }

    private void bindingBackButton() {
        // Back to main when touch return button
        binding.experimentalAboutBackBtn.setOnClickListener(view -> getParentFragmentManager().beginTransaction()
                .replace(R.id.experimental_fullscreen_content, MainFragment.newInstance())
                .commitNow());
    }
}