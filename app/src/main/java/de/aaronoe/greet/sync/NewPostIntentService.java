package de.aaronoe.greet.sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import java.io.File;
import java.io.IOException;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.FireStore;
import id.zelory.compressor.Compressor;

@SuppressLint("Registered")
@EIntentService
public class NewPostIntentService extends IntentService {

    private static final String TAG = "NewPostIntentService";

    private StorageReference mStorageRef;

    public NewPostIntentService() {
        super(TAG);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @ServiceAction
    void addPostToFirestore(final Group group, final Post post) {
        // Note: this post object comes with a local URI, we need to upload the file to Firebase and then use that URL
        if (post.getPostImageUrl() != null) {
            File imageFile = new File(post.getPostImageUrl());
            try {
                File compressedImageFile = new Compressor(this).compressToFile(imageFile);
                Uri fileUri = Uri.fromFile(compressedImageFile);

                String location = "images" +
                        "/" +
                        group.getGroupId() +
                        "/" +
                        post.getId();

                StorageReference imageRef = mStorageRef.child(location);
                imageRef.putFile(fileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                if (downloadUrl != null) {
                                    post.setPostImageUrl(downloadUrl.toString());
                                }
                                addPost(group, post);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            addPost(group, post);
        }
    }

    private void addPost(Group group, Post post) {
        FireStore.postToGroup(FirebaseFirestore.getInstance(), group.getGroupId(), post);
        // Also update the latest post in that Group
        group.setLatestPost(post);
        FireStore.updateGroup(FirebaseFirestore.getInstance(), group);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // do nothing here
    }
}
