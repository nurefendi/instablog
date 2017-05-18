 package com.masfendi.instablog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

 public class ProfilActivity extends AppCompatActivity {

     private RecyclerView mBloglist;

     private DatabaseReference mDatabase, mDatabaseUsers,mDatabaselike, mDatabaseCurentUser;
     private FirebaseAuth mAuth;
     private FirebaseAuth.AuthStateListener mAuthListener;

     private boolean mProccesLike = false;
     private Query mQueryCurrentUser;

     private ImageButton mProfilImagebtn;
     private TextView mNameUser, mEmailuser;
     private String pos_key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        pos_key = getIntent().getExtras().getString("key_post");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        String curenUserUid = mAuth.getCurrentUser().getUid();
        mDatabaseCurentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseCurentUser.keepSynced(true);
        mQueryCurrentUser = mDatabaseCurentUser.orderByChild("uid").equalTo(curenUserUid);

        mBloglist = (RecyclerView) findViewById(R.id.blog_list_profil);
        mBloglist.setHasFixedSize(true);
        mBloglist.setLayoutManager(new LinearLayoutManager(this));

        mProfilImagebtn = (ImageButton) findViewById(R.id.user_profile_photo);
        mNameUser = (TextView) findViewById(R.id.user_profile_name);
        mEmailuser = (TextView) findViewById(R.id.user_profile_email);

        sekalilagi();
        authStatelistener();
        viewHolder();
    }
     private void sekalilagi() {
        mDatabaseCurentUser.child(pos_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("username").getValue().toString();
                String email = mAuth.getCurrentUser().getEmail();
                String ppImg = dataSnapshot.child("profil_image").getValue().toString();
                String post_uid = dataSnapshot.child("uid").getValue().toString();

                if (mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mNameUser.setText(nama);
                    mEmailuser.setText(email);
                    Picasso.with(ProfilActivity.this).load(ppImg).into(mProfilImagebtn);
                    pos_key=null;
                } else {
                    mNameUser.setText(nama);
                    mEmailuser.setText(email);
                    Picasso.with(ProfilActivity.this).load(ppImg).into(mProfilImagebtn);
                    pos_key=null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     }

     private void authStatelistener() {
         mAuthListener = new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 if (firebaseAuth.getCurrentUser() == null) {
                     Intent loginIntent = new Intent(ProfilActivity.this, LoginActivity.class);
                     loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(loginIntent);
                 }
             }
         };

     }

     private void viewHolder() {

         mAuth.addAuthStateListener(mAuthListener);
         FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecycleadapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                 Blog.class,
                 R.layout.blog_row,
                 BlogViewHolder.class,
                 mQueryCurrentUser
         ) {
             @Override
             protected void populateViewHolder(BlogViewHolder viewHolder, final Blog model, final int position) {
                 final String post_key = getRef(position).getKey();
                 pos_key = post_key;

                 viewHolder.setTitle(model.getTitle());
                 viewHolder.setDescription(model.getDescription());
                 viewHolder.setImage(getApplicationContext(), model.getImage());
                 viewHolder.setUsername(model.getUsername());
                 viewHolder.setProfil_image(getApplicationContext(), model.getProfil_image());
                 viewHolder.setLikeBtn(post_key);
                 viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent postIntent = new Intent(ProfilActivity.this, BlogSingleActivity.class);
                         postIntent.putExtra("blog_id", post_key);
                         postIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(postIntent);

                     }
                 });
                 viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         mProccesLike = true;
                         mDatabaselike.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 if (mProccesLike) {

                                     if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                                         mDatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                         mProccesLike = false;

                                     } else {
                                         mDatabaselike.child(post_key)
                                                 .child(mAuth.getCurrentUser().getUid())
                                                 .setValue(model.getUsername().toString() + " Has Likes " + model.getTitle().toString());

                                         mProccesLike = false;
                                     }
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                             }
                         });
                     }
                 });
             }
         };
         mBloglist.setAdapter(firebaseRecycleadapter);
     }
     public static class BlogViewHolder extends RecyclerView.ViewHolder {

         View mView;

         ImageButton mLikeBtn;
         DatabaseReference mDatabaseLike, mDatabasescuret;
         FirebaseAuth mAuthlike;


         public BlogViewHolder(View itemView) {
             super(itemView);

             mView = itemView;
             mLikeBtn = (ImageButton) mView.findViewById(R.id.post_like);
             mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
             mAuthlike = FirebaseAuth.getInstance();
             mDatabaseLike.keepSynced(true);

         }

         public void setLikeBtn(final String post_key) {
             mDatabaseLike.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if (dataSnapshot.child(post_key).hasChild(mAuthlike.getCurrentUser().getUid())){

                         mLikeBtn.setImageResource(R.mipmap.ic_like_like);

                     } else {
                         mLikeBtn.setImageResource(R.mipmap.ic_like_unlike);

                     }

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
         public void setTitle(String title){
             TextView post_title = (TextView) mView.findViewById(R.id.post_title);
             post_title.setText(title);
         }

         public void setDescription(String description) {
             TextView post_descr = (TextView) mView.findViewById(R.id.post_desc);
             post_descr.setText(description);
         }
         public void setImage(Context ctx, String image) {

             ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
             Picasso.with(ctx).load(image).into(post_image);
         }
         public void setUsername(String username){
             TextView post_username = (TextView) mView.findViewById(R.id.post_name);
             post_username.setText(username);
         }
         public void setProfil_image(Context ctx, String profil_image){
             ImageView post_profilImage = (ImageView) mView.findViewById(R.id.post_Profil);
             Picasso.with(ctx).load(profil_image).into(post_profilImage);
         }

     }
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main_menu, menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {

         if (item.getItemId() == R.id.add_newpost) {
             Intent postIntent = new Intent(ProfilActivity.this, PostActivity.class);
             postIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(postIntent);
         }

         if (item.getItemId() == R.id.logout_menu) {
             logOut();
         }

         if (item.getItemId() == R.id.setting_menu) {

         }

         return super.onOptionsItemSelected(item);
     }

     private void logOut() {
         mAuth.signOut();
         Intent profilIntent = new Intent(ProfilActivity.this, LoginActivity.class);
         profilIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(profilIntent);
     }


 }
