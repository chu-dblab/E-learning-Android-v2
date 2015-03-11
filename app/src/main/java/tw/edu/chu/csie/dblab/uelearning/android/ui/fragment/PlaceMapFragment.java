package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

/**
 * Created by yuan on 2014/12/25.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tw.edu.chu.csie.dblab.uelearning.android.R;

/**
 * 館區地圖
 */
public class PlaceMapFragment extends Fragment {

    private ListView mList_place_map;
    private String[] itemEnableActivity_default = {"Entry"};
    public static PlaceMapFragment newInstance(int sectionNumber) {
        PlaceMapFragment fragment = new PlaceMapFragment();
        return fragment;
    }

    public PlaceMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);
        initUI(rootView);
        return rootView;
    }

    protected void initUI(View rootView) {
        mList_place_map = (ListView)rootView.findViewById(R.id.list_place_map);
        ArrayAdapter<String> arrayData ;
        arrayData = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, itemEnableActivity_default);
    }
}
