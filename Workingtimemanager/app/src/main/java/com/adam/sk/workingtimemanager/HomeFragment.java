package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.adam.sk.workingtimemanager.controller.TimeController;

import org.joda.time.DateTime;

import javax.inject.Inject;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {
    Context thisContext;

    @BindView(R.id.label)
    TextView goHome;

    @BindView(R.id.textView3)
    TextView overTime;

    @BindView(R.id.textView4)
    TextView goHomeOv;

    @Inject
    public TimeController timeController;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        thisContext = container.getContext();
        ButterKnife.bind(this, rootView);

        ((Main) thisContext.getApplicationContext()).getComponent().inject(this);

        final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        CircleButton btnScale = (CircleButton) rootView.findViewById(R.id.button2);

        btnScale.setOnClickListener(v -> {
            v.startAnimation(animRotate);
            v.getResources();
            CircleButton btnScale1 = (CircleButton) v.findViewById(R.id.button2);
            DateTime today = new DateTime();
            timeController.saveWorkTime(today);
            timeController.calculateTime(today);

            goHome.setText(timeController.getGoHomeTime());
            goHomeOv.setText(timeController.getGoHomeTimeOv());
            overTime.setText(timeController.getOverTime());
        });

        goHome.setText("00:00");
        goHomeOv.setText("00:00");
        overTime.setText("00:00");

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
