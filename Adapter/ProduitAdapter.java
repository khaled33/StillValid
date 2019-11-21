package com.stillvalid.asus.stillvalid.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Produit;
import com.stillvalid.asus.stillvalid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    private int jour=0;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jourArticle, nomArticle, id,j;
        CircleImageView image_produit;

        public ViewHolder(View itemView) {
            super(itemView);

            jourArticle = itemView.findViewById(R.id.jourArticle);
            j = itemView.findViewById(R.id.j);
            id = itemView.findViewById(R.id.id);
            nomArticle = itemView.findViewById(R.id.enseigneArticle);
            image_produit = itemView.findViewById(R.id.image_produit);

        }
    }

    private Context context;
    private List<Produit> list;

    public ProduitAdapter(Context context, ArrayList<Produit> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ProduitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_mes_produits_recycler, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ProduitAdapter.ViewHolder holder, int position) {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Calendar calendar = Calendar.getInstance();
        Calendar calendar_Inst = Calendar.getInstance();
        Calendar calendar_final = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH,-1);
        calendar_Inst.add(Calendar.DAY_OF_MONTH,-1);
        try {
            String dateFin =list.get(position).getDateFin();
            calendar.setTime(sdf.parse(dateFin));
            calendar_final.setTimeInMillis(calendar.getTimeInMillis()-calendar_Inst.getTimeInMillis());
             jour= (int) (calendar_final.getTimeInMillis()/86400000);
            holder.jourArticle.setText(String.valueOf(jour));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.nomArticle.setText(list.get(position).getNom());
        holder.id.setText(String.valueOf(list.get(position).getId()));


        Picasso.get()
                .load(list.get(position).getPhoto())
                .resize(400,500)
                .into(holder.image_produit);
if (jour<=0){
    holder.image_produit.setBorderColor(Color.RED);
    holder.jourArticle.setTextColor(Color.RED);
    holder.j.setTextColor(Color.RED);
}else {
    holder.image_produit.setBorderColor(Color.parseColor("#358c42"));
    holder.jourArticle.setTextColor(Color.parseColor("#358c42"));
    holder.j.setTextColor(Color.parseColor("#358c42"));
}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
