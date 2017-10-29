package de.aaronoe.greet.ui.groupdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.FireStore;


public class GroupViewModel extends ViewModel {

    private MutableLiveData<List<Post>> mLivePosts;
    private Group mGroup;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public MutableLiveData<List<Post>> getLivePosts(Group group) {
        this.mGroup = group;
        if (mLivePosts == null) {
            mLivePosts = new MutableLiveData<>();
        }
        loadPosts();
        return mLivePosts;
    }

    private void loadPosts() {
        FireStore.getGroupPostsReference(firestore, mGroup.getGroupId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                List<Post> postList = new ArrayList<>();
                for (DocumentSnapshot value : documentSnapshots) {
                    if (!value.exists()) continue;
                    postList.add(value.toObject(Post.class));
                }
                mLivePosts.setValue(postList);
            }
        });
    }

}
