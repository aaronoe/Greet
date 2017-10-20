package de.aaronoe.greet.ui.main;

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


public class MainViewModel extends ViewModel {

    private MutableLiveData<List<Group>> userGroups;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private User mUser;

    public MutableLiveData<List<Group>> getUserGroups(User user) {
        mUser = user;
        if (userGroups == null) {
            userGroups = new MutableLiveData<>();
            loadGroups();
        }
        return userGroups;
    }

    private void loadGroups() {
        FireStore.getUsersGroupsReference(firestore, mUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                List<Group> groups = new ArrayList<>();
                for (DocumentSnapshot value : documentSnapshots) {
                    if (!value.exists()) continue;
                    groups.add(value.toObject(Group.class));
                }

                userGroups.setValue(groups);
            }
        });
    }

}
