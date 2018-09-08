package okechukwu.nwagba.ng.com.photoblog;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    private RecyclerView homeRecyclerview;
    private List<Blogpost> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private BlogrecyclerAdapter blogrecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private Query firstQuery;
    private DocumentSnapshot lastvisible;


    private boolean isFirstpageLoadedFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container,false);

        blog_list = new ArrayList<>();
        homeRecyclerview = view.findViewById(R.id.fragment_home_recyclerview);
        blogrecyclerAdapter = new BlogrecyclerAdapter(view.getContext(),blog_list);
        homeRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        homeRecyclerview.setAdapter(blogrecyclerAdapter);
        

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        homeRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean reachedbottom = !recyclerView.canScrollVertically(1);

                if (reachedbottom){

                    String desc = lastvisible.get("description").toString();

                    Toast.makeText(container.getContext(), "Reached "+desc , Toast.LENGTH_SHORT).show();

                    LoadMorePosts();
                }
            }
        });

        if (firebaseAuth.getCurrentUser() != null){

            firstQuery = firebaseFirestore.collection("Posts").orderBy("time",Query.Direction.DESCENDING).limit(3);


            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (isFirstpageLoadedFirst){

                        lastvisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() -1);

                    }


                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){



                        if (doc.getType()== DocumentChange.Type.ADDED){

                            Blogpost blogpost = doc.getDocument().toObject(Blogpost.class);

                            if (isFirstpageLoadedFirst){

                                blog_list.add(blogpost);
                            }else {
                                blog_list.add(0,blogpost);
                            }


                            blogrecyclerAdapter.notifyDataSetChanged();
                        }


                    }

                    isFirstpageLoadedFirst = false;

                }
            });

        }




        return view;

    }

    private void LoadMorePosts(){
       Query first = firebaseFirestore.collection("Posts")
               .orderBy("time",Query.Direction.DESCENDING)
               .startAfter(lastvisible)
               .limit(3);


        first.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                        lastvisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() -1);

                        if (doc.getType()== DocumentChange.Type.ADDED){
                            Blogpost blogpost = doc.getDocument().toObject(Blogpost.class);
                            blog_list.add(blogpost);

                            blogrecyclerAdapter.notifyDataSetChanged();
                        }


                    }
                }


            }
        });
    }

}
