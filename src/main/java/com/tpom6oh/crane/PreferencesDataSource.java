package com.tpom6oh.crane;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by alx on 12.10.14.
 */
public class PreferencesDataSource implements IDataSource {

    private static final String PREFERENCES_TAG = "Hosts shared preferences";
    private static final String HOSTS_TAG = "Hosts";
    private static final String PASSWORD_HASH_TAG = "PASSWORD";

    private SharedPreferences sharedPreferences;

    public PreferencesDataSource(Context context) {
        sharedPreferences = getPrefs(context);
    }

    @Override
    public Set<String> getHostsData() {
        return sharedPreferences.getStringSet(HOSTS_TAG, new TreeSet<String>());
    }

    @Override
    public void addHost(String host) {
        Set<String> hosts = getHostsData();
        hosts.add(host);

        sharedPreferences.edit().putStringSet(HOSTS_TAG, hosts).apply();
    }

    @Override
    public void putPassword(String host, String password) {
        if (!checkHostExists(host)) {
            addHost(host);
        }

        sharedPreferences.edit().putString(host, password).apply();
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
    }

    private boolean checkHostExists(String host) {
        return getHostsData().contains(host);
    }

    @Override
    public String getPassword(String host) {
        return sharedPreferences.getString(host, null);
    }

    @Override
    public void setPersistentPasswordHash(String passwordHash)
    {
        sharedPreferences.edit().putString(PASSWORD_HASH_TAG, passwordHash).apply();
    }

    @Override
    public String getPersistentPasswordHash()
    {
        return sharedPreferences.getString(PASSWORD_HASH_TAG, null);
    }
}
