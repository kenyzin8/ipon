package com.kentj.ipon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewIponActivity extends AppCompatActivity {

    private TextView tvPurposeIp, tvIponDeadline, tvHulogGoal, tvNoHulog, tvHulogTotal, tvHulogBalance;
    private DatabaseHelper databaseHelper;
    private ImageView btnBack, btnAddhulog, btnSettings;
    private List<Hulog> hulogData;
    private HulogAdapter hulogAdapter;
    private ListView hulogList;
    private EditText etSource, etAmount, etDate, etUpdatePurpose, etUpdateGoal, etUpdateDeadline;
    private CheckBox cbNoDeadline;
    private int IPON_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ipon);

        tvPurposeIp = findViewById(R.id.tvPurposeIp);
        tvIponDeadline = findViewById(R.id.tvIponDeadline);
        tvHulogTotal = findViewById(R.id.tvHulogTotal);
        tvHulogGoal = findViewById(R.id.tvHulogGoal);
        tvNoHulog = findViewById(R.id.tvNoHulog);
        databaseHelper = new DatabaseHelper(this);
        btnBack = findViewById(R.id.btnBack);
        btnAddhulog = findViewById(R.id.btnAddhulog);
        hulogList = findViewById(R.id.hulogList);
        tvHulogBalance = findViewById(R.id.tvHulogBalance);
        btnSettings = findViewById(R.id.btnSettings);

        hulogData = new ArrayList<>();
        hulogAdapter = new HulogAdapter(this, hulogData, this);
        hulogList.setAdapter(hulogAdapter);

        Intent intent = getIntent();
        int id = intent.getIntExtra("ipon_id", 0);
        IPON_ID = id;

        Ipon ipon = databaseHelper.get_ipon(id);

        tvPurposeIp.setText(ipon.getPurpose());

        if(ipon.getDeadline() == null){
            tvIponDeadline.setText("No Deadline");
        }
        else{
            tvIponDeadline.setText(Utility.convertDate(ipon.getDeadline()));
        }

        tvHulogTotal.setText("₱" + Utility.intcomma(String.valueOf(databaseHelper.getTotalHulog(id))));
        tvHulogGoal.setText("₱" + Utility.intcomma(String.valueOf(ipon.getGoal_amount())));

        double balance = ipon.getGoal_amount() - databaseHelper.getTotalHulog(id);
        tvHulogBalance.setText("₱" + Utility.intcomma(String.valueOf(balance)));

        loadHulogData(id);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddhulog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddHulogDialog(id);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ipon ipon = databaseHelper.get_ipon(id);
                showUpdateIponDialog(ipon);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showUpdateDatePickerDialog(View view){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", month + 1, day, year);
                etUpdateDeadline.setText(formattedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    public void showDatePickerDialogHulog(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", month + 1, day, year);
                etDate.setText(formattedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void showUpdateIponDialog(Ipon ipon){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.update_ipon_dialog, null);

        etUpdatePurpose = dialogView.findViewById(R.id.etUpdatePurpose);
        etUpdateGoal = dialogView.findViewById(R.id.etUpdateGoalAmount);
        etUpdateDeadline = dialogView.findViewById(R.id.etUpdateDeadline);
        cbNoDeadline = dialogView.findViewById(R.id.cbNoDeadline);

        Button btnSave = dialogView.findViewById(R.id.btnUpdateSave);
        Button btnCancel = dialogView.findViewById(R.id.btnUpdateCancel);

        etUpdatePurpose.setText(ipon.getPurpose());
        etUpdateGoal.setText(String.valueOf(ipon.getGoal_amount()));

        if(ipon.getDeadline() == null){
            etUpdateDeadline.setText("");
            etUpdateDeadline.setEnabled(false);
            cbNoDeadline.setChecked(true);
        }
        else{
            etUpdateDeadline.setText(ipon.getDeadline());
            etUpdateDeadline.setEnabled(true);
            cbNoDeadline.setChecked(false);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etUpdatePurpose.getText().toString().isEmpty()){
                    etUpdatePurpose.setError("Required Field");
                    return;
                }
                else if(etUpdateGoal.getText().toString().isEmpty()){
                    etUpdateGoal.setError("Required Field");
                    return;
                }

                if(!cbNoDeadline.isChecked()){
                    if(etUpdateDeadline.getText().toString().isEmpty()){
                        etUpdateDeadline.setError("Required Field");
                        return;
                    }
                }

                Ipon ipon = new Ipon();
                ipon.setId(IPON_ID);
                ipon.setPurpose(etUpdatePurpose.getText().toString());
                ipon.setGoal_amount(Double.parseDouble(etUpdateGoal.getText().toString()));

                String deadline = "";

                if(etUpdateDeadline.getText().toString().isEmpty()){
                    deadline = null;
                }
                else{
                    deadline = etUpdateDeadline.getText().toString();
                }

                ipon.setDeadline(deadline);

                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);

                ipon.setDate_added(formattedDate);

                databaseHelper.update_ipon(IPON_ID, ipon);

                bottomSheetDialog.dismiss();

                tvPurposeIp.setText(etUpdatePurpose.getText().toString());

                if(deadline == null){
                    tvIponDeadline.setText("No Deadline");
                }
                else{
                    tvIponDeadline.setText(Utility.convertDate(deadline));
                }

                tvHulogGoal.setText("₱" + Utility.intcomma(etUpdateGoal.getText().toString()));
                updateTextViews();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        cbNoDeadline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etUpdateDeadline.setError(null);
                if(b){
                    etUpdateDeadline.setText("");
                    etUpdateDeadline.setEnabled(false);
                }
                else{
                    if(ipon.getDeadline() == null){
                        etUpdateDeadline.setText("");
                    }
                    else{
                        etUpdateDeadline.setText(ipon.getDeadline());
                    }
                    etUpdateDeadline.setEnabled(true);
                }
            }
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showAddHulogDialog(int ipon_id) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_hulog_dialog, null);

        etSource = dialogView.findViewById(R.id.etSource);
        etAmount = dialogView.findViewById(R.id.etAmount);
        etDate = dialogView.findViewById(R.id.etDate);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        etDate.setText(formattedDate);

        Button btnSave = dialogView.findViewById(R.id.btnSaveHulog);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelHulog);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etSource.getText().toString().isEmpty()){
                    etSource.setError("Required Field");
                    return;
                }
                else if(etAmount.getText().toString().isEmpty()){
                    etAmount.setError("Required Field");
                    return;
                }
                else if(etDate.getText().toString().isEmpty()){
                    etDate.setError("Required Field");
                    return;
                }

                Hulog hulog = new Hulog();
                Ipon ipon = new Ipon();
                ipon.setId(ipon_id);

                hulog.setIpon(ipon);
                hulog.setSource(etSource.getText().toString());
                hulog.setAmount(Double.parseDouble(etAmount.getText().toString()));
                hulog.setDate_added(etDate.getText().toString());

                hulog.setId(Integer.parseInt(String.valueOf(databaseHelper.insert_hulog(hulog))));

                loadHulogData(ipon_id);

                updateTextViews();

                tvNoHulog.setVisibility(View.INVISIBLE);

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

    private void loadHulogData(int ipon_id) {
        hulogData.clear();
        hulogData.addAll(databaseHelper.get_all_hulog(ipon_id));

        if(hulogData.size() == 0){
            tvNoHulog.setVisibility(View.VISIBLE);
        }

        hulogAdapter.notifyDataSetChanged();
    }

    public void updateTextViews(){
        DatabaseHelper db = new DatabaseHelper(this);
        Ipon ipon = db.get_ipon(IPON_ID);

        tvHulogTotal.setText("₱" + Utility.intcomma(String.valueOf(db.getTotalHulog(IPON_ID))));
        double balance = ipon.getGoal_amount() - db.getTotalHulog(IPON_ID);
        tvHulogBalance.setText("₱" + Utility.intcomma(String.valueOf(balance)));
    }
}