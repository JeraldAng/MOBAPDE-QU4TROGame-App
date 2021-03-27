package com.example.qu4trogame;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RecordList extends ArrayAdapter<Record> {
    private Activity context;
    List<Record> records;

    public RecordList(Activity context, List<Record> records) {
        super(context, R.layout.layout_record_list, records);
        this.context = context;
        this.records = records;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_record_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewWins = (TextView) listViewItem.findViewById(R.id.textViewWins);
        TextView textViewLosses = (TextView) listViewItem.findViewById(R.id.textViewLosses);

        Record record = records.get(position);
        textViewName.setText(record.getUsername());
        textViewWins.setText(record.getWins());
        textViewLosses.setText(String.valueOf(record.getLosses()));

        return listViewItem;
    }
}