package com.waygo.network;

import com.waygo.pojo.GitHubRepository;

import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import rx.Observable;

public class NetworkApi {
    private final GitHubService gitHubService;

    public NetworkApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
        gitHubService = restAdapter.create(GitHubService.class);
    }

    public Observable<List<GitHubRepository>> search(Map<String, String> search) {
        return gitHubService.search(search).map(GitHubRepositorySearchResults::getItems);
    }

    public Observable<GitHubRepository> getRepository(int id) {
        return gitHubService.getRepository(id);
    }
}
