//package com.louis.app.ginkorealtimewidget;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//
//import android.appwidget.AppWidgetManager;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.RemoteViews;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.android.material.snackbar.Snackbar;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import static com.louis.app.ginkorealtimewidget.MainActivity.EXTRA_LIGNE;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ARRET1;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ARRET2;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_COLOR_BACKGROUND;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_COLOR_TEXT;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ID_LIGNE;
//import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_SENS_ALLER1;
//import static com.louis.app.ginkorealtimewidget.MainActivity.SHARED_PREFS;
//
//public class ActivityConfig extends AppCompatActivity {
//
//    public static final String URL_LIGNES = "https://api.ginko.voyage/DR/getLignes.do";
//    public static final String URL_ARG1 = "&listeIdLignes=";
//    public static final String URL_ARG2 = "&listeSensAller=";
//    public static final String URL_LISTE_TEMPS =
//            "https://api.ginko.voyage/TR/getListeTemps.do?listeNoms=";
//
//    CoordinatorLayout mCoordinatorLayout;
//    TextView tvLigneDemandee;
//    ProgressBar mProgressBar;
//    CheckBox cbDestination1;
//    Button bValider;
//    EditText etArret1;
//    EditText etArret2;
//
//    String couleurLigne;
//    String couleurTexte;
//    String destination1;
//    String destination2;
//    boolean sensAller1;
//    String nomLigne;
//    String idLigne;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_config);
//
//        ActionBar actionBar = getSupportActionBar();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        if(actionBar != null){
//            actionBar.setTitle("Configurer le widget Ginko");
//        }
//
//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//
//        mCoordinatorLayout = findViewById(R.id.CONFIG_coordinator);
//        mProgressBar = findViewById(R.id.CONFIG_indeterminateBar);
//        cbDestination1 = findViewById(R.id.CONFIG_destination1);
//        etArret1 = findViewById(R.id.CONFIG_entrerArret1);
//        etArret2 = findViewById(R.id.CONFIG_entrerArret2);
//
//
//        RequestQueue myRequestQueue = Volley.newRequestQueue(this);
//
//        if(extras != null){
//            nomLigne = extras.getString(EXTRA_LIGNE);
//            tvLigneDemandee = findViewById(R.id.CONFIG_ligneDemandee);
//            tvLigneDemandee.setText(String.format(getString(R.string.CONFIG_ligne), nomLigne));
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_LIGNES, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        idLigne = "";           // ID de la ligne utilisé en interne par l'API
//                        destination1 = "";      // Terminus de la ligne
//                        destination2 = "";      // Deuxième terminus
//                        couleurLigne = "#";     // Couleur a utiliser dans l'affichage
//                        couleurTexte = "#";     // Couleur a utiliser pour le texte
//                        boolean trouve = false; // Ligne existante ?
//
//                        JSONArray array;
//                        JSONObject objet;
//
//                        try {
//                            array = response.getJSONArray("objets");
//
//                            // On cherche la ligne demandée dans la liste des lignes
//                            for(int i=0; i < array.length(); i++){
//                                objet = array.getJSONObject(i);
//                                String numLignePublic = objet.getString("numLignePublic");
//
//                                if(numLignePublic.equals(nomLigne)){
//                                    idLigne = objet.getString("id");
//                                    couleurLigne += objet.getString("couleurFond");
//                                    couleurTexte += objet.getString("couleurTexte");
//
//                                    JSONArray tabVariantes = objet.getJSONArray("variantes");
//                                    JSONObject variante1 = tabVariantes.getJSONObject(0);
//                                    destination1 = variante1.getString("destination");
//                                    sensAller1 = variante1.getBoolean("sensAller");
//
//                                    trouve = true;
//
//                                    // Certaines lignes n'ont pas de 2eme variantes, elles ne sont pas prises en charge
//                                    JSONObject variante2 = tabVariantes.getJSONObject(1);
//                                    destination2 = variante2.getString("destination");
//
//                                    mProgressBar.setVisibility(View.INVISIBLE);
//
//                                    break;
//                                }
//                            }
//                        } catch (JSONException e) {
//                            Log.v("________JSON-EXCEPTION/CONFIG________", "Une exception a étée levée lors de la désérialisation");
//
//                            Toast.makeText(ActivityConfig.this, R.string.CONFIG_notSupported, Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//
//                        if(trouve){
//                            // Actualisation des vues
//                            tvLigneDemandee.setBackgroundColor(Color.parseColor(couleurLigne));
//                            tvLigneDemandee.setTextColor(Color.parseColor(couleurTexte));
//                            cbDestination1.setText(String.format(getString(R.string.CONFIG_vers1), destination1));
//                        } else{
//                            // Ligne saisie invalide
//                            Toast.makeText(ActivityConfig.this, R.string.CONFIG_ligneError, Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                public void onErrorResponse(VolleyError error) {
//                    mProgressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(ActivityConfig.this, R.string.internetError, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            myRequestQueue.add(request);
//
//            bValider = findViewById(R.id.CONFIG_boutonAjouter);
//            bValider.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String arret1 = etArret1.getText().toString().replaceAll("\\s+","");
//                    String arret2 = etArret2.getText().toString().replaceAll("\\s+","");
//
//                    // Check arret, le 3eme para vaut true seulement pour le dernier check
//                    checkArret(arret1, arret2, sensAller1);
//                }
//            });
//        }
//    }
//
//    private void updateWidget(){
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.ginko_widget);
//        ComponentName widget = new ComponentName(this, MainActivity.class);
//
//        appWidgetManager.updateAppWidget(widget, remoteViews);
//    }
//
//    private void checkArret(String arret1, String arret2, boolean sensAller){
//        StringBuilder url1 = new StringBuilder();
//        url1.append(URL_LISTE_TEMPS).append(arret1).append(URL_ARG1)
//                .append(idLigne).append(URL_ARG2).append(sensAller);
//
//        final String url2= URL_LISTE_TEMPS + arret2 + URL_ARG1 +
//                idLigne + URL_ARG2 + !sensAller;
//
//        sendRequest(url1, new VolleyCallback() {
//            // La première requête passe
//            @Override
//            public void onSuccess() {
//                StringBuilder sb = new StringBuilder(url2);
//                sendRequest(sb, new VolleyCallback() {
//                    // Les deux requêtes sont passées
//                    @Override
//                    public void onSuccess() {
//                        validerPrefs();
//                        Snackbar.make(mCoordinatorLayout, R.string.CONFIG_finished, Snackbar.LENGTH_LONG).show();
//                    }
//
//                    // La deuxième requête échoue
//                    @Override
//                    public void onError() {
//                        Snackbar.make(mCoordinatorLayout, R.string.CONFIG_arret2Error, Snackbar.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            // La première requête échoue
//            @Override
//            public void onError() {
//                Snackbar.make(mCoordinatorLayout, R.string.CONFIG_arret1Error, Snackbar.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void sendRequest(StringBuilder url, final VolleyCallback callback){
//        RequestQueue myRequestQueue = Volley.newRequestQueue(this);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        JSONArray array;
//
//                        try {
//                            array = response.getJSONArray("objets");
//
//                            // Vérification arrêt valide
//                            if(array.length() == 0){
//                                callback.onError();
//                            } else{
//                                callback.onSuccess();
//                            }
//                        } catch(JSONException e) {
//                            Log.v("________JSON-EXCEPTION________", "Une exception a étée levée lors de la désérialisation");
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Snackbar.make(mCoordinatorLayout, R.string.internetError, Snackbar.LENGTH_LONG).show();
//            }
//        });
//
//        myRequestQueue.add(request);
//    }
//
//    private void validerPrefs(){
//        String arret1 = etArret1.getText().toString();
//        String arret2 = etArret2.getText().toString();
//
//        // Edition des préférences
//        if(!cbDestination1.isChecked()){
//            sensAller1 = !sensAller1;
//        }
//
//        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(KEY_ID_LIGNE, idLigne);
//        editor.putString(KEY_ARRET1, arret1);
//        editor.putBoolean(KEY_SENS_ALLER1, sensAller1);
//        editor.putString(KEY_ARRET2, arret2);
//        editor.apply();
//
//        updateWidget();
//    }
//
//    private interface VolleyCallback{
//        void onSuccess();
//        void onError();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp(){
//        finish();
//        return true;
//    }
//}
