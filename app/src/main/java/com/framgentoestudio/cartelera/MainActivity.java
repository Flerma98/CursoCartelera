package com.framgentoestudio.cartelera;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth Auth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    Button btnIniciar_Sesion, btnRegistrarse, btnRegistrarse_registrar;
    EditText txtCorreo, txtContraseña, txtRegistrarse_Nombre, txtRegistrarse_Correo, txtRegistrarse_Contraseña;
    Dialog Dialogo_Registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        btnIniciar_Sesion= findViewById(R.id.btn_login_Iniciar_Sesion);
        txtCorreo= findViewById(R.id.txt_login_Correo);
        txtContraseña= findViewById(R.id.txt_login_Contraseña);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    try {
                            Intent intent = new Intent(MainActivity.this, Cartelera.class);
                            startActivity(intent);
                            finish();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        btnIniciar_Sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtCorreo.getWindowToken(), 0);
                try {
                    Auth.signInWithEmailAndPassword(txtCorreo.getText().toString(), txtContraseña.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                            } else {
                                Toast.makeText(MainActivity.this, "No existe una cuenta con esos datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogo_Registrarse= new Dialog(MainActivity.this);
                Dialogo_Registrarse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Dialogo_Registrarse.setContentView(R.layout.registrar_usuario);
                Dialogo_Registrarse.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Dialogo_Registrarse.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Dialogo_Registrarse.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                Dialogo_Registrarse.getWindow().setAttributes(lp);
                Dialogo_Registrarse.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                Dialogo_Registrarse.show();

                txtRegistrarse_Nombre= Dialogo_Registrarse.findViewById(R.id.txt_Registrar_Nombre);
                txtRegistrarse_Correo= Dialogo_Registrarse.findViewById(R.id.txt_Registrar_Correo);
                txtRegistrarse_Contraseña= Dialogo_Registrarse.findViewById(R.id.txt_Registrar_Contraseña);
                btnRegistrarse_registrar= Dialogo_Registrarse.findViewById(R.id.btn_Registrar_registrar);

                btnRegistrarse_registrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Auth.createUserWithEmailAndPassword(txtRegistrarse_Correo.getText().toString().trim(), txtRegistrarse_Contraseña.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Usuario Registrado Exitosamente", Toast.LENGTH_SHORT).show();
                                        Dialogo_Registrarse.dismiss();
                                        finish();
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(getApplicationContext(), "Usuario ya en uso", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }catch (Exception e){}
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Auth.addAuthStateListener(firebaseAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Auth.removeAuthStateListener(firebaseAuthListener);
    }
}
