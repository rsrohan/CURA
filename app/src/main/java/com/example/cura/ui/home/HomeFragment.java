package com.example.cura.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.bumptech.glide.Glide;
import com.example.cura.EditImageActivity;
import com.example.cura.R;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Button addFood, addIngredient, analyze, addIngredientUsingCamera;
    EditText foodItem, foodIngredient;
    TextView ingredientsList, IngredientsListMessage;
    ImageView ingredients;
    ProgressBar progressBar;
    private AnyChartView anyChartView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        addFood = root.findViewById(R.id.saveFoodItem);
//        addIngredient = root.findViewById(R.id.addIngredientsButton);
        analyze = root.findViewById(R.id.analyseButton);
//        foodItem = root.findViewById(R.id.foodName);
//        foodIngredient = root.findViewById(R.id.addIngredientsEditText);
//        foodIngredientList = root.findViewById(R.id.ingredientsListToShow);
        addIngredientUsingCamera = root.findViewById(R.id.addIngredientsButtonUsingCamera);
        ingredientsList = root.findViewById(R.id.ingredientList);

        progressBar = root.findViewById(R.id.progress_bar);
        anyChartView = root.findViewById(R.id.any_chart_view);
        String s = requireActivity().getIntent().getStringExtra("ingredients details");


        if (s != null) {
            root.findViewById(R.id.ingredientListMessage).setVisibility(View.VISIBLE);
            try {
                s = s.replace(", ", "\n");
                s = s.toUpperCase();
                try {
                    s = s.substring(s.indexOf("INGRE"));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
                ingredientsList.setText(s);
            } catch (Exception e) {
                Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
                ingredientsList.setText(s);
            }

        } else {
            root.findViewById(R.id.ingredientListMessage).setVisibility(View.GONE);
        }
        addIngredientUsingCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions(getActivity())) {
                    startActivity(new Intent(getContext(), EditImageActivity.class));
                    requireActivity().finish();
                }
            }
        });
        final String urlAdress="";


        final JSONObject data = new JSONObject();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlAdress);
                    Log.d(TAG, "run: " + urlAdress);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.d(TAG, "run: " + data.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(data.toString());
                    os.flush();
                    os.close();

                    final Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = in.read()) >= 0; )
                        sb.append((char) c);
                    final String response = sb.toString();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG123", response);

                    final JSONObject jsonObject = new JSONObject(response);

                    Log.d(TAG, "run: " + jsonObject);

                    conn.disconnect();
                } catch (final Exception e) {
                    e.printStackTrace();

                }


            }
        });

        thread.start();


        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ingredientsList.setVisibility(View.GONE);
                anyChartView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                anyChartView.setProgressBar(progressBar);



                Cartesian cartesian = AnyChart.column();

                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("ingred1", 1));
                data.add(new ValueDataEntry("ingred2", 2));
                data.add(new ValueDataEntry("ingred3", 5));
                data.add(new ValueDataEntry("ingred4", 4));

                Column column = cartesian.column(data);
                column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(5d)
                        .format("number of intakes: {%Value}{groupsSeparator: }");

                cartesian.animation(true);
                cartesian.title("INGREDIENTS CONSUMED BY YOU DURING THIS ALLERGY PERIOD.");

                cartesian.yScale().minimum(0d);

                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.interactivity().hoverMode(HoverMode.BY_X);

                cartesian.xAxis(0).title("INGREDIENT");
                cartesian.yAxis(0).title("FREQUENCY");

                anyChartView.setChart(cartesian);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(getContext(), EditImageActivity.class));
                requireActivity().finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}