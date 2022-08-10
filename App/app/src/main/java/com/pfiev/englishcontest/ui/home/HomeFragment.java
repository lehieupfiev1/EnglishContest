package com.pfiev.englishcontest.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public static String TAG = "HomeFragment";
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUserId;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Display One-Tap Sign In if user isn't logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.textUserId.setText("User ID :"+currentUser.getDisplayName());
            binding.textUserName.setText("User email :"+currentUser.getDisplayName());
        }
        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signOut();
            }
        });

        String userProfileImageUrl = currentUser.getPhotoUrl().toString();
        Log.i(TAG," userProfileImageUrl : "+userProfileImageUrl );
        if (userProfileImageUrl != null) {
            binding.avatar.load(getContext(), userProfileImageUrl);
        } else {
            binding.avatar.setDefaultDrawable();
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        Toast.makeText(getActivity(),"Sign Out", Toast.LENGTH_SHORT);

        startActivity(new Intent(getActivity(), LoginActivity.class));

        // Google sign out
//        signInClient.signOut().addOnCompleteListener(requireActivity(),
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}