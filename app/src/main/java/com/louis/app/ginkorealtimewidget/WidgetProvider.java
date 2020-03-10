package com.louis.app.ginkorealtimewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ARRET1;
import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ARRET2;
import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_ID_LIGNE;
import static com.louis.app.ginkorealtimewidget.MainActivity.KEY_SENS_ALLER1;
import static com.louis.app.ginkorealtimewidget.MainActivity.SHARED_PREFS;

public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REVERSE = "com.louis.app.ginkorealtimewidget.REVERSE";

    String temps;                                           // Temps avant le passage du bus
    String destination;                                     // Destination du bus
    String couleurLigne = "#";                              // Couleur de fond de la ligne
    String couleurTexte = "#";                              // Couleur du texte de la ligne
    boolean fiable;                                         // Horaire théorique ?
    HashMap<String, String> mapInfos = new HashMap<>();     // Stocke les infos récupérées pour un arrêt

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        // Récupération de la configuration du widget définie dans la Main
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String idLigne = prefs.getString(KEY_ID_LIGNE, "Nom de la ligne");                  // Ligne choisie
        String nomArret1 = prefs.getString(KEY_ARRET1, "Nom arrêt 1");                      // Arrêt voulu
        boolean sensAller1 = prefs.getBoolean(KEY_SENS_ALLER1, true);                       // Sens pour l'arrêt 1

        // Construction de l'url
        StringBuilder url = new StringBuilder("https://api.ginko.voyage/TR/getListeTemps.do?listeNoms=");
        url.append(nomArret1).append("&listeIdLignes=").append(idLigne).append("&listeSensAller=")
                .append(sensAller1).append("&nb=3");

        // Queue de requête
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray array;
                    JSONObject objet;

                    try {
                        array = response.getJSONArray("objets");

                        // Vérification ultime arrêt valide. Peut se déclencher pour un terminus avec un mauvais sens de circulation par exemple
                        // ou si aucun bus ne passe à cet arrêt (relativement rare)
                        if(array.length() == 0){
                            Toast.makeText(context, R.string.CONFIG_arretErrorOrNoBus, Toast.LENGTH_LONG).show();
                            return;
                        }

                        array = array.getJSONObject(0).getJSONArray("listeTemps");

                        for(int i=0; i < array.length(); i++){
                            objet = array.getJSONObject(i);
                            temps = objet.getString("temps");
                            fiable = objet.getBoolean("fiable");

                            if(!fiable){ temps += "*"; }

                            mapInfos.put("temps" + i, temps);

                            if(i == 0){
                                destination = array.getJSONObject(i).getString("destination");
                                couleurLigne += array.getJSONObject(i).getString("couleurFond");
                                couleurTexte += array.getJSONObject(i).getString("couleurTexte");
                                String numLignePublic = array.getJSONObject(i).getString("numLignePublic");

                                mapInfos.put("sens", destination);
                                mapInfos.put("couleurTexte", couleurTexte);
                                mapInfos.put("couleurLigne", couleurLigne);
                                mapInfos.put("numLignePublic", numLignePublic);
                            }
                        }
                    } catch(JSONException e) {
                        Log.v("________JSON-EXCEPTION________", "Une exception a étée levée lors de la désérialisation");
                        e.printStackTrace();
                    }

                    for(int appWidgetId : appWidgetIds){
                        updateAppWidget(context, appWidgetManager, appWidgetId, mapInfos);
                    }
                }
            }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context, R.string.internetError, Toast.LENGTH_LONG).show();
            }
        });

        MyRequestQueue.add(request);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null){
            if(intent.getAction().equals(ACTION_REVERSE)){
                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                String arret1 = prefs.getString(KEY_ARRET1, "Nom de la ligne");
                boolean retour = prefs.getBoolean(KEY_SENS_ALLER1, true);
                retour = !retour;

                editor.putBoolean(KEY_SENS_ALLER1, retour);
                editor.putString(KEY_ARRET1, prefs.getString(KEY_ARRET2, "Nom de la ligne"));
                editor.putString(KEY_ARRET2, arret1);
                editor.apply();

                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            }
        }

        super.onReceive(context, intent);
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, HashMap<String, String> mapInfos){
        int[] idArray = new int[] {appWidgetId};                                                // Pour n'actualiser que le widget touché
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ginko_widget);   // Obtenir les vues à actualiser

        // Connaître l'heure
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
        String heure = simpleDateFormat.format(c.getTime());

        // Actualisation des vues
        views.setInt(R.id.imgLigne, "setBackgroundColor", Color.parseColor(mapInfos.get("couleurLigne")));
        views.setTextColor(R.id.imgLigne, Color.parseColor(mapInfos.get("couleurTexte")));
        views.setTextViewText(R.id.imgLigne, mapInfos.get("numLignePublic"));
        views.setTextViewText(R.id.textLigne, mapInfos.get("sens"));
        views.setTextViewText(R.id.tempsBus1, mapInfos.get("temps0"));
        views.setTextViewText(R.id.tempsBus2, mapInfos.get("temps1"));
        views.setTextViewText(R.id.tempsBus3, mapInfos.get("temps2"));
        views.setTextViewText(R.id.tempsRefresh, String.format(context.getString(R.string.refreshTime), heure));

        // Intent d'actualisation de widget
        Intent intentUpdate = new Intent(context, WidgetProvider.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Intent d'inversion du trajet
        Intent intentReverse = new Intent(context, WidgetProvider.class);
        intentReverse.setAction(ACTION_REVERSE);
        intentReverse.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Passer l'Intent dans un PendingIntent
        PendingIntent pIntentUpdate = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntentReverse = PendingIntent.getBroadcast(context, appWidgetId, intentReverse, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.boutonRefresh, pIntentUpdate);
        views.setOnClickPendingIntent(R.id.boutonReverse, pIntentReverse);



        appWidgetManager.updateAppWidget(appWidgetId, views);

        Toast.makeText(context, R.string.refreshed, Toast.LENGTH_SHORT).show();
    }
}
