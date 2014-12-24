package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

/**
 * Created by yuan on 2014/12/25.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.edu.chu.csie.dblab.uelearning.android.R;

/**
 * 瀏覽教材
 */
public class BrowseMaterialFragment extends Fragment {


    public static BrowseMaterialFragment newInstance(int sectionNumber) {
        BrowseMaterialFragment fragment = new BrowseMaterialFragment();
        return fragment;
    }

    public BrowseMaterialFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_material, container, false);
        return rootView;
    }
}