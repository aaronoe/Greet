package de.aaronoe.greet.repository;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;

public class FireStore {

    private static final String USERS = "USERS";
    private static final String GROUPS = "GROUPS";
    private static final String GROUP_POSTS = "GROUP_POSTS";
    private static final String USERS_GROUPS = "USERS_GROUPS";
    private static final String GROUPS_USERS = "GROUPS_USERS";


    public static CollectionReference getUsersReference(FirebaseFirestore fireStore) {
        return fireStore.collection(USERS);
    }

    public static DocumentReference getUserReference(FirebaseFirestore firestore, User user) {
        return firestore.collection(USERS).document(user.getUserID());
    }

    public static CollectionReference getUsersGroupsReference(FirebaseFirestore firestore, User user) {
        return firestore.collection(USERS).document(user.getUserID()).collection(USERS_GROUPS);
    }

    public static CollectionReference getGroupsReference(FirebaseFirestore fireStore) {
        return fireStore.collection(GROUPS);
    }

    public static DocumentReference getGroupReference(FirebaseFirestore firestore, String groupId) {
        return firestore.collection(GROUPS).document(groupId);
    }

    public static CollectionReference getGroupPostsReference(FirebaseFirestore firestore, String groupId) {
        return firestore.collection(GROUPS).document(groupId).collection(GROUP_POSTS);
    }

    public static void setUser(FirebaseFirestore firestore, User user) {
        firestore.collection(USERS).document(user.getUserID()).set(user);
    }

    public static void createGroup(final FirebaseFirestore firestore, final User user, final Group group) {
        firestore.collection(GROUPS).document(group.getGroupId()).set(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firestore.collection(GROUPS).document(group.getGroupId()).collection(GROUPS_USERS).document(user.getUserID()).set(user);
                firestore.collection(USERS).document(user.getUserID()).collection(USERS_GROUPS).add(group);
            }
        });
    }

    public static void joinGroup(final FirebaseFirestore firestore, final User user, final Group group) {
        firestore.collection(GROUPS).document(group.getGroupId()).collection(GROUPS_USERS).document(user.getUserID()).set(user);
        firestore.collection(USERS).document(user.getUserID()).collection(USERS_GROUPS).add(group);
    }

    public static void postToGroup(FirebaseFirestore firestore, String groupId, Post post) {
        firestore.collection(GROUPS).document(groupId).collection(GROUP_POSTS).document(post.getId()).set(post);
    }

}
