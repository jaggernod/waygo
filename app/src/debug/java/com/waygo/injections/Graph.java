package com.waygo.injections;

import com.waygo.WaygoApplication;
import com.waygo.activities.MainActivity;
import com.waygo.data.DataStoreModule;
import com.waygo.fragments.AgendaListFragment;
import com.waygo.fragments.ChatListFragment;
import com.waygo.network.NetworkService;
import com.waygo.viewmodels.ChatViewModel;
import com.waygo.viewmodels.RepositoryViewModel;
import com.waygo.viewmodels.ViewModelModule;
import com.waygo.widget.WidgetService;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DataStoreModule.class, ViewModelModule.class,
                      DebugInstrumentationModule.class})
public interface Graph {

    void inject(RepositoryViewModel widgetService);
    void inject(ChatViewModel chatViewModel);
    void inject(WidgetService widgetService);
    void inject(MainActivity mainActivity);
    void inject(NetworkService networkService);
    void inject(WaygoApplication waygoApplication);
    void inject(AgendaListFragment agendaListFragment);
    void inject(ChatListFragment chatListFragment);

    final class Initializer {

        public static Graph init(Application application) {
            return DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }

    }
}
