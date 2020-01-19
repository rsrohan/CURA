package com.example.cura.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cura.CameraActivity;
import com.example.cura.R;
import com.example.cura.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Button addFood, addIngredient, analyze, addIngredientUsingCamera;
    EditText foodItem, foodIngredient;
    TextView foodIngredientList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        addFood = root.findViewById(R.id.saveFoodItem);
        addIngredient = root.findViewById(R.id.addIngredientsButton);
        analyze = root.findViewById(R.id.analyseButton);
        foodItem = root.findViewById(R.id.foodName);
        foodIngredient = root.findViewById(R.id.addIngredientsEditText);
        foodIngredientList = root.findViewById(R.id.ingredientsListToShow);
        addIngredientUsingCamera = root.findViewById(R.id.addIngredientsButtonUsingCamera);

        addIngredientUsingCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions(getActivity()))
                {
                    startActivity(new Intent(getContext(), CameraActivity.class));
                }
            }
        });

        final ArrayList<String> stringArrayList = new ArrayList<>();
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodIngredient.getText().toString().length()>1)
                {
                    String s = foodIngredient.getText().toString();
                    stringArrayList.add(s);
                }
            }
        });
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