package de.wm.planungskalender;
/*
 * ALLES DURCHGESCHAUT
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;



import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);


        Button offlineBtn = findViewById(R.id.offlineBtn);
        offlineBtn.setOnClickListener(this);


        DBHandlerOnlineData dbHandlerOnlineData = new DBHandlerOnlineData(this, null, null, 1);
        User stayLoIn = dbHandlerOnlineData.getUser();

        Intent intent = getIntent();
        boolean InternetFailed = intent.getBooleanExtra("IFAIL", false);
        if(InternetFailed) {
            stayLoIn = null;
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Die Verbindung zum Internet wurde abgebrochen.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


        if (stayLoIn != null) {

            if (stayLoIn.get_cookies().get("PHPSESSID").contains("none") || !stayLoIn.get_cookies().containsKey("PHPSESSID")) {

            } else if (checkForConnection()) {
                //CHANGE LAYOUT
                //
                ProgressBar progressBarLogin = findViewById(R.id.progressBarLogin);
                progressBarLogin.setVisibility(View.VISIBLE);

                loginBtn.setVisibility(View.GONE);
                offlineBtn.setVisibility(View.GONE);

                EditText usrnme = findViewById(R.id.nameField);
                usrnme.setVisibility(View.GONE);

                EditText psswrd = findViewById(R.id.passField);
                psswrd.setVisibility(View.GONE);

                CheckBox CB = findViewById(R.id.checkBox);
                CB.setVisibility(View.GONE);

                TextView text = findViewById(R.id.textView);
                text.setText("Einloggen");

                //Get Data
                //
                new GetDataFromWebsite().execute("has_cookies", stayLoIn.get_cookies().get("PHPSESSID"));

            }

        }


    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginBtn:
                ProgressBar progressBarLogin = findViewById(R.id.progressBarLogin);
                progressBarLogin.setVisibility(View.VISIBLE);

                Button loginBtn = findViewById(R.id.loginBtn);
                loginBtn.setVisibility(View.GONE);

                EditText usrnme = findViewById(R.id.nameField);
                EditText psswrd = findViewById(R.id.passField);

                String usr = usrnme.getText().toString();
                String pss = psswrd.getText().toString();

                new GetDataFromWebsite().execute(usr, pss);
                break;

            case R.id.offlineBtn:
                Intent myIntent = new Intent(MainActivity.this, Events.class);
                myIntent.putExtra("isOffline", true);

                this.startActivity(myIntent);
                finish();
                break;
        }

    }

    public class GetDataFromWebsite extends AsyncTask<String, Void, String> {
        boolean uses_cookies = false;

        @Override
        protected String doInBackground(String... strings) {

            PlanungskalenderApi API = new PlanungskalenderApi(MainActivity.this);

            if (strings[0].equals("has_cookies")) {
                uses_cookies = true;
                // If Cookie is saved


                Map<String, String> cookies = new HashMap<>();
                cookies.put("PHPSESSID", strings[1]);

                Map<String, String> result = API.EventsToDatabase(cookies);
                String returnString = result.get("returnString");


                ProgressBar progressBarLogin = findViewById(R.id.progressBarLogin);
                progressBarLogin.setVisibility(View.INVISIBLE);

                if (result.get("result").equals("login")) {

                    startActivity(new Intent(MainActivity.this, Events.class));
                    finish();

                }
                return returnString;

            } else {
                //If Username and Password is put in

                String name = strings[0];
                String pass = strings[1];

                Map<String, String> result = API.EventsToDatabase(name, pass);
                String returnString = result.get("returnString");
                String cookie = result.get("cookie");

                CheckBox CB = findViewById(R.id.checkBox);
                String ret2 = "";

                if (result.get("result").equals("login")) {
                    if (CB.isChecked()) {

                        ret2 = API.UserToDatabase(cookie, true);

                    } else {

                        ret2 = API.UserToDatabase(cookie, false);

                    }

                    startActivity(new Intent(MainActivity.this, Events.class));
                    finish();

                }
                return returnString + " " + ret2;


            }

        }

        @Override
        protected void onPostExecute(String result) {

            TextView LoggedIn = findViewById(R.id.textView);
            LoggedIn.setText(result);

            if (result.equals("Login erfolgreich")) {
                ProgressBar progressBarLogin = findViewById(R.id.progressBarLogin);
                progressBarLogin.setVisibility(View.GONE);
            } else {
                ProgressBar progressBarLogin = findViewById(R.id.progressBarLogin);
                progressBarLogin.setVisibility(View.GONE);

                Button loginBtn = findViewById(R.id.loginBtn);
                loginBtn.setVisibility(View.VISIBLE);

                if (uses_cookies) {

                    Button offline = findViewById(R.id.offlineBtn);
                    offline.setVisibility(View.VISIBLE);

                    EditText usrnme = findViewById(R.id.nameField);
                    usrnme.setVisibility(View.VISIBLE);

                    EditText psswrd = findViewById(R.id.passField);
                    psswrd.setVisibility(View.VISIBLE);

                    CheckBox cb = findViewById(R.id.checkBox);
                    cb.setVisibility(View.VISIBLE);

                    LoggedIn.setText("Automatischer Login fehlgeschlagen");

                }
            }
        }
    }

    private boolean checkForConnection() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
