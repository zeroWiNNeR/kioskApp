package com.adamanta.kioskapp.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.utils.ProductsDBHelper;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private ProductsDBHelper productsDBHelper;
    private SearchRVAdapter searchRVAdapter;

    public static SearchFragment newInstance(int num) {
        SearchFragment f = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        productsDBHelper = new ProductsDBHelper(view.getContext());

        searchRVAdapter = new SearchRVAdapter(productsDBHelper.getAllProducts());
        final RecyclerView searchRV = view.findViewById(R.id.fragment_search_rv);
        searchRV.setHasFixedSize(false);
        searchRV.setAdapter(searchRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        searchRV.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        searchRV.setItemAnimator(itemAnimator);

        return view;
    }

    @Override
    public void onClick(View view) {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void searchInDb(String s) {
        searchRVAdapter.updateData(productsDBHelper.getFilteredProducts(s));
    }
}
