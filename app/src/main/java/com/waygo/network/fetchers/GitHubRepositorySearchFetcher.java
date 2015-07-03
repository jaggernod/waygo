package com.waygo.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.waygo.data.stores.GitHubRepositorySearchStore;
import com.waygo.data.stores.GitHubRepositoryStore;
import com.waygo.network.NetworkApi;
import com.waygo.pojo.GitHubRepository;
import com.waygo.pojo.GitHubRepositorySearch;
import com.waygo.pojo.NetworkRequestStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.internal.Preconditions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GitHubRepositorySearchFetcher extends FetcherBase {
    private static final String TAG = GitHubRepositorySearchFetcher.class.getSimpleName();

    private final GitHubRepositoryStore gitHubRepositoryStore;
    private final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public GitHubRepositorySearchFetcher(@NonNull NetworkApi networkApi,
                                         @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                         @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                                         @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(gitHubRepositoryStore, "GitHub Repository Store cannot be null.");
        Preconditions.checkNotNull(gitHubRepositorySearchStore, ""
                                                                + "GitHub Repository Search Store cannot be null.");

        this.gitHubRepositoryStore = gitHubRepositoryStore;
        this.gitHubRepositorySearchStore = gitHubRepositorySearchStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final String searchString = intent.getStringExtra("searchString");
        if (searchString != null) {
            fetchGitHubSearch(searchString);
        } else {
            Log.e(TAG, "No searchString provided in the intent extras");
        }
    }

    private void fetchGitHubSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search String cannot be null.");

        Log.d(TAG, "fetchGitHubSearch(" + searchString + ")");
        if (requestMap.containsKey(searchString.hashCode()) &&
                !requestMap.get(searchString.hashCode()).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for repository " + searchString);
            return;
        }
        final String uri = gitHubRepositorySearchStore.getUriForKey(searchString).toString();
        Subscription subscription = createNetworkObservable(searchString)
                .subscribeOn(Schedulers.computation())
                .map((repositories) -> {
                    final List<Integer> repositoryIds = new ArrayList<>();
                    for (GitHubRepository repository : repositories) {
                        gitHubRepositoryStore.put(repository);
                        repositoryIds.add(repository.getId());
                    }
                    return new GitHubRepositorySearch(searchString, repositoryIds);
                })
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(gitHubRepositorySearchStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository search for '" + searchString + "'", e));
        requestMap.put(searchString.hashCode(), subscription);
        startRequest(uri);
    }

    @NonNull
    private Observable<List<GitHubRepository>> createNetworkObservable(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search String cannot be null.");

        return Observable.<List<GitHubRepository>>create((subscriber) -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("q", searchString);
                List<GitHubRepository> results = networkApi.search(params);
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return gitHubRepositorySearchStore.getContentUri();
    }
}
