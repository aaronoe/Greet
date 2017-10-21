package de.aaronoe.greet.ui.newpost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.sync.NewPostIntentService_;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

@SuppressLint("Registered")
@EActivity(R.layout.activity_new_post)
public class NewPostActivity extends AppCompatActivity {

    @ViewById(R.id.new_post_container)
    FrameLayout mPostFrame;
    @ViewById(R.id.post_author_iv)
    CircleImageView mAuthorIv;
    @ViewById(R.id.author_name_tv)
    TextView mAuthorNameTv;
    @ViewById(R.id.new_post_edittext)
    EditText mPostEditText;
    @ViewById(R.id.post_image_iv)
    ImageView mPostPreviewImageView;
    @ViewById(R.id.post_add_Image)
    ImageView mAddImageView;

    @InstanceState
    String previewImagePath;
    @Extra
    Group mGroup;

    FirebaseUser mUser;

    @AfterViews
    void init() {

        mPostEditText.setLines(5);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.new_post_toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadPreviewImage();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mAuthorNameTv.setText(mUser.getDisplayName());
            Glide.with(this)
                    .load(mUser.getPhotoUrl())
                    .into(mAuthorIv);
        }
    }

    @Click({R.id.post_add_Image, R.id.post_image_iv})
    void onAddPhoto() {
        EasyImage.openChooserWithGallery(this, "Choose Image for Post", 0);
    }

    @Click(R.id.fab_post)
    void createPost() {
        String postText = mPostEditText.getText().toString();
        if (postText.length() != 0) {

            Post post = new Post(new User(mUser), previewImagePath, postText);

            NewPostIntentService_
                    .intent(getApplication())
                    .addPostToFirestore(mGroup, post)
                    .start();

            Toast.makeText(getApplication(), "Your post is being created", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Snackbar.make(mPostFrame, "Please enter some text", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                previewImagePath = imageFile.getAbsolutePath();
                loadPreviewImage();
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }
        });
    }

    private void loadPreviewImage() {
        if (previewImagePath != null) {
            Glide.with(NewPostActivity.this)
                    .load(previewImagePath)
                    .into(mPostPreviewImageView);
            mAddImageView.setVisibility(View.GONE);
            mPostPreviewImageView.setVisibility(View.VISIBLE);
        } else {
            mAddImageView.setVisibility(View.VISIBLE);
            mPostPreviewImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
