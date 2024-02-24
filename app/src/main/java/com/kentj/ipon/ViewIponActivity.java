package com.kentj.ipon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewIponActivity extends AppCompatActivity {

    private TextView tvHakdog, tvIponDeadline, tvHulogTotal, tvHulogGoal, tvNoHulog;
    private DatabaseHelper databaseHelper;
    private ImageView btnBack, btnAddhulog;

    private List<Hulog> hulogData;
    private HulogAdapter hulogAdapter;
    private ListView hulogList;

    private EditText etSource, etAmount, etDate;

    private int IPON_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ipon);

        tvHakdog = findViewById(R.id.tvPurposeIp);
        tvIponDeadline = findViewById(R.id.tvIponDeadline);
        tvHulogTotal = findViewById(R.id.tvHulogTotal);
        tvHulogGoal = findViewById(R.id.tvHulogGoal);
        tvNoHulog = findViewById(R.id.tvNoHulog);
        databaseHelper = new DatabaseHelper(this);
        btnBack = findViewById(R.id.btnBack);
        btnAddhulog = findViewById(R.id.btnAddhulog);
        hulogList = findViewById(R.id.hulogList);

        hulogData = new ArrayList<>();
        hulogAdapter = new HulogAdapter(this, hulogData);
        hulogList.setAdapter(hulogAdapter);

        Intent intent = getIntent();
        int id = intent.getIntExtra("ipon_id", 0);
        IPON_ID = id;

        Ipon ipon = databaseHelper.get_ipon(id);

        tvHakdog.setText(ipon.getPurpose());
        tvIponDeadline.setText(Utility.convertDate(ipon.getDeadline()));
        tvHulogTotal.setText("₱" + Utility.intcomma(String.valueOf(databaseHelper.getTotalHulog(id))));
        tvHulogGoal.setText("₱" + Utility.intcomma(String.valueOf(ipon.getGoal_amount())));

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
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

                if(etSource.getText().toString().isEmpty() || etSource.getText().toString().isEmpty() || etSource.getText().toString().isEmpty()){
                    return;
                }

                Hulog hulog = new Hulog();
                Ipon ipon = new Ipon();
                ipon.setId(ipon_id);
                hulog.setIpon(ipon);
                hulog.setSource(etSource.getText().toString());
                hulog.setAmount(Double.parseDouble(etAmount.getText().toString()));
                hulog.setDate_added(etDate.getText().toString());

                databaseHelper.insert_hulog(hulog);

                hulogData.add(0, hulog);
                hulogAdapter.notifyDataSetChanged();

                tvHulogTotal.setText("₱" + Utility.intcomma(String.valueOf(databaseHelper.getTotalHulog(IPON_ID))));

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
}