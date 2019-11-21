package com.stillvalid.asus.stillvalid.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Boutique;
import com.stillvalid.asus.stillvalid.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class BotiqueAdapter extends RecyclerView.Adapter<BotiqueAdapter.ViewHolder>  {

public static class ViewHolder extends RecyclerView.ViewHolder{
    TextView non,ville,prix,id;
    CircleImageView image_produit;
    ImageView editDeleteMenu;

    public ViewHolder(View itemView) {
        super(itemView);
        id=itemView.findViewById(R.id.id);
        non=itemView.findViewById(R.id.nom_produit);
        ville=itemView.findViewById(R.id.ville_produit);
        prix=itemView.findViewById(R.id.prix_produit);
        image_produit=itemView.findViewById(R.id.image_produits);
        editDeleteMenu= itemView.findViewById(R.id.editDeleteMenu);
    }
}
    private Context context;
    private List<Boutique> list;
    public BotiqueAdapter(Context context, ArrayList<Boutique> list ){

        this.context=context;
        this.list=list;

    }
    @NonNull
    @Override
    public BotiqueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.activity_botique_item,parent,false);

        return new BotiqueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        String Id = prefs.getString("ID", null);

        holder.id.setText(String.valueOf(list.get(position).getId()));
        holder.non.setText(list.get(position).getTitre());
        holder.ville.setText(list.get(position).getVille());
        holder.prix.setText(list.get(position).getPrix());
        Picasso.get()
                .load(list.get(position).getImage())
                .resize(400,500)
                .into(holder.image_produit);
        if (list.get(position).getUser_id().equals(Id)){
            holder.editDeleteMenu.setVisibility(View.VISIBLE);
        }else {
            holder.editDeleteMenu.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
