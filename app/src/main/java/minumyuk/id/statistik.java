package minumyuk.id;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class statistik extends AppCompatActivity {

    private BarChartView barChartView;
    private TextView periodTextView, totalTextView;
    private DatabaseReference riwayatAirRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);

        barChartView = findViewById(R.id.barChartView);
        periodTextView = findViewById(R.id.periodTextView);
        totalTextView = findViewById(R.id.totalTextView);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        riwayatAirRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId).child("riwayat_air");

        Button btnWeekly = findViewById(R.id.btnWeekly);
        Button btnMonthly = findViewById(R.id.btnMonthly);
        Button btnYearly = findViewById(R.id.btnYearly);

        btnWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChartData(7); // Load data for the last 7 days
            }
        });

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChartData(30); // Load data for the last month
            }
        });

        btnYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChartData(365); // Load data for the last year
            }
        });

    }

    private void loadChartData(final int days) {
        final List<BarChartView.BarEntry> entries = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - (days * 24 * 60 * 60 * 1000L);

        riwayatAirRef.orderByKey().startAt(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(startTime)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        float totalAir = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String date = snapshot.getKey();
                            if (date != null && snapshot.child("air_konsumsi").getValue() != null) {
                                float airKonsumsi = snapshot.child("air_konsumsi").getValue(Float.class);
                                entries.add(new BarChartView.BarEntry(airKonsumsi));
                                totalAir += airKonsumsi;
                            }
                        }
                        barChartView.setEntries(entries);

                        // Set period and total air consumption in TextViews
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String startDate = sdf.format(new Date(startTime));
                        String endDate = sdf.format(new Date(currentTime));
                        String period = startDate + " ~ " + endDate;
                        periodTextView.setText(period);
                        totalTextView.setText("Total: " + totalAir);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
    }

    public void bukaPengaturan(View view) {
        Intent intent = new Intent(this, pengaturan.class);
        startActivity(intent);
    }

    public void bukaBeranda(View view) {
        Intent intent = new Intent(this, beranda.class);
        startActivity(intent);
    }
}
