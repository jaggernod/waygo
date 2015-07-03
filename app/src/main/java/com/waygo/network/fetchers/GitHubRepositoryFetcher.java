package com.waygo.network.fetchers;

import com.waygo.data.stores.GitHubRepositoryStore;
import com.waygo.network.NetworkApi;
import com.waygo.pojo.GitHubRepository;
import com.waygo.pojo.NetworkRequestStatus;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.android.internal.Preconditions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GitHubRepositoryFetcher extends FetcherBase {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    @NonNull
    private final GitHubRepositoryStore gitHubRepositoryStore;

    public GitHubRepositoryFetcher(@NonNull NetworkApi networkApi,
                                   @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                   @NonNull GitHubRepositoryStore gitHubRepositoryStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(gitHubRepositoryStore, "GitHub Repository Store cannot be null.");

        this.gitHubRepositoryStore = gitHubRepositoryStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch Intent cannot be null.");

        final int repositoryId = intent.getIntExtra("id", -1);
        if (repositoryId != -1) {
            fetchGitHubRepository(repositoryId);
        } else {
            Log.e(TAG, "No repositoryId provided in the intent extras");
        }
    }

    private void fetchGitHubRepository(final int repositoryId) {
        Log.d(TAG, "fetchGitHubRepository(" + repositoryId + ")");
        if (requestMap.containsKey(repositoryId) &&
                !requestMap.get(repositoryId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for repository " + repositoryId);
            return;
        }
        final String uri = gitHubRepositoryStore.getUriForKey(repositoryId).toString();
        Subscription subscription =  networkApi.getRepository(repositoryId)
                .subscribeOn(Schedulers.computation())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(gitHubRepositoryStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository " + repositoryId, e));
        requestMap.put(repositoryId, subscription);
        startRequest(uri);
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return gitHubRepositoryStore.getContentUri();
    }
}
