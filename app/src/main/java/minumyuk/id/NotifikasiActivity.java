package minumyuk.id;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class NotifikasiActivity extends AppCompatActivity {

    private EditText editTextStartHour;
    private EditText editTextEndHour;
    private EditText editTextInterval;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifikasi);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(firebaseUser.getUid()).child("pengaturan_notifikasi");

        sharedPreferences = getSharedPreferences("NotificationSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editTextStartHour = findViewById(R.id.editTextStartHour);
        editTextEndHour = findViewById(R.id.editTextEndHour);
        editTextInterval = findViewById(R.id.editTextInterval);
        buttonSave = findViewById(R.id.buttonSave);

        loadNotificationSettings();

        // Default values from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String startHour = dataSnapshot.child("start_hour").getValue(String.class);
                    String endHour = dataSnapshot.child("end_hour").getValue(String.class);
                    String interval = dataSnapshot.child("interval_minute").getValue(String.class);

                    editTextStartHour.setText(startHour != null ? startHour : "07:00");
                    editTextEndHour.setText(endHour != null ? endHour : "22:00");
                    editTextInterval.setText(interval != null ? interval : "60");
                } else {
                    // Default values if no data exists
                    editTextStartHour.setText("07:00");
                    editTextEndHour.setText("22:00");
                    editTextInterval.setText("60");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(NotifikasiActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editTextInterval.setOnEditorActionListener((v, actionId, event) -> {
            editTextStartHour.requestFocus();
            return true;
        });

        editTextStartHour.setOnEditorActionListener((v, actionId, event) -> {
            editTextEndHour.requestFocus();
            return true;
        });

        editTextEndHour.setOnEditorActionListener((v, actionId, event) -> {
            editTextEndHour.clearFocus();
            return true;
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadNotificationSettings() {
        // Mengambil data dari SharedPreferences
        String startHour = sharedPreferences.getString("start_hour", "07:00");
        String endHour = sharedPreferences.getString("end_hour", "22:00");
        String interval = sharedPreferences.getString("interval_minute", "60");

        // Mengisi data ke view
        editTextStartHour.setText(startHour);
        editTextEndHour.setText(endHour);
        editTextInterval.setText(interval);
    }

    private void saveSettings() {
        String startHour = editTextStartHour.getText().toString().trim();
        String endHour = editTextEndHour.getText().toString().trim();
        String interval = editTextInterval.getText().toString().trim();

        if (startHour.isEmpty() || endHour.isEmpty() || interval.isEmpty()) {
            Toast.makeText(NotifikasiActivity.this, "Data harus diisi terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firebase
        databaseReference.child("start_hour").setValue(startHour);
        databaseReference.child("end_hour").setValue(endHour);
        databaseReference.child("interval_minute").setValue(interval);

        // Save to SharedPreferences
        editor.putString("start_hour", startHour);
        editor.putString("end_hour", endHour);
        editor.putString("interval_minute", interval);
        editor.apply();

        setWaterReminder(startHour, endHour, Integer.parseInt(interval));
        Toast.makeText(NotifikasiActivity.this, "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NotifikasiActivity.this, pengaturan.class);
        startActivity(intent);
        finish();
    }

    private void setWaterReminder(String startHour, String endHour, int intervalMinute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        String[] start = startHour.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(start[1]));
        calendar.set(Calendar.SECOND, 0);

        long startMillis = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startMillis, intervalMinute * 60 * 1000, pendingIntent);
    }
}
