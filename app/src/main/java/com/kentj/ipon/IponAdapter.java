package com.kentj.ipon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kentj.ipon.Ipon;
import com.kentj.ipon.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IponAdapter extends BaseAdapter {

    private Context context;
    private List<Ipon> iponList;
    private LayoutInflater inflater;

    public IponAdapter(Context context, List<Ipon> iponList) {
        this.context = context;
        this.iponList = iponList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return iponList.size();
    }

    @Override
    public Ipon getItem(int i) {
        return iponList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            iponList.remove(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ipon_list_item, parent, false);
        }

        TextView tvPurpose = convertView.findViewById(R.id.tvPurpose);
        TextView tvGoal = convertView.findViewById(R.id.tvGoal);
        TextView tvDeadline = convertView.findViewById(R.id.tvDeadline);
        TextView tvBalance = convertView.findViewById(R.id.tvBalance);

        Ipon ipon = getItem(position);

        if(ipon.getDeadline().equals("00/00/0000")){
            tvDeadline.setText("No Deadline");
        }
        else{
            tvDeadline.setText(Utility.convertDate(ipon.getDeadline()));
        }

        tvPurpose.setText(ipon.getPurpose());
        tvGoal.setText("₱" + Utility.intcomma(String.valueOf(ipon.getGoal_amount())));
        tvBalance.setText("₱" + Utility.intcomma(String.valueOf(ipon.getBalance())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ipon ipon = getItem(position);
                Intent intent = new Intent(context, ViewIponActivity.class);
                intent.putExtra("ipon_id", ipon.getId());
                context.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Ipon ipon = getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_confirmation_dialog, null);
                builder.setView(dialogView);

                TextView tvConfirmationMessage = dialogView.findViewById(R.id.tvConfirmationMessage);
                TextView tvConfirmationPurpose = dialogView.findViewById(R.id.tvConfirmationPurpose);
                tvConfirmationMessage.setText("Are you sure you want to delete this item?");
                tvConfirmationPurpose.setText(ipon.getPurpose());

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button btnYes = dialogView.findViewById(R.id.btnYes);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper db = new DatabaseHelper(context);
                        db.delete_ipon(ipon.getId());

                        removeItem(position);
                        notifyDataSetChanged();

                        alertDialog.dismiss();
                    }
                });

                Button btnNo = dialogView.findViewById(R.id.btnNo);
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                return true;
            }
        });

        return convertView;
    }
}