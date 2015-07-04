package com.waygo.fragments;

import com.waygo.R;
import com.waygo.WaygoApplication;
import com.waygo.activities.MainActivity;
import com.waygo.data.model.fuel.Fuel;
import com.waygo.utils.Instrumentation;
import com.waygo.view.RepositoryView;
import com.waygo.viewmodels.RepositoryViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class AgendaListFragment extends Fragment {

    private static final String TAG = AgendaListFragment.class.getSimpleName();

    private RepositoryView repositoryView;

//    @Inject
//    RepositoryViewModel viewModel;

    @Inject
    Instrumentation instrumentation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WaygoApplication.getInstance().getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.agenda_list, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                                                                    Arrays.asList("1", "2", "1", "2", "1", "2")));
    }

    @Override
    public void onResume() {
        super.onResume();
//        repositoryView.setViewModel(viewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
//        repositoryView.setViewModel(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        viewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        viewModel.dispose();
//        viewModel = null;
        instrumentation.getLeakTracing().traceLeakage(this);
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
            }
        }

//        public static class ViewHolder1 extends ViewHolder {
//            public String mBoundString;
//
//            public final View mView;
//            public final TextView m;
//
//            public ViewHolder1(View view) {
//                super(view);
//                mView = view;
//                mTextView = (TextView) view.findViewById(R.id.info_text);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + mTextView.getText();
//            }
//        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(getLayout(viewType), parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        private int getLayout(int viewType) {
            switch (viewType) {
                case 0: return R.layout.flight_item;
                case 1: return R.layout.map_item;
                case 2: return R.layout.sun_item;
                default: return R.layout.list_item;
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                default: setupViewHolder(holder, position);
            }
        }

        private void setupViewHolder(ViewHolder holder, int position) {
//            holder.mBoundString = mValues.get(position);
//            holder.mTextView.setText(mValues.get(position));
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Context context = v.getContext();
////                    Intent intent = new Intent(context, CheeseDetailActivity.class);
////                    intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
////
////                    context.startActivity(intent);
//                }
//            });
//
////            Glide.with(holder.mImageView.getContext())
////                 .load(Cheeses.getRandomCheeseDrawable())
////                 .fitCenter()
////                 .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public int getItemViewType (int position) {
            return position % 10;
        }
    }
}
