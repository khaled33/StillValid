package com.stillvalid.asus.stillvalid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 10/07/2018.
 */

public class ContratsAdapter extends RecyclerView.Adapter<ContratsAdapter.ViewHolder>  {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Types,Jours,Id,j;
        RoundedImageView image_contrat;




        public ViewHolder(View itemView) {
            super(itemView);

            Types=itemView.findViewById(R.id.Types);
            Jours=itemView.findViewById(R.id.jourContrat);
            Id=itemView.findViewById(R.id.id);
            image_contrat=itemView.findViewById(R.id.roundedImageView);
            j = itemView.findViewById(R.id.j);

        }
    }
    private Context context;
    private List<Contrats> list;
    public ContratsAdapter(Context context, ArrayList<Contrats> list ){

        this.context=context;
        this.list=list;

    }
    @NonNull
    @Override
    public ContratsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.activity_mes_contrats_recycler,parent,false);

        return new ContratsAdapter.ViewHolder(view);
    }


    int jour=0;

    @Override
    public void onBindViewHolder(@NonNull ContratsAdapter.ViewHolder holder, int position) {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Calendar calendar = Calendar.getInstance();
        Calendar calendar_Inst = Calendar.getInstance();
        Calendar calendar_final = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        calendar_Inst.add(Calendar.DAY_OF_MONTH,-1);

        try {
            String dateEch =list.get(position).getDateEcheance();
            calendar.setTime(sdf.parse(dateEch));
            calendar_final.setTimeInMillis(calendar.getTimeInMillis()-calendar_Inst.getTimeInMillis());
             jour= (int) (calendar_final.getTimeInMillis()/86400000);
            holder.Jours.setText(String.valueOf(jour));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.Types.setText(list.get(position).getType());
        holder.Id.setText(String.valueOf(list.get(position).getId()));
        Picasso.get()
                .load(list.get(position).getPhoto())
                .resize(400,500)
                .into(holder.image_contrat);
        if (jour<=0){
            holder.image_contrat.setBorderColor(Color.RED);
            holder.Jours.setTextColor(Color.RED);
            holder.j.setTextColor(Color.RED);

        }else {
            holder.image_contrat.setBorderColor(Color.parseColor("#358c42"));
            holder.Jours.setTextColor(Color.parseColor("#358c42"));
            holder.j.setTextColor(Color.parseColor("#358c42"));
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
