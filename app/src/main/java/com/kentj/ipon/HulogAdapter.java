package com.kentj.ipon;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HulogAdapter extends BaseAdapter {

    private Context context;
    private List<Hulog> hulogList;
    private LayoutInflater inflater;
    private ViewIponActivity via;

    public HulogAdapter(Context context, List<Hulog> hulogList, ViewIponActivity via) {
        this.context = context;
        this.hulogList = hulogList;
        this.inflater = LayoutInflater.from(context);
        this.via = via;
    }

    @Override
    public int getCount() {
        return hulogList.size();
    }

    @Override
    public Hulog getItem(int i) {
        return hulogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            hulogList.remove(position);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.hulog_list_item, viewGroup, false);
        }

        TextView tvSource, tvDateAdded, tvAmount;
        tvSource = view.findViewById(R.id.tvSource);
        tvDateAdded = view.findViewById(R.id.tvDateAdded);
        tvAmount = view.findViewById(R.id.tvAmount);

        Hulog hulog = getItem(i);

        tvSource.setText(hulog.getSource());

        tvDateAdded.setText(Utility.convertDate(hulog.getDate_added()));

        tvAmount.setText("₱" + Utility.intcomma(String.valueOf(hulog.getAmount())));

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_confirmation_dialog, null);
                builder.setView(dialogView);

                TextView tvConfirmationMessage = dialogView.findViewById(R.id.tvConfirmationMessage);
                TextView tvConfirmationPurpose = dialogView.findViewById(R.id.tvConfirmationPurpose);
                tvConfirmationMessage.setText("Are you sure you want to delete this item?");
                tvConfirmationPurpose.setText(hulog.getSource() + " · ₱" + Utility.intcomma(String.valueOf(hulog.getAmount())));

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button btnYes = dialogView.findViewById(R.id.btnYes);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper db = new DatabaseHelper(context);
                        db.delete_hulog(hulog.getId());

                        removeItem(i);

                        notifyDataSetChanged();

                        via.updateTextViews();

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

        return view;
    }
}
