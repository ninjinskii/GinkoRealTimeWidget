package com.louis.app.ginkorealtimewidget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "com.louis.app.ginkorealtimewidget_SHARED_PREFS";
    public static final String KEY_ID_LIGNE = "com.louis.app.ginkorealtimewidget_KEY_LIGNE";
    public static final String KEY_ARRET1 = "com.louis.app.ginkorealtimewidget_KEY_ARRET1";
    public static final String KEY_SENS_ALLER1 = "com.louis.app.ginkorealtimewidget_KEY_SENS_ALLER1";
    public static final String KEY_ARRET2 = "com.louis.app.ginkorealtimewidget_KEY_ARRET2";
    public static final String KEY_COLOR_TEXT = "com.louis.app.ginkorealtimewidget_KEY_COLOR_TEXT";
    public static final String KEY_COLOR_BACKGROUND = "com.louis.app.ginkorealtimewidget_KEY_COLOR_BACKGROUND";

    public static final String EXTRA_LIGNE = "com.louis.app.ginkorealtimewidget_EXTRA_LIGNE";

    private EditText etLigne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setTitle("Configurer le widget Ginko");
        }

        etLigne = findViewById(R.id.MAIN_entrerLigne);

        Button bValider = findViewById(R.id.buttonAdd);
        bValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityConfig();
            }
        });
    }

    private void startActivityConfig(){
        Intent intent = new Intent(this, ActivityConfig.class);
        intent.putExtra(EXTRA_LIGNE, etLigne.getText().toString());
        startActivity(intent);
    }
}
