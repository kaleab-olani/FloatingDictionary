package com.app.kol.fd.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.kol.fd.R;
import com.app.kol.fd.service.CustomFloatingViewService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DeleteActionFragment extends Fragment {

    /**
     * DeleteActionFragment
     *
     * @return DeleteActionFragment
     */
    public static DeleteActionFragment newInstance() {
        final DeleteActionFragment fragment = new DeleteActionFragment();
        return fragment;
    }

    public DeleteActionFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delete_action, container, false);

        final View clearFloatingButton = rootView.findViewById(R.id.clearDemo);
        clearFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Easy way to delete a service
                final Activity activity = getActivity();
                activity.stopService(new Intent(activity, CustomFloatingViewService.class));
            }
        });
        return rootView;
    }
}
