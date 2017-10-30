package de.aaronoe.greet;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import org.androidannotations.annotations.EApplication;

@SuppressLint("Registered")
@EApplication
public class GreetApplication extends MultiDexApplication {}
