package com.example.diariodemascotas.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diariodemascotas.R;
import com.example.diariodemascotas.models.DiarioMascotaModel;

import java.util.List;

public class NotasDiarioAdapter extends RecyclerView.Adapter<NotasDiarioAdapter.ViewHolderNotas> {

    List<DiarioMascotaModel> listaNotas;

    public interface OnClickListener
    {
        void eliminarItem(int position);
        void editarItem(int id);
    }

    OnClickListener onClickListener;

    public NotasDiarioAdapter(List<DiarioMascotaModel> listaNotas, OnClickListener onClickListener) {
        this.listaNotas = listaNotas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolderNotas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_notasdiario, parent, false);
        return new ViewHolderNotas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotas holder, int position) {
        DiarioMascotaModel nota = listaNotas.get(position);
        holder.txtFecha.setText(nota.getFecha().toString());
        holder.txtTitulo.setText(nota.getTitulo());
        holder.txtNota.setText(nota.getNota());

        if (nota.getUriImage() != null) {

            Uri uri = Uri.parse(nota.getUriImage());
            holder.imgMascota.setImageURI(uri);
        }else {
            holder.imgMascota.setImageResource(nota.getPathImagen());
        }
        if(nota.isFavorito()){
            holder.imgFavorito.setVisibility(View.VISIBLE);
        }else{
            holder.imgFavorito.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public class ViewHolderNotas extends RecyclerView.ViewHolder {
        TextView txtFecha, txtTitulo, txtNota;
        ImageView imgMascota, imgFavorito;
        Button btnEditar, btnEliminar;
        public ViewHolderNotas(@NonNull View itemView) {
            super(itemView);

            txtFecha = itemView.findViewById(R.id.fecha_registro);
            txtTitulo = itemView.findViewById(R.id.titulo);
            txtNota = itemView.findViewById(R.id.nota);
            imgMascota = itemView.findViewById(R.id.imagen);
            imgFavorito = itemView.findViewById(R.id.icon_favorite);


            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);

            btnEditar.setOnClickListener(view -> {
                int id = listaNotas.get(getAdapterPosition()).getId();
                onClickListener.editarItem(id);

            });

            btnEliminar.setOnClickListener(view -> {
                onClickListener.eliminarItem(getAdapterPosition());
            });
        }
    }
}
