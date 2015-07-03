package com.waygo.network;

import com.waygo.pojo.GitHubRepository;

import android.support.annotation.NonNull;

import java.util.List;

import rx.android.internal.Preconditions;

public class GitHubRepositorySearchResults {

    @NonNull
    final private List<GitHubRepository> items;

    public GitHubRepositorySearchResults(@NonNull final List<GitHubRepository> items) {
        Preconditions.checkNotNull(items, "GitHub Repository Items cannot be null.");

        this.items = items;
    }

    @NonNull
    public List<GitHubRepository> getItems() {
        return items;
    }
}
