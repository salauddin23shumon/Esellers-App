package com.wstcon.gov.bd.esellers.dashboard;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wstcon.gov.bd.esellers.R;
import com.wstcon.gov.bd.esellers.database.DatabaseQuery;
import com.wstcon.gov.bd.esellers.interfaces.AddorRemoveCallbacks;
import com.wstcon.gov.bd.esellers.mainApp.adapter.MixedAdapter;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.RecyclerViewItem;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.SliderImage;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.VerticalModel;
import com.wstcon.gov.bd.esellers.product.ProductFragment;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment ";
    private RecyclerView verticalRV;
    private Context context;
    private ArrayList<VerticalModel> vmList;
    private MixedAdapter mixedAdapter;
    private ArrayList<RecyclerViewItem> items;
    private List<SliderImage> sliders;
    private int cart_count=0;


    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        this.context = context;
        Bundle bundle = this.getArguments();
        DatabaseQuery query=new DatabaseQuery(context);
        sliders=query.getSlider();
        if (bundle != null) {
            vmList= (ArrayList<VerticalModel>) bundle.getSerializable("product");
        }
        Log.e(TAG, "onAttach: " + sliders.size());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        verticalRV = view.findViewById(R.id.verticalRV);

        items = new ArrayList<>();
        mixedAdapter = new MixedAdapter(items, context);

        verticalRV.setHasFixedSize(true);
        verticalRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        verticalRV.setAdapter(mixedAdapter);

        mixedAdapter.updateSlider(sliders);
        mixedAdapter.updateList(vmList);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


//    @Override
//    public void onAddProduct() {
//        cart_count++;
//        getActivity().invalidateOptionsMenu();
//        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onRemoveProduct() {
//        cart_count--;
//        getActivity().invalidateOptionsMenu();
//        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
//    }
}
