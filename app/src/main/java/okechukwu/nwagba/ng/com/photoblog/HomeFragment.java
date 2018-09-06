package okechukwu.nwagba.ng.com.photoblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    private RecyclerView homeRecyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        homeRecyclerview = getActivity().findViewById(R.id.fragment_home_recyclerview);


        return inflater.inflate(R.layout.fragment_home, container, false);

    }

}
