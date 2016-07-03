package com.dohabit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dohabit.R;

public class EditPhotoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_profile, container, false);
        // TODO: 30/06/2016 Create edit photo fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
