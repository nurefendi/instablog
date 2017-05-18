package com.masfendi.instablog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key;
    private ImageView mImagePost;
    private TextView mTitlepost, mDescpost;
    private Button mRemovebtn;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        mPost_key = getIntent().getExtras().getString("blog_id");

        mImagePost = (ImageView) findViewById(R.id.singleBlogImageView);
        mTitlepost = (TextView) findViewById(R.id.singleBlogTitleView);
        mDescpost = (TextView) findViewById(R.id.singleBlogDescView);
        mRemovebtn = (Button) findViewById(R.id.singleBlogRemoveBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = dataSnapshot.child("title").getValue().toString();
                String post_desc = dataSnapshot.child("description").getValue().toString();
                String post_image = dataSnapshot.child("image").getValue().toString();
                String post_uid = dataSnapshot.child("uid").getValue().toString();

                mTitlepost.setText(post_title);
                mDescpost.setText(post_desc);
                Picasso.with(BlogSingleActivity.this).load(post_image).into(mImagePost);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mRemovebtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRemovebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).removeValue();

                Intent profilActivity = new Intent(BlogSingleActivity.this, ProfilActivity.class);
                profilActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profilActivity);
            }
        });
    }
}
