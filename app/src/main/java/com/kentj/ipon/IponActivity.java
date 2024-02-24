package com.kentj.ipon;

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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
        tvTotal.setText("₱" + Utility.intcomma(String.valueOf(databaseHelper.get_all_ipon_amount())));

        btnAddIpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIponDialog();
            }
        });
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

                if(etPurpose.getText().toString().isEmpty() || etGoalAmount.getText().toString().isEmpty()) {
                    return;
                }

                double bal = Double.parseDouble(etGoalAmount.getText().toString());

                Ipon ip = new Ipon();
                ip.setPurpose(etPurpose.getText().toString());
                ip.setGoal_amount(bal);

                String deadline = "";

                if(etDeadline.getText().toString().isEmpty()){
                    deadline = "00/00/0000";
                }
                else{
                    deadline = etDeadline.getText().toString();
                }

                ip.setDeadline(deadline);
                ip.setBalance(bal);

                ip.setId(Integer.parseInt(String.valueOf(databaseHelper.insert_ipon(ip))));

                iponData.add(0, ip);
                iponAdapter.notifyDataSetChanged();

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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void loadIponData() {
        iponData.clear();
        iponData.addAll(databaseHelper.get_all_ipon());
        iponAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}