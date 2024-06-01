package minumyuk.id;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class pengaturan_akun extends AppCompatActivity {

    private EditText etName;
    private SeekBar seekBarAge, seekBarWeight, seekBarHeight, seekBarActivityLevel, seekBarSeason;
    private TextView tvAge, tvWeight, tvHeight, tvActivityLevel, tvSeason;
    private Button btnSave;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_akun);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Pengguna").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "Silakan Masuk Kembali", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etName);
        seekBarAge = findViewById(R.id.seekBarAge);
        seekBarWeight = findViewById(R.id.seekBarWeight);
        seekBarHeight = findViewById(R.id.seekBarHeight);
        seekBarActivityLevel = findViewById(R.id.seekBarActivityLevel);
        seekBarSeason = findViewById(R.id.seekBarSeason);

        tvAge = findViewById(R.id.tvAge);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvActivityLevel = findViewById(R.id.tvActivityLevel);
        tvSeason = findViewById(R.id.tvSeason);
        btnSave = findViewById(R.id.btnSave);

        sharedPreferences = getSharedPreferences("Pengguna", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadUserData();

        btnSave.setOnClickListener(v -> saveUserData());
        setupSeekBarListeners();
    }

    private void setupSeekBarListeners() {
        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAge.setText("Usia saat ini: " + progress + " Tahun");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWeight.setText("Berat badan: " + progress + " Kg");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvHeight.setText("Tinggi badan: " + progress + " Cm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarActivityLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvActivityLevel.setText("Aktivitas: " + getActivityLevel(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSeason.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeason.setText("Musim: " + getSeason(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void saveUserData() {
        String name = etName.getText().toString().trim();

        // Validasi input nama (tidak boleh kosong)
        if (name.isEmpty()) {
            Toast.makeText(pengaturan_akun.this, "Isikan nama terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = seekBarAge.getProgress();
        int weight = seekBarWeight.getProgress();
        int height = seekBarHeight.getProgress();
        int activityLevel = seekBarActivityLevel.getProgress();
        int season = seekBarSeason.getProgress();

        // Mendapatkan referensi pengguna saat ini (pastikan pengguna sudah login)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId);

        // Menyimpan data pengguna secara individual ke Firebase
        userRef.child("nama").setValue(name);
        userRef.child("umur").setValue(age);
        userRef.child("beratBadan").setValue(weight);
        userRef.child("tinggiBadan").setValue(height);
        userRef.child("aktivitas").setValue(activityLevel);
        userRef.child("musim").setValue(season);

        // Menyimpan data pengguna ke SharedPreferences
        editor.putString("nama", name);
        editor.putInt("umur", age);
        editor.putInt("beratBadan", weight);
        editor.putInt("tinggiBadan", height);
        editor.putInt("aktivitas", activityLevel);
        editor.putInt("musim", season);
        editor.apply();

        Toast.makeText(pengaturan_akun.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(pengaturan_akun.this, pengaturan.class);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        // Mengambil data dari SharedPreferences
        String name = sharedPreferences.getString("nama", "");
        int age = sharedPreferences.getInt("umur", 0);
        int weight = sharedPreferences.getInt("beratBadan", 0);
        int height = sharedPreferences.getInt("tinggiBadan", 0);
        int activityLevel = sharedPreferences.getInt("aktivitas", 0);
        int season = sharedPreferences.getInt("musim", 0);

        // Mengisi data ke view
        etName.setText(name);
        seekBarAge.setProgress(age);
        seekBarWeight.setProgress(weight);
        seekBarHeight.setProgress(height);
        seekBarActivityLevel.setProgress(activityLevel);
        seekBarSeason.setProgress(season);

        tvAge.setText("Usia saat ini: " + age + " Tahun");
        tvWeight.setText("Berat badan: " + weight + " Kg");
        tvHeight.setText("Tinggi badan: " + height + " Cm");
        tvActivityLevel.setText("Aktivitas: " + getActivityLevel(activityLevel));
        tvSeason.setText("Musim: " + getSeason(season));
    }

    private String getActivityLevel(int index) {
        switch (index) {
            case 0:
                return "Kurang aktif";
            case 1:
                return "Cukup aktif";
            case 2:
                return "Aktif";
            case 3:
                return "Sangat aktif";
            default:
                return "";
        }
    }

    private String getSeason(int index) {
        switch (index) {
            case 0:
                return "Semi";
            case 1:
                return "Panas";
            case 2:
                return "Gugur";
            case 3:
                return "Dingin";
            default:
                return "";
        }
    }
}