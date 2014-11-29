package com.tpom6oh.crane;

import java.util.Set;

/**
 * Created by alx on 12.10.14.
 */
public interface IDataSource {
    Set<String> getHostsData();

    void addHost(String host);

    void putPassword(String host, String password);

    String getPassword(String host);

    void setPersistentPasswordHash(String passwordHash);

    String getPersistentPasswordHash();
}
