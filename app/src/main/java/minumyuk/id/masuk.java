package minumyuk.id;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class masuk extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_UID = "userUid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.katasandi);
        Button masukButton = findViewById(R.id.masuk);
        Button registerButton = findViewById(R.id.register);
        Button forgotPasswordButton = findViewById(R.id.forgotPassword);

        masukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(masuk.this, daftar.class);
                startActivity(intent);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email diperlukan!.");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Silakan masuk email.");
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(masuk.this, "Email setel ulang kata sandi telah dikirim.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(masuk.this, "Gagal mengirim email setel ulang kata sandi. Silakan periksa alamat email Anda.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void signIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email diperlukan!");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Tolong masukkan email yang benar.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("kata sandi diperlukan!");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(masuk.this, "Login berhasil.", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser(); // Get FirebaseUser object after successful login
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String userUid = auth.getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                        if (user != null) {
                            // Save login status and user UID
                            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_USER_UID, user.getUid()); // Save user UID to SharedPreferences
                            editor.apply();

                            Intent intent = new Intent(masuk.this, beranda.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(masuk.this, "Gagal mendapatkan informasi pengguna.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle login failure
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            emailEditText.setError("Tidak ada akun yang ditemukan dengan email ini.");
                            emailEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            passwordEditText.setError("Kata Sandi tidak sesuai.");
                            passwordEditText.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(masuk.this, "Gagal Masuk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void keregister(View view) {
        Intent intent = new Intent(this, daftar.class);
        startActivity(intent);
        finish();
    }

    public void google(View view) {
        Toast.makeText(masuk.this, "Saat ini belum bisa !", Toast.LENGTH_SHORT).show();
    }
}