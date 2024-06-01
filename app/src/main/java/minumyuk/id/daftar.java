package minumyuk.id;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class daftar extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, verifyPasswordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.katasandi);
        verifyPasswordEditText = findViewById(R.id.verifikasisandi);
        Button daftarButton = findViewById(R.id.daftar);

        daftarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String verifyPassword = verifyPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email diperlukan.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Tolong masukkan email yang benar.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Kata sandi dibutuhkan.");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Kata sandi minimal 6 karakter.");
            return;
        }

        if (!password.equals(verifyPassword)) {
            verifyPasswordEditText.setError("Sandi tidak sesuai.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String userUid = auth.getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userUid);
                        Toast.makeText(daftar.this, "Akun berhasil dibuat.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(daftar.this, r1.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            passwordEditText.setError("kata sandi lemah.");
                            passwordEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            emailEditText.setError("Email tidak valid.");
                            emailEditText.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailEditText.setError("Email sudah terdaftar.");
                            emailEditText.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(daftar.this, "Otentikasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void kelogin(android.view.View view) {
        Intent intent = new Intent(this, masuk.class);
        startActivity(intent);
        finish();
    }
    public void google(View view) {
        Toast.makeText(daftar.this, "Saat ini belum bisa !", Toast.LENGTH_SHORT).show();
    }
}