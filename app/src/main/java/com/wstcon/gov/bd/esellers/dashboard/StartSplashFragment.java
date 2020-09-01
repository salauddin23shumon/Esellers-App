package com.wstcon.gov.bd.esellers.dashboard;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wstcon.gov.bd.esellers.R;
import com.wstcon.gov.bd.esellers.category.categoryModel.Category;
import com.wstcon.gov.bd.esellers.category.categoryModel.CategoryResponse;
import com.wstcon.gov.bd.esellers.database.DatabaseQuery;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.HorizontalModel;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.SliderImage;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.SliderResponse;
import com.wstcon.gov.bd.esellers.mainApp.dataModel.VerticalModel;
import com.wstcon.gov.bd.esellers.networking.RetrofitClient;
import com.wstcon.gov.bd.esellers.product.productModel.Product;
import com.wstcon.gov.bd.esellers.product.productModel.ProductResponse;
import com.wstcon.gov.bd.esellers.utility.EventBusMessenger;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.wstcon.gov.bd.esellers.utility.Constant.BASE_URL;
import static com.wstcon.gov.bd.esellers.utility.Utils.isNetworkAvailable;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartSplashFragment extends Fragment {

    private String TAG = "StartSplashFragment";
    private SplashAction action;
    private ArrayList<VerticalModel> vmList = new ArrayList<>();
    private DatabaseQuery query;
    private int sliderSize = 0;
    private int catSize = 0;
    private Context context;
    private GifImageView gifImageView;
    private Button tryBtn;
    private EventBus eventBus = EventBus.getDefault();
    private EventBusMessenger messenger;


    public StartSplashFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        query = new DatabaseQuery(context);
        action = (SplashAction) context;
        messenger=new EventBusMessenger(0,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_start_splash, container, false);
        gifImageView = view.findViewById(R.id.loginGif);
        tryBtn = view.findViewById(R.id.tryBtn);


        tryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryBtn.setVisibility(View.GONE);
                gifImageView.setVisibility(View.VISIBLE);
                fetchData();
            }
        });

        fetchData();

        return view;
    }


    private void fetchData(){
        if (isNetworkAvailable(context)) {
            if (query.getCatIconCount() > 4 && query.getSliderCount() > 4) {
                addData();
            } else
                runThreads();
        } else {
            Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
            gifImageView.setVisibility(View.GONE);
            tryBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }

    private void fetchSlider() {
        Log.d(TAG, "fetchSlider: cld");
        Call<SliderResponse> call = RetrofitClient.getInstance().getApiInterface().getSliders();
        call.enqueue(new Callback<SliderResponse>() {
            @Override
            public void onResponse(Call<SliderResponse> call, Response<SliderResponse> response) {
                List<SliderImage> sliderImages = response.body().getSliderImages();
                Log.e(TAG, "onResponse: " + sliderImages.size());
                sliderSize = sliderImages.size();
                for (SliderImage s : sliderImages) {
                    getSliderPhoto(s);
                }
            }
            @Override
            public void onFailure(Call<SliderResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    private void getSliderPhoto(final SliderImage slider) {
        Log.d(TAG, "getSliderPhoto: cld");
        Glide.with(context).asBitmap().load(BASE_URL + slider.getSliderImage()).timeout(2000).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                slider.setBitmap(resource);
//                Log.e(TAG, "onResourceReady: " + slider.getSliderImage());
                query.insertSlider(slider);
                if (query.getSliderCount() == sliderSize)
                    messenger.setComplete1(1);
                eventBus.post(messenger);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                Log.e(TAG, "onLoadFailed: called 4 slider image");
                eventBus.post(new EventBusMessenger(true));
            }
        });

    }

    private void getCatIcon(final Category category) {
        Log.d(TAG, "getCatIcon: cld");
        Glide.with(context).asBitmap().load(BASE_URL + category.getCategoryIcon()).timeout(2000).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                category.setBitmap(resource);
//                Log.e(TAG, "onResourceReady: " + category.getCategoryIcon());
                query.insertCategory(category);
                if (query.getCatIconCount() == catSize) {
                    if (query.getSliderCount() == sliderSize)
                        messenger.setComplete2(1);
                    eventBus.post(messenger);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                eventBus.post(new EventBusMessenger(true));
                Log.e(TAG, "onLoadFailed: called 4 catIcon");
            }
        });
    }

    private void addData() {

        Log.d(TAG, "addData: cld");

        final ArrayList<HorizontalModel> horizontalModels1 = new ArrayList<>();
        final ArrayList<HorizontalModel> horizontalModels2 = new ArrayList<>();
        final ArrayList<HorizontalModel> horizontalModels3 = new ArrayList<>();
        final ArrayList<HorizontalModel> horizontalModels4 = new ArrayList<>();

        Call<ProductResponse> call = RetrofitClient.getInstance().getApiInterface().getAllProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 1) {
                        List<Product> products = response.body().getProducts();
                        Log.e(TAG, "onResponse: products size " + products.size());
                        for (Product p : products) {
                            if (p.getProductStatusName().equals("Featured"))
                                horizontalModels1.add(new HorizontalModel(p));
                            else if (p.getProductStatusName().equals("Offer"))
                                horizontalModels2.add(new HorizontalModel(p));
                            else if (p.getProductStatusName().equals("Latest"))
                                horizontalModels3.add(new HorizontalModel(p));
                            else if (p.getProductStatusName().equals("Popular"))
                                horizontalModels4.add(new HorizontalModel(p));
                        }

                        vmList.add(new VerticalModel(horizontalModels1));
                        vmList.add(new VerticalModel(horizontalModels2));
                        vmList.add(new VerticalModel(horizontalModels3));
                        vmList.add(new VerticalModel(horizontalModels4));

                        action.onSplashFinished(vmList);
                    } else {
                        Log.d(TAG, "onResponse: " + response.body().getMessage());
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.code());
                    gifImageView.setVisibility(View.GONE);
                    tryBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Server busy... please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {

                gifImageView.setVisibility(View.GONE);
                tryBtn.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Please check internet connection ", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }


    private void getCategory() {
        Log.d(TAG, "getCategory: cld");
        Call<CategoryResponse> call = RetrofitClient.getInstance().getApiInterface().getAllCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                List<Category> categoryList = response.body().getCategories();
                catSize = categoryList.size();
                Log.e(TAG, "getCategory: size" + catSize);
                for (Category c : categoryList) {
                    getCatIcon(c);
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessenger eventBusMessenger) {
        Log.d(TAG, "onEvent: called" + "  " + eventBusMessenger.isFailed());
        if (eventBusMessenger.isFailed()) {
            gifImageView.setVisibility(View.GONE);
            tryBtn.setVisibility(View.VISIBLE);
        }


        if (eventBusMessenger.getComplete1() != 0 && eventBusMessenger.getComplete2() != 0) {
            Log.d(TAG, "onEvent: " + eventBusMessenger.getComplete1() + " " + eventBusMessenger.getComplete2());
            addData();
        }
    }


    private void runThreads() {
        getSlider();
        getCategoryIcon();
    }

    private void getSlider() {
        new Thread() {
            public void run() {
                fetchSlider();
            }
        }.start();
    }

    private void getCategoryIcon() {
        new Thread() {
            public void run() {
                getCategory();
            }
        }.start();
    }

    public interface SplashAction {
        void onSplashFinished(List<VerticalModel> vmList);
    }

}
