package de.aaronoe.greet.ui.groupdetail;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;

@EActivity(R.layout.group_host_activity)
public class GroupHostActivity extends AppCompatActivity {

    @Extra("group_extra")
    Group mGroup;

    @AfterViews
    void init() {
        GroupFragment fragment = GroupFragment_.builder().mGroup(mGroup).build();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.group_host_frame, fragment)
                .commit();
    }

}
