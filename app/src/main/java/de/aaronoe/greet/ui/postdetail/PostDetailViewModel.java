package de.aaronoe.greet.ui.postdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.greet.model.Comment;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.FireStore;


public class PostDetailViewModel extends ViewModel {

    public MutableLiveData<List<Comment>> mCommentsLiveData;

    public MutableLiveData<List<Comment>> getCommentsLiveData(Group group, Post post) {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
            downloadComments(group, post);
        }
        return mCommentsLiveData;
    }

    private void downloadComments(Group group, Post post) {
        FireStore.getCommentsReference(FirebaseFirestore.getInstance(), group, post)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        List<Comment> groups = new ArrayList<>();
                        for (DocumentSnapshot value : documentSnapshots) {
                            if (!value.exists()) continue;
                            groups.add(value.toObject(Comment.class));
                        }
                        mCommentsLiveData.setValue(groups);
                    }
                });
    }

}
