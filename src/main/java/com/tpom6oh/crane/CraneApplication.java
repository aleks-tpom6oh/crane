/**
 * Created on 29.11.14
 * @author alexey@plainvanillagames.com
 */
package com.tpom6oh.crane;

import android.app.Application;
import dagger.ObjectGraph;

public class CraneApplication extends Application implements Injector
{
    public static ObjectGraph graph;
    private String userPassword;

    public String getUserPassword()
    {
        return userPassword;
    }

    public void setUserPassword(String userPassword)
    {
        this.userPassword = userPassword;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        graph = ObjectGraph.create(new MainModule(this));
    }

    @Override
    public void inject(Object o)
    {
        graph.inject(o);
    }
}
