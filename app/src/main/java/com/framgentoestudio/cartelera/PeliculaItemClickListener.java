package com.framgentoestudio.cartelera;

import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public interface PeliculaItemClickListener {
    void onPeliculaItemClick(ImageView perfil, ImageView portada, TextView titulo, TextView director);
}
