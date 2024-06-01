package minumyuk.id;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class beranda extends AppCompatActivity {

    private TextView tanggalTextView, waktuTextView, progressText, usernameTextView;
    private ProgressBar seekBar;
    private int jumlahAir = 0;
    private final int totalAir = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        tanggalTextView = findViewById(R.id.tanggal);
        waktuTextView = findViewById(R.id.waktu);
        seekBar = findViewById(R.id.seekBar);
        progressText = findViewById(R.id.progressText);
        usernameTextView = findViewById(R.id.username);

        Button kustomButton = findViewById(R.id.kustom);
        Button ml250Button = findViewById(R.id.ml_250);
        Button ml500Button = findViewById(R.id.ml_500);
        Button kembaliButton = findViewById(R.id.kembali);
        Button bagikanButton = findViewById(R.id.tombolBagikan);

        seekBar.setEnabled(false);

        updateTanggalWaktu();
        fetchUsername();
        loadAirData();

        kustomButton.setOnClickListener(v -> showCustomInputDialog());
        ml250Button.setOnClickListener(v -> masukkan250ml());
        ml500Button.setOnClickListener(v -> masukkan500ml());
        kembaliButton.setOnClickListener(v -> hapusKembali());
    }

    private void updateTanggalWaktu() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String tanggal = dateFormat.format(new Date());
        String waktu = timeFormat.format(new Date());

        tanggalTextView.setText(tanggal);
        waktuTextView.setText(waktu);
    }

    private void fetchUsername() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId).child("nama");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.getValue(String.class);
                    if (username != null) {
                        usernameTextView.setText(username);
                    } else {
                        usernameTextView.setText("Nama Tidak Ditemukan");
                    }
                } else {
                    usernameTextView.setText("Nama Tidak Ditemukan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(beranda.this, "Gagal memuat nama", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAirData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference riwayatAirRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId).child("riwayat_air");
        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        riwayatAirRef.child(tanggal).child("air_konsumsi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    jumlahAir = snapshot.getValue(Integer.class);
                    updateProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(beranda.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAirData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference riwayatAirRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId).child("riwayat_air");
        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        riwayatAirRef.child(tanggal).child("air_konsumsi").setValue(jumlahAir);
    }

    private void showCustomInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan Jumlah Air (ml)");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                if (!inputText.isEmpty()) {
                    int ml = Integer.parseInt(inputText);
                    masukkanKustom(ml);
                } else {
                    Toast.makeText(beranda.this, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void masukkanKustom(int ml) {
        jumlahAir += ml;
        updateProgress();
        saveAirData();
    }

    private void masukkan250ml() {
        jumlahAir += 250;
        updateProgress();
        saveAirData();
    }

    private void masukkan500ml() {
        jumlahAir += 500;
        updateProgress();
        saveAirData();
    }

    private void hapusKembali() {
        if (jumlahAir > 0) {
            jumlahAir -= 250;
            if (jumlahAir < 0) {
                jumlahAir = 0;
            }
            updateProgress();
            saveAirData();
        } else {
            Toast.makeText(this, "Tidak ada yang riwayat.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress() {
        seekBar.setProgress(jumlahAir);
        int percent = (jumlahAir * 100) / totalAir;
        progressText.setText(String.format(Locale.getDefault(), "%d/%d ml (%d%%)", jumlahAir, totalAir, percent));

        // Panggil method untuk menampilkan dialog peringatan setelah delay 3 detik
        if (jumlahAir >= totalAir) {
            showCongratulationsDialog();
        }
    }

    private void reloadAndUploadDataFromSharedPreferences() {
        // Load data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Pengguna", MODE_PRIVATE);
        int age = sharedPreferences.getInt("umur", 0);
        int weight = sharedPreferences.getInt("beratBadan", 0);
        int height = sharedPreferences.getInt("tinggiBadan", 0);
        int activityLevel = sharedPreferences.getInt("aktivitas", 0);
        int season = sharedPreferences.getInt("musim", 0);
        int totalKonsumsi = sharedPreferences.getInt("total_konsumsi", 0);

        // Upload data to Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId);
        userRef.child("umur").setValue(age);
        userRef.child("beratBadan").setValue(weight);
        userRef.child("tinggiBadan").setValue(height);
        userRef.child("aktivitas").setValue(activityLevel);
        userRef.child("musim").setValue(season);
        userRef.child("total_konsumsi").setValue(totalKonsumsi);

        Toast.makeText(this, "Upload data berhasil.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(beranda.this, beranda.class);
        startActivity(intent);
        finish();
    }

    public void reloadAndUploadData(View view) {
        reloadAndUploadDataFromSharedPreferences();
    }

    private void showCongratulationsDialog() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(beranda.this);
                builder.setMessage("Selamat anda telah mencapai target minum harian");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }, 500);
    }

    public void bukaStatistik(View view) {
        Intent intent = new Intent(this, statistik.class);
        startActivity(intent);
    }

    public void bukaPengaturan(View view) {
        Intent intent = new Intent(this, pengaturan.class);
        startActivity(intent);
    }
}