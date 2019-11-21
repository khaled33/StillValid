package com.stillvalid.asus.stillvalid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Papiers;
import com.stillvalid.asus.stillvalid.R;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Papiers> list;

    public ImageAdapter(Context context, List<Papiers> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = inflater.inflate(R.layout.grid_item, null);
        TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

        textView.setText(String.valueOf(list.get(i).getId()));
        Picasso.get()
                .load(list.get(i).getPath())
                .resize(400,500)
                .into(imageView);

        return gridView;
    }


}
