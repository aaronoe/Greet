package de.aaronoe.greet.repository;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.aaronoe.greet.model.User;

public class FireStore {

    private static final String USERS = "USERS";
    private static final String GROUPS = "GROUPS";
    private static final String GROUP_POSTS = "GROUP_POSTS";

    public static CollectionReference getUsersReference(FirebaseFirestore fireStore) {
        return fireStore.collection(USERS);
    }

    public static DocumentReference getUserReference(FirebaseFirestore firestore, String username) {
        return firestore.collection(USERS).document(username);
    }

    public static CollectionReference getGroupsReference(FirebaseFirestore fireStore) {
        return fireStore.collection(GROUPS);
    }

    public static DocumentReference getGroupReference(FirebaseFirestore firestore, String groupName) {
        return firestore.collection(GROUPS).document(groupName);
    }

    public static CollectionReference getGroupPostsReference(FirebaseFirestore firestore, String groupName) {
        return firestore.collection(GROUPS).document(groupName).collection(GROUP_POSTS);
    }

    public static void setUser(FirebaseFirestore firestore, User user) {
        firestore.collection(USERS).document(user.getUserID()).set(user);
    }

}
