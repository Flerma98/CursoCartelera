package com.framgentoestudio.cartelera;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Cartelera extends AppCompatActivity implements PeliculaItemClickListener{

    //FirebaseAuth Auth = FirebaseAuth.getInstance();
    FloatingActionButton fab_Agregar, fab_Salir;
    Dialog Dialogo_Registrarse;
    CircleImageView civ_Pelicula;
    ImageView iv_Portada;
    EditText txtTitulo, txtDirector;
    Button btnRegistrar;
    RecyclerView rv_Pelicula;
    rv_adapter adapter;
    public static ArrayList<Pelicula> listaPeliculas= new ArrayList<>();

    public static Uri uriPerfil;
    public static Uri uriPortada;
    private static final int Perfil = 1;
    private static final int Portada = 2;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Peliculas");

    private StorageReference mStorageRef;

    public static final String EXTRA_PERFIL_IMAGE_TRANSITION_NAME = "perfil_image_transition_name";
    public static final String EXTRA_PORTADA_IMAGE_TRANSITION_NAME = "portada_image_transition_name";
    public static final String EXTRA_TITULO_TEXT_TRANSITION_NAME = "titulo_text_transition_name";
    public static final String EXTRA_DIRECTOR_TEXT_TRANSITION_NAME = "director_text_transition_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cartelera);

        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(null);

        fab_Agregar= findViewById(R.id.fab_Agregar);
        fab_Salir= findViewById(R.id.fab_Salir);

        Animation anim_slide_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_from_right);
        fab_Agregar.setAnimation(anim_slide_right);
        fab_Salir.setAnimation(anim_slide_right);

        rv_Pelicula= findViewById(R.id.rv_peliculas);
        rv_Pelicula.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        adapter = new rv_adapter(listaPeliculas, rv_Pelicula, getApplicationContext(), this);
        rv_Pelicula.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPeliculas.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Pelicula pelicula = objSnaptshot.getValue(Pelicula.class);
                    listaPeliculas.add(pelicula);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*fab_Salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Cartelera.this);
                dialogo1.setTitle("Cerrar Sesión");
                dialogo1.setMessage("¿ Desea cerrar sesión ?");
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Auth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        });*/

        fab_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogo_Registrarse= new Dialog(Cartelera.this);
                Dialogo_Registrarse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Dialogo_Registrarse.setContentView(R.layout.registrar_pelicula);
                Dialogo_Registrarse.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Dialogo_Registrarse.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Dialogo_Registrarse.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                Dialogo_Registrarse.getWindow().setAttributes(lp);
                Dialogo_Registrarse.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                Dialogo_Registrarse.show();

                civ_Pelicula= Dialogo_Registrarse.findViewById(R.id.civ_Pelicula_perfil);
                iv_Portada= Dialogo_Registrarse.findViewById(R.id.iv_Pelicula_portada);
                txtTitulo= Dialogo_Registrarse.findViewById(R.id.txt_Pelicula_Titulo);
                txtDirector= Dialogo_Registrarse.findViewById(R.id.txt_Pelicula_Director);
                btnRegistrar= Dialogo_Registrarse.findViewById(R.id.btn_Pelicula_registrar);

                btnRegistrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(uriPerfil!=null && uriPortada!=null){
                            Pelicula pelicula= new Pelicula();
                            pelicula.setTitulo(txtTitulo.getText().toString().trim());
                            pelicula.setDirector(txtDirector.getText().toString().trim());
                            pelicula.setUID(UUID.randomUUID().toString());

                            //Database
                            myRef.child(pelicula.getUID()).setValue(pelicula);

                            //Storage
                            SubirFoto(pelicula.getUID() + "_Perfil", "Peliculas", uriPerfil);
                            SubirFoto(pelicula.getUID() + "_Portada", "Peliculas", uriPortada);
                        }
                    }
                });

                civ_Pelicula.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirGaleria(Perfil);
                    }
                });

                iv_Portada.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirGaleria(Portada);
                    }
                });
            }
        });
    }

    public void abrirGaleria(int Cual){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"), Cual);
    }

    public void SubirFoto(String Nombre, String Referencia, Uri uri){
        mStorageRef = FirebaseStorage.getInstance().getReference(Referencia);
        StorageReference storageReference = mStorageRef.child( Nombre + ".jpg");
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;
        Uri selectedImage;
        String filePath = null;
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();
                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(selectedImage);
                            } catch (FileNotFoundException e) { e.printStackTrace(); }

                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            if (requestCode == Perfil) {
                                // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                                uriPerfil= (selectedImage);
                                civ_Pelicula.setImageURI(uriPerfil);
                            }
                            if (requestCode == Portada) {
                                // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                                uriPortada= (selectedImage);
                                iv_Portada.setImageURI(uriPortada);
                            }
                        }
                }
    }

    @Override
    public void onPeliculaItemClick(ImageView perfil, ImageView portada, TextView titulo, TextView director) {
        Intent intent = new Intent(Cartelera.this, DatosPelicula.class);
        intent.putExtra(EXTRA_PERFIL_IMAGE_TRANSITION_NAME,  ViewCompat.getTransitionName(perfil));
        intent.putExtra(EXTRA_PORTADA_IMAGE_TRANSITION_NAME,  ViewCompat.getTransitionName(portada));
        intent.putExtra(EXTRA_TITULO_TEXT_TRANSITION_NAME,  ViewCompat.getTransitionName(titulo));
        intent.putExtra(EXTRA_DIRECTOR_TEXT_TRANSITION_NAME,  ViewCompat.getTransitionName(director));
        Pair<View, String> p1 = Pair.create((View)perfil, EXTRA_PERFIL_IMAGE_TRANSITION_NAME);
        Pair<View, String> p2 = Pair.create((View)portada, EXTRA_PORTADA_IMAGE_TRANSITION_NAME);
        Pair<View, String> p3 = Pair.create((View)titulo, EXTRA_TITULO_TEXT_TRANSITION_NAME);
        Pair<View, String> p4 = Pair.create((View)director, EXTRA_DIRECTOR_TEXT_TRANSITION_NAME);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Cartelera.this, p1, p2, p3, p4);
        startActivity(intent, options.toBundle());
    }
}
