package com.pfiev.englishcontest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.pfiev.englishcontest.databinding.FragmentChooserBinding;
import com.pfiev.englishcontest.setup.FacebookSignInActivity;
import com.pfiev.englishcontest.setup.TwitterSignInActivity;

public class ChooserFragment extends BaseFragment {
    /*    private static final int[] NAV_ACTIONS = new int[]{
                R.id.action_google,
                R.id.action_facebook,
                R.id.action_emailpassword,
                R.id.action_passwordless,
                R.id.action_phoneauth,
                R.id.action_anonymousauth,
                R.id.action_firebaseui,
                R.id.action_customauth,
                R.id.action_genericidp,
                R.id.action_mfa,
        };*/
    private FragmentChooserBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentChooserBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.signInFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(ChooserFragment.this)
//                        .navigate(R.id.action_chooser_facebook);
                Intent intent = new Intent(getActivity(), FacebookSignInActivity.class);
                startActivity(intent);
            }
        });
        mBinding.signInTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(), TwitterSignInActivity.class));
            }
        });

    }
}
