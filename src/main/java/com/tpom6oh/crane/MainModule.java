/**
 * Created on 29.11.14
 * @author alexey@plainvanillagames.com
 */
package com.tpom6oh.crane;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
        library = true,
        injects = {
                CraneApplication.class,
                HolderActivity.class
        }
)
public class MainModule
{
    private Context context;

    public MainModule(Context context)
    {
        this.context = context;
    }

    @Provides
    Context provideContext()
    {
        return context;
    }

    @Provides
    @Singleton
    CryptUtils provideHttpClient()
    {
        return new CryptUtils();
    }

    @Provides
    @Singleton
    IDataSource provideDataSource(Context context)
    {
        return new PreferencesDataSource(context);
    }
}
