package com.framgentoestudio.cartelera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class DatosPelicula extends AppCompatActivity {

    public static Pelicula pelicula;
    CircleImageView civPerfil;
    ImageView ivPortada;
    TextView txtTitulo, txtDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_pelicula);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportStartPostponedEnterTransition();

        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        civPerfil= findViewById(R.id.civ_Datos_Perfil);
        ivPortada= findViewById(R.id.iv_Datos_Portada);
        txtTitulo= findViewById(R.id.txt_Datos_Titulo);
        txtDirector= findViewById(R.id.txt_Datos_Director);

        Bundle extras = getIntent().getExtras();

        txtTitulo.setText(pelicula.getTitulo());
        txtDirector.setText(pelicula.getDirector());
        DescargarImagen("Peliculas", pelicula.getUID() + "_Perfil", civPerfil);
        DescargarImagen("Peliculas", pelicula.getUID() + "_Portada", ivPortada);

        String perfilTransitionName = extras.getString(Cartelera.EXTRA_PERFIL_IMAGE_TRANSITION_NAME);
        civPerfil.setTransitionName(perfilTransitionName);

        String portadaTransitionName = extras.getString(Cartelera.EXTRA_PORTADA_IMAGE_TRANSITION_NAME);
        ivPortada.setTransitionName(portadaTransitionName);

        String tituloTransitionName = extras.getString(Cartelera.EXTRA_TITULO_TEXT_TRANSITION_NAME);
        txtTitulo.setTransitionName(tituloTransitionName);

        String directorTransitionName = extras.getString(Cartelera.EXTRA_TITULO_TEXT_TRANSITION_NAME);
        txtDirector.setTransitionName(directorTransitionName);
    }

    public void DescargarImagen(String Referencia, String Nombre, final ImageView imageView){
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
