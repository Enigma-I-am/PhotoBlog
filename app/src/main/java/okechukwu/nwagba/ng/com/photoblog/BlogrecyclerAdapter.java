package okechukwu.nwagba.ng.com.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogrecyclerAdapter extends RecyclerView.Adapter<BlogrecyclerAdapter.ViewHolder> {

    public List<Blogpost> blogposts_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    CropSquareTransformation cropSquareTransformation = new CropSquareTransformation();


    public BlogrecyclerAdapter(Context mcontext,List<Blogpost> blog_list) {

        blogposts_list =blog_list;
        context =mcontext;
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public BlogrecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.blog_list_item, parent, false);
        return new BlogrecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogrecyclerAdapter.ViewHolder holder, int position) {
        holder.descText.setText(blogposts_list.get(position).getDescription());

        String imageurl = blogposts_list.get(position).getImageUrl();
        final String user_id =blogposts_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){


                    String userName = task.getResult().getString("name");
                    String imageurl = task.getResult().getString("imageurl");
                    holder.binduserdata(imageurl,userName);
                }
            }
        });

        long milliseconds = blogposts_list.get(position).getTime().getTime();
        String dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(milliseconds).toString();

        holder.timeText.setText(dateString);


        holder.bindimage(imageurl);
    }

    @Override
    public int getItemCount() {
        return blogposts_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {



        private TextView descText;
        private ImageView blogimage;
        private CircleImageView userimage;
        private TextView Username;
        private TextView timeText;


        public ViewHolder(View itemView) {
            super(itemView);
            descText = itemView.findViewById(R.id.blog_description);
            blogimage = itemView.findViewById(R.id.blog_image);
            timeText = itemView.findViewById(R.id.blog_date);

        }

        void binduserdata(String imageurl, String username){

            userimage = itemView.findViewById(R.id.blog_userimage);
            Username = itemView.findViewById(R.id.blog_username);

            Username.setText(username);
            Picasso.get()
                    .load(imageurl)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .transform(cropSquareTransformation)
                    .into(userimage);



        }

        void bindimage(String url){
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(cropSquareTransformation)
                    .into(blogimage);
        }
    }
}
