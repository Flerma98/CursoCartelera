package com.framgentoestudio.cartelera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class rv_adapter extends RecyclerView.Adapter<rv_adapter.Peliculasviewholder> implements View.OnClickListener {

    ArrayList<Pelicula> Peliculas;
    View view;
    private View.OnClickListener listener;
    Context contexto;
    private final PeliculaItemClickListener onPeliculaClickListener;

    public rv_adapter(ArrayList<Pelicula> peliculas, View view, Context context, PeliculaItemClickListener onpeliculaClickListener) {
        Peliculas = peliculas;
        this.view = view;
        contexto = context;
        onPeliculaClickListener = onpeliculaClickListener;
    }

    @NonNull
    @Override
    public Peliculasviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_pelicula, viewGroup, false);
        Peliculasviewholder holder= new Peliculasviewholder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Peliculasviewholder peliculasviewholder, int i) {
        view= peliculasviewholder.itemView;
        final Pelicula pelicula= Peliculas.get(i);
        peliculasviewholder.txtTitulo.setText(pelicula.getTitulo());
        peliculasviewholder.txtDirector.setText(pelicula.getDirector());
        //peliculasviewholder.civ_Perfil.setImageBitmap(DescargarImagen("Pelicula", pelicula.getUID() + "_Perfil"));
        //peliculasviewholder.iv_Portada.setImageBitmap(DescargarImagen("Pelicula", pelicula.getUID() + "_Portada"));

        ViewCompat.setTransitionName(peliculasviewholder.civ_Perfil, Cartelera.EXTRA_PERFIL_IMAGE_TRANSITION_NAME);
        ViewCompat.setTransitionName(peliculasviewholder.iv_Portada, Cartelera.EXTRA_PORTADA_IMAGE_TRANSITION_NAME);
        ViewCompat.setTransitionName(peliculasviewholder.txtTitulo, Cartelera.EXTRA_TITULO_TEXT_TRANSITION_NAME);
        ViewCompat.setTransitionName(peliculasviewholder.txtDirector, Cartelera.EXTRA_DIRECTOR_TEXT_TRANSITION_NAME);

        peliculasviewholder.btnVermas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatosPelicula.pelicula= pelicula;
                onPeliculaClickListener.onPeliculaItemClick(peliculasviewholder.civ_Perfil, peliculasviewholder.iv_Portada, peliculasviewholder.txtTitulo, peliculasviewholder.txtDirector);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Peliculas.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener= listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class Peliculasviewholder extends RecyclerView.ViewHolder{
        CircleImageView civ_Perfil;
        ImageView iv_Portada;
        TextView txtTitulo, txtDirector;
        Button btnVermas;
        public Peliculasviewholder(@NonNull View itemView) {
            super(itemView);
            civ_Perfil= itemView.findViewById(R.id.rv_perfil);
            iv_Portada= itemView.findViewById(R.id.rv_portada);
            txtTitulo= itemView.findViewById(R.id.rv_Titulo);
            txtDirector= itemView.findViewById(R.id.rv_Director);
            btnVermas= itemView.findViewById(R.id.btn_rv_Vermas);
        }
    }

    public void DescargarImagen(String Referencia, String Nombre, final ImageView imageView){
        final Bitmap[] imagen = {null};
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.
                getReferenceFromUrl("gs://agronodo-fragmentoestudio.appspot.com/" + Referencia).child(Nombre + ".jpg");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}
    }
}

