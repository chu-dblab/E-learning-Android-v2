package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.edu.chu.csie.dblab.uelearning.android.R;

/**
 * 館場資訊
 */
public class PlaceInfoFragment extends Fragment {


    public static PlaceInfoFragment newInstance(int sectionNumber) {
        PlaceInfoFragment fragment = new PlaceInfoFragment();
        return fragment;
    }

    public PlaceInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place_info, container, false);
        return rootView;
    }
}