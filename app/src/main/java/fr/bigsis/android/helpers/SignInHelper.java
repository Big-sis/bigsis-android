package fr.bigsis.android.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import fr.bigsis.android.R;

public class SignInHelper {

    public static void verifyEmailSignIn(EditText emailBox, EditText passwordBox, Context context, RelativeLayout relativeLayout) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        AlertDialog builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                .setTitle("Un e-mail de vérification vous a déjà été envoyé")
                .setMessage("Renvoyer un autre e-mail pour vérification ?")
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, null)
                .show();
        emailBox.setText("");
        passwordBox.setText("");
        Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                    .setMessage("Un e-mail de vérification vient de vous etre envoyé")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            Snackbar.make(relativeLayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });
    }


}
