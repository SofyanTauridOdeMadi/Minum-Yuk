package minumyuk.id;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_UID = "userUid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Membuat Handler untuk menunda eksekusi
        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

            Intent intent;
            if (isLoggedIn) {
                // Jika pengguna sudah login, arahkan ke halaman Beranda
                intent = new Intent(MainActivity.this, beranda.class);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userUid = auth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userUid);
            } else {
                // Jika pengguna belum login, arahkan ke halaman Masuk
                intent = new Intent(MainActivity.this, masuk.class);
            }

            // Periksa apakah ada user UID yang tersimpan
            String userUid = sharedPreferences.getString(KEY_USER_UID, null);
            if (userUid != null) {
                // Jika ada, kirimkan user UID ke aktivitas berikutnya
                intent.putExtra(KEY_USER_UID, userUid);
            }

            startActivity(intent);
            finish();
            overridePendingTransition(R.xml.zoom_in, R.xml.zoom_out);
        }, 1500);
    }
}
