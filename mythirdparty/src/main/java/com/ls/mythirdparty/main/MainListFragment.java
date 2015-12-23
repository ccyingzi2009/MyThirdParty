package com.ls.mythirdparty.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ls.mythirdparty.OnItemClickListener;
import com.ls.mythirdparty.R;

/**
 * Created by user on 15-12-17.
 */
public class MainListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_images, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_last_images_recycler);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        final MainListAdapter adapter = new MainListAdapter();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {

            }
        });

        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view == null) {
                    return;
                }
                ProgressBar bar = (ProgressBar) view.findViewById(R.id.fragment_images_progress);
                bar.setVisibility(View.GONE);

                recyclerView.setVisibility(View.VISIBLE);
                adapter.setImages(Data.URLS);


            }
        }, 1000);
    }
}
