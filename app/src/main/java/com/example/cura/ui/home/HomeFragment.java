package com.example.cura.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.cura.EditImageActivity;
import com.example.cura.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Button addFood, addIngredient, analyze, addIngredientUsingCamera;
    EditText foodItem, foodIngredient;
    TextView ingredientsList, IngredientsListMessage;
    ImageView ingredients;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        addFood = root.findViewById(R.id.saveFoodItem);
//        addIngredient = root.findViewById(R.id.addIngredientsButton);
//        analyze = root.findViewById(R.id.analyseButton);
//        foodItem = root.findViewById(R.id.foodName);
//        foodIngredient = root.findViewById(R.id.addIngredientsEditText);
//        foodIngredientList = root.findViewById(R.id.ingredientsListToShow);
        addIngredientUsingCamera = root.findViewById(R.id.addIngredientsButtonUsingCamera);
        ingredientsList = root.findViewById(R.id.ingredientList);

        String s = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("ingredients details");


        if (s!=null)
        {
            root.findViewById(R.id.ingredientListMessage).setVisibility(View.VISIBLE);
            try {
                s = s.replace(", ", "\n");
                s = s.toUpperCase();
                s = s.substring(s.indexOf("INGRE"));
                ingredientsList.setText(s);
            }catch (Exception e){
                Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
            }

        }else{
            root.findViewById(R.id.ingredientListMessage).setVisibility(View.GONE);
        }
        addIngredientUsingCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions(getActivity()))
                {
                    startActivity(new Intent(getContext(), EditImageActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        });

        final ArrayList<String> stringArrayList = new ArrayList<>();
//        addIngredient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (foodIngredient.getText().toString().length()>1)
//                {
//                    String s = foodIngredient.getText().toString();
//                    stringArrayList.add(s);
//                }
//            }
//        });
        return root;
    }
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    public static boolean checkAndRequestPermissions(final Activity context) {
        int ExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }




}