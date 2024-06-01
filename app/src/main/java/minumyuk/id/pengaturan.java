package minumyuk.id;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class pengaturan extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
    }

    public void bukaStatistik(android.view.View view) {
        Intent intent = new Intent(this, statistik.class);
        startActivity(intent);
    }

    public void bukaBeranda(android.view.View view) {
        Intent intent = new Intent(this, beranda.class);
        startActivity(intent);
    }

    public void akun(android.view.View view) {
        Intent intent = new Intent(this, pengaturan_akun.class);
        startActivity(intent);
    }

    public void notifikasi(android.view.View view) {
        Intent intent = new Intent(this, NotifikasiActivity.class);
        startActivity(intent);
    }

    public void pencapaian(android.view.View view) {
        Intent intent = new Intent(this, pencapaian.class);
        startActivity(intent);
    }

    public void panduan(android.view.View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Minum Yuk adalah aplikasi yang membantu Anda untuk melacak asupan air harian dan memastikan Anda minum cukup air putih. Aplikasi ini memiliki beberapa fitur yang bermanfaat, seperti:\n" +
                        "\n" +
                        "Pelacak air: Catat berapa banyak air yang Anda minum setiap hari.\n" +
                        "Pengingat air: Dapatkan pengingat untuk minum air secara berkala sepanjang hari.\n" +
                        "Riwayat asupan air: Lihat riwayat asupan air Anda untuk melacak kemajuan Anda.\n" +
                        "Tujuan asupan air: Tetapkan tujuan asupan air harian dan pantau kemajuan Anda.\n" +
                        "Grafik asupan air: Lihat grafik asupan air Anda untuk melihat pola minum Anda.\n" +
                        "Tips minum air: Dapatkan tips dan trik untuk membantu Anda minum lebih banyak air.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void tips(android.view.View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tips\n" +
                        "\n" +
                        "Minumlah air putih setiap kali Anda merasa haus.\n" +
                        "Bawalah botol air minum kemana-mana Anda pergi.\n" +
                        "Minumlah air sebelum, selama, dan setelah berolahraga.\n" +
                        "Tambahkan buah atau rempah-rempah ke dalam air Anda untuk membuatnya lebih beraroma.\n" +
                        "Minumlah air dingin atau air hangat, tergantung pada preferensi Anda.\n" +
                        "Gunakan aplikasi ini untuk melacak asupan air Anda dan memastikan Anda minum cukup air putih")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void dukungan(android.view.View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dukungan\n" +
                        "\n" +
                        "Jika Anda memiliki pertanyaan atau masalah dengan aplikasi Minum Yuk,\n" +
                        "Anda dapat menghubungi +6282320577639 atau melalui email di sofyantauridodemadi@gmail.com.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void keluar(android.view.View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        Intent intent = new Intent(pengaturan.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.xml.zoom_in, R.xml.zoom_out);
    }
}
