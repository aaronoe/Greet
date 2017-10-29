package de.aaronoe.greet.ui.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;


public class SearchViewModel extends ViewModel {

    private static final String PROPERTY_GROUP_NAME = "groupName";

    private MutableLiveData<List<Group>> mGroups;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public MutableLiveData<List<Group>> getGroups(String query) {
        if (mGroups == null) {
            mGroups = new MutableLiveData<>();
        }
        if (query != null) {
            downloadGroups(query);
        }
        return mGroups;
    }

    void joinGroup(User user, Group group) {
        FireStore.joinGroup(firestore, user, group);
    }

    private void downloadGroups(String query) {
        FireStore.getGroupsReference(firestore)
                .whereGreaterThanOrEqualTo(PROPERTY_GROUP_NAME, query)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        List<Group> groups = new ArrayList<>();
                        for (DocumentSnapshot value : documentSnapshots) {
                            if (!value.exists()) continue;
                            groups.add(value.toObject(Group.class));
                        }
                        mGroups.setValue(groups);
                    }
                });
    }

}
