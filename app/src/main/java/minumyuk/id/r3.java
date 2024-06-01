package minumyuk.id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class r3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r3);
    }
    public void keatur(android.view.View view) {
        Intent intent = new Intent(this, pengaturan_akun.class);
        startActivity(intent);
        finish();
    }
}