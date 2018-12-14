package de.wm.planungskalender;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventDetails extends Activity {
    private RecyclerView rvsi;
    private RecyclerView rvso;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        final int EventID = intent.getIntExtra("EventID", 0);
        final int UserID = intent.getIntExtra("UserID", 0);
        final String cookie = intent.getStringExtra("cookie");
        boolean isOffline = intent.getBooleanExtra("isOffline", false);

        DBHandler dbHandler = new DBHandler(this, null, null, 1);

        final Event EventDetails = dbHandler.findEventByRealId(EventID);

        showDataTop(EventDetails, isOffline);

        rvsi = findViewById(R.id.rvTeilnehmer);
        LinearLayoutManager llmsi = new LinearLayoutManager(getApplicationContext());
        rvsi.setLayoutManager(llmsi);

        rvso = findViewById(R.id.rvAbgesagt);
        LinearLayoutManager llmso = new LinearLayoutManager(getApplicationContext());
        rvso.setLayoutManager(llmso);

        Teilnehmer(EventDetails);
        Abgesagt(EventDetails);
        dbHandler.close();

        ImageButton closeDetails = findViewById(R.id.back);
        closeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        final Button signOut = findViewById(R.id.signOut2);
        final Button signUp = findViewById(R.id.signUp3);

        switch (EventDetails.get_isSignedIn()) {
            case "Ja":
                signUp.setVisibility(View.GONE);
                signOut.setText("Anmeldung \nzurücknehmen");
                break;
            case "Nein":
                signUp.setVisibility(View.GONE);
                signOut.setText("Abmeldung \nzurücknehmen");
                break;
            case "?":

                break;

        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LongOperationSignUp().execute("" + EventID, "" + UserID, cookie);
                signUp.setVisibility(View.GONE);
                signOut.setText("Anmeldung \nzurücknehmen");

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signOut.getText().toString().contains("Abmeldung")){
                    new RevertSignInOut().execute("" + EventID, "" + UserID, cookie);

                    signUp.setVisibility(View.VISIBLE);

                    signOut.setText("Abmelden");
                }else if(signOut.getText().toString().contains("Anmeldung")){

                    new RevertSignInOut().execute("" + EventID, "" + UserID, cookie);
                    signUp.setVisibility(View.VISIBLE);
                    signOut.setText("Abmelden");
                }else {
                    new LongOperationSignOut().execute("" + EventID, "" + UserID, cookie);
                    signUp.setVisibility(View.GONE);
                    signOut.setText("Abmeldung \nzurücknehmen");
                }
            }
        });


    }

    private void showDataTop(Event EventDetails, boolean isOffline) {
        TextView time = findViewById(R.id.time);
        TextView date = findViewById(R.id.date);
        TextView users = findViewById(R.id.users);
        TextView signedUp = findViewById(R.id.signedUp);
        View signedUpPic = findViewById(R.id.imageView4);
        Toolbar name = findViewById(R.id.toolbar);
        TextView info = findViewById(R.id.infoInsert);
        Button signUp = findViewById(R.id.signUp3);
        Button signOut = findViewById(R.id.signOut2);

        time.setText(EventDetails.get_time());
        date.setText(EventDetails.get_date());
        int usersint = EventDetails.get_users();
        users.setText(String.valueOf(usersint));
        if (isOffline) {
            signedUpPic.setVisibility(View.GONE);
            signedUp.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);

        } else {
            signedUp.setText(EventDetails.get_isSignedIn());
        }
        name.setTitle(EventDetails.get_eventname());
        info.setText(EventDetails.get_info());
        if (EventDetails.get_info() == null) {
            View infoview = findViewById(R.id.imageView6);
            infoview.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
        }

    }

    private void Teilnehmer(Event EventDetails) {

        RVAdapterTeilnehmer adapter = new RVAdapterTeilnehmer(EventDetails.get_signedIn());
        rvsi.setAdapter(adapter);

    }

    private void Abgesagt(Event EventDetails) {

        RVAdapterAbgesagt adapter = new RVAdapterAbgesagt(EventDetails.get_signedOut());
        rvso.setAdapter(adapter);

    }

    private class LongOperationSignUp extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            PlanungskalenderApi api = new PlanungskalenderApi(EventDetails.this);
            api.signUpToEvent(Integer.valueOf(params[0]), Integer.valueOf(params[1]), params[2]);


            return "" + params[0];


        }

        @Override
        protected void onPostExecute(String result) {

            TextView signedUp = findViewById(R.id.signedUp);
            signedUp.setText("Ja");

            AppCompatImageView signedUpImg = findViewById(R.id.imageView4);
            signedUpImg.setImageResource(R.drawable.ic_person_green);


        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private class LongOperationSignOut extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            PlanungskalenderApi api = new PlanungskalenderApi(EventDetails.this);
            api.signOutOfEvent(Integer.valueOf(params[0]), Integer.valueOf(params[1]), params[2]);

            return "" + params[0];


        }

        @Override
        protected void onPostExecute(String result) {

            TextView signedUp = findViewById(R.id.signedUp);
            signedUp.setText("Nein");

            AppCompatImageView signedUpImg = findViewById(R.id.imageView4);
            signedUpImg.setImageResource(R.drawable.ic_person_red);


        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private class RevertSignInOut extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            PlanungskalenderApi api = new PlanungskalenderApi(EventDetails.this);
            api.revertSignInOut(Integer.valueOf(params[0]), params[2]);

            return "" + params[0];


        }

        @Override
        protected void onPostExecute(String result) {

            TextView signedUp = findViewById(R.id.signedUp);
            signedUp.setText("?");

            AppCompatImageView signedUpImg = findViewById(R.id.imageView4);
            signedUpImg.setImageResource(R.drawable.ic_person_black_24dp);


        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

}
