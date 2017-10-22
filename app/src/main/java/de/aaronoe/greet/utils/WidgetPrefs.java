package de.aaronoe.greet.utils;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface WidgetPrefs {

    @DefaultString("def")
    String groupName();

    @DefaultString("def")
    String groupId();

    long lastUpdated();

}
