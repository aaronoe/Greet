package de.aaronoe.greet.ui.groupdetail;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;

@EActivity(R.layout.group_host_activity)
public class GroupHostActivity extends AppCompatActivity {

    @Extra("group_extra")
    Group mGroup;

    @InstanceState
    boolean firstStart = false;

    @AfterViews
    void init() {
        if (!firstStart) {
            GroupFragment fragment = GroupFragment_.builder().mGroup(mGroup).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.group_host_frame, fragment)
                    .commit();
            firstStart = true;
        }

    }

}
