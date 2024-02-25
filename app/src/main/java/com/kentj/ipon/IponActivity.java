package com.kentj.ipon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class IponActivity extends AppCompatActivity {

    private ListView iponList;
    private ImageView btnAddIpon;
    private DatabaseHelper databaseHelper;
    private List<Ipon> iponData;
    private IponAdapter iponAdapter;

    private EditText etPurpose;
    private EditText etGoalAmount;
    private EditText etDeadline;
    private TextView tvTotal;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipon);

        iponList = findViewById(R.id.iponList);
        btnAddIpon = findViewById(R.id.btnAddIpon);
        databaseHelper = new DatabaseHelper(this);
        iponData = new ArrayList<>();
        iponAdapter = new IponAdapter(this, iponData);
        iponList.setAdapter(iponAdapter);

        tvTotal = findViewById(R.id.tvTotal);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

//        Ipon ipon = new Ipon();
//        ipon.setPurpose("hakdog");
//        ipon.setGoal_amount(12.2);
//        ipon.setDeadline("05/05/2001");
//
//        for(int i = 0; i < 3; i++)
//            databaseHelper.insert_ipon(ipon);

//            Ipon ipon = new Ipon();
//            ipon.setId(2);
//
//            Hulog hulog = new Hulog();
//            hulog.setIpon(ipon);
//            hulog.setAmount(2.3);
//            hulog.setSource("EastWest");
//            hulog.setDate_added("05/05/2001");
//
//            databaseHelper.insert_hulog(hulog);

        loadIponData();

        btnAddIpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIponDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIponData();
    }

    private void showAddIponDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_ipon_dialog, null);

        etPurpose = dialogView.findViewById(R.id.etPurpose);
        etGoalAmount = dialogView.findViewById(R.id.etGoalAmount);
        etDeadline = dialogView.findViewById(R.id.etDeadline);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etPurpose.getText().toString().isEmpty()){
                    etPurpose.setError("Required Field");
                    return;
                }
                else if(etGoalAmount.getText().toString().isEmpty()){
                    etGoalAmount.setError("Required Field");
                    return;
                }

                double bal = Double.parseDouble(etGoalAmount.getText().toString());

                Ipon ip = new Ipon();
                ip.setPurpose(etPurpose.getText().toString());
                ip.setGoal_amount(bal);

                String deadline = "";

                if(etDeadline.getText().toString().isEmpty()){
                    deadline = null;
                }
                else{
                    deadline = etDeadline.getText().toString();
                }

                ip.setDeadline(deadline);
                ip.setBalance(bal);

                ip.setId(Integer.parseInt(String.valueOf(databaseHelper.insert_ipon(ip))));

                loadIponData();

                bottomSheetDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    public void showDatePickerDialog(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", month + 1, day, year);
                etDeadline.setText(formattedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void loadIponData() {
        iponData.clear();
        iponData.addAll(databaseHelper.get_all_ipon());
        tvTotal.setText("₱" + Utility.intcomma(String.valueOf(databaseHelper.get_all_ipon_amount())));
        iponAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}