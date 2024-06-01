package minumyuk.id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class r1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1);
    }
    public void keatur(android.view.View view) {
        Intent intent = new Intent(this, r2.class);
        startActivity(intent);
        finish();
    }
}