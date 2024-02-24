package com.kentj.ipon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HulogAdapter extends BaseAdapter {

    private Context context;
    private List<Hulog> hulogList;
    private LayoutInflater inflater;

    public HulogAdapter(Context context, List<Hulog> hulogList) {
        this.context = context;
        this.hulogList = hulogList;
        this.inflater = LayoutInflater.from(context);
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

        return view;
    }
}
