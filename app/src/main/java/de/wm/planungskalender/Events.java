package de.wm.planungskalender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Events extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean isOffline;
    ArrayList<Event> events;
    int UserID;
    String cookie;
    private RecyclerView rv;

    CardView cv;

    NavigationView navigationView;

    boolean backgroundRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        cv = findViewById(R.id.refreshcardview);
        /*
         * ############################################
         * Navigation Drawer and Toolbar
         * ############################################
         */
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
         * ############################################
         * Offline detection
         * ############################################
         */

        Intent intent = getIntent();
        final boolean isOffline = intent.getBooleanExtra("isOffline", false);
        this.isOffline = isOffline;
        if (isOffline) {
            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);

        }


        /*
         * ############################################
         * Recycler View and Adapter
         * ############################################
         */


        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.getString("cookie", null) != null) {
            cookie = sharedPref.getString("cookie", null);
        }
        final boolean refresh = intent.getBooleanExtra("refresh", false);
        if (refresh) {
            refreshData();

        }
        rv = findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter(isOffline);
        soutData();
        /*if(events.size()==0){

            Intent myIntent = new Intent(this, MainActivity.class);
            myIntent.putExtra("IFAIL", true);
            this.startActivity(myIntent);
            finish();
        }*/

        /*
         * ############################################
         * UserID & cookie
         * ############################################
         */

        DBHandlerOnlineData DBh = new DBHandlerOnlineData(this, null, null, 1);
        if (!isOffline) {
            UserID = DBh.getUser().get_userid();

            cookie = DBh.getUser().get_cookies().get("PHPSESSID");

            sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            sharedPref.edit().putString("cookie", cookie).apply();

        }

        /*
         * ############################################
         * Click on Event Detection
         * ############################################
         */
        final View[] ChildView = new View[1];
        final int[] RecyclerViewItemPosition = {0};


        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(Events.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView[0] = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (ChildView[0] != null && gestureDetector.onTouchEvent(motionEvent)) {


                    RecyclerViewItemPosition[0] = Recyclerview.getChildAdapterPosition(ChildView[0]);
                    //Toast.makeText(Events.this, events.get(RecyclerViewItemPosition[0]).get_id()+"", Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(Events.this, EventDetails.class);
                    myIntent.putExtra("EventID", events.get(RecyclerViewItemPosition[0]).get_id());


                    if (isOffline) {
                        myIntent.putExtra("isOffline", true);
                    } else {

                        myIntent.putExtra("UserID", UserID);
                        myIntent.putExtra("cookie", cookie);

                    }
                    if (!backgroundRunning) {
                        Events.this.startActivity(myIntent);
                        finish();
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Events.this);
                        builder1.setMessage("Die Events werden gerade noch aktualisiert.\n");
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

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        RVAdapter adapter = new RVAdapter(events, false);
        adapter.notifyDataSetChanged();


    }

    private void initializeData() {
        events = null;
        events = new ArrayList<>();
        if (!isOffline) {
            DBHandler dbHandler = new DBHandler(this, null, null, 1);

            int i = 1;
            while (dbHandler.findEventById(i) != null) {
                events.add(dbHandler.findEventById(i));
                i++;

            }
            dbHandler.close();
        } else {
            DBHandlerBackup dbHandler = new DBHandlerBackup(this, null, null, 1);

            int i = 1;
            while (dbHandler.findEventById(i) != null) {
                events.add(dbHandler.findEventById(i));
                i++;

            }
            dbHandler.close();
        }


    }

    private void initializeAdapter(boolean isOffline) {

        RVAdapter adapter = new RVAdapter(events, isOffline);
        rv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_event) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //TODO Navigation

        if (id == R.id.nav_refresh) {

            refreshData();

        } else if (id == R.id.nav_events) {

        } else if (id == R.id.nav_archive) {
        } else if (id == R.id.nav_password) {

        } else if (id == R.id.nav_admin) {

        } else if (id == R.id.nav_close) {

            finish();
        } else if (id == R.id.nav_logout) {

            DBHandlerOnlineData dbHandlerOnlineData = new DBHandlerOnlineData(this, null, null, 1);
            dbHandlerOnlineData.deleteDatabase();

            startActivity(new Intent(Events.this, MainActivity.class));
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //TODO access refreshData() from "EventDetails" with api.refreshSignedIn
    public void refreshData() {
        if (checkForConnection()) {
            new refresh().execute();
        }
    }


    public class refresh extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            backgroundRunning = true;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    navigationView.getMenu().getItem(0).setVisible(false);
                    cv.setVisibility(View.VISIBLE);

                }
            });

            PlanungskalenderApi api = new PlanungskalenderApi(Events.this);
            Map<String, String> cookies = new HashMap<>();
            cookies.put("PHPSESSID", cookie);
            api.EventsToDatabase(cookies);

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            initializeData();

            RVAdapter adapter = new RVAdapter(events, isOffline);
            rv.swapAdapter(adapter, true);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    cv.setVisibility(View.GONE);
                    navigationView.getMenu().getItem(0).setVisible(true);

                }
            });
            backgroundRunning = false;
        }
    }

    public void soutData() {
        //DEBUG vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

        Object[] eventsarr = events.toArray();
        Event[] eventarr2 = new Event[eventsarr.length];
        String[] eventarr3 = new String[eventsarr.length];
        for (int j = 0; j < eventsarr.length; j++) {
            eventarr2[j] = (Event) eventsarr[j];
            eventarr3[j] = eventarr2[j].get_eventname() + ";" + Arrays.toString(eventarr2[j].get_signedOut().toArray());
        }
        Log.d("refreshData", Arrays.toString(eventarr3));

        //DEBUG^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    }

    private boolean checkForConnection() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Toast.makeText(this, "Es konnte keine Verbindung zum Internet hergestellt werden", Toast.LENGTH_LONG).show();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


}

