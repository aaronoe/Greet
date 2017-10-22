package de.aaronoe.greet;

import android.annotation.SuppressLint;
import android.app.Application;

import org.androidannotations.annotations.EApplication;

@SuppressLint("Registered")
@EApplication
public class GreetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
