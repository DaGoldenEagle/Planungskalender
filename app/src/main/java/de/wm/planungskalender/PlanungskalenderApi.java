package de.wm.planungskalender;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlanungskalenderApi {
    Context context;

    public PlanungskalenderApi(Context context2) {
        context = context2;
    }

    public Map<String, String> EventsToDatabase(String name, String pass) {

        Map<String, String> result = new HashMap<>();
        String resultStr = "";
        String returnString = "";
        Map<String, String> Cookies = new HashMap<>();

        Cookies.put("PHPSESSID", "9c7f6d9e9c966530af5e7efbff77fa54");


        if (checkForConnection()) {
            Connection.Response res = null;
            try {
                res = Jsoup.connect("https://www.wittighaeuser-musikanten.de/kalender/index.php?action=login&show=events")
                        .timeout(0)
                        .userAgent("Mozilla")
                        .data("Name", name, "Passwort", pass)
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Cookies = res.cookies();


            org.jsoup.nodes.Document doc = null;
            try {
                doc = res.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            //
            //      Table into Database
            //
            //


            Elements noLogin = doc.select("b");


            if (noLogin.html().contains("anmelden")) {
                returnString = "Login fehlgeschlagen (Name/Passwort falsch).";
                resultStr = "noLogin";

            } else {
                deleteDatabase();


                Element table = doc.select("table").get(4);
                Elements rows = table.select("tr");


                for (int i = 1; i < rows.size(); i++) { //first row skipped (Beschreibung)
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    Element prevrow = rows.get(i - 1);
                    Elements prevcols = prevrow.select("td");

                    if (cols.get(0).html().equalsIgnoreCase("&nbsp;")) { //Test if date is displayed in previous row
                        //events on same date as previous
                        //TODO if more than 2 events are on 1 day
                        Element id = cols.get(2);
                        String ID = id.html();
                        ID = ID.substring(47, 53).replace("\"", "").replace(">", "");
                        int ID_INT = Integer.parseInt(ID);

                        Elements eventname = cols.get(2).select("a");
                        String NAME = eventname.html().replace("<b>", "").replace("</b>", "");

                        Element users = cols.get(4);
                        int USERS = Integer.parseInt(users.html());

                        Elements date = prevcols.get(0).select("a");

                        int j = 0;
                        while (date.html().equalsIgnoreCase("&nbsp;")) {
                            j++;
                            prevrow = rows.get(i - j);
                            prevcols = prevrow.select("td");
                            date = prevcols.get(0).select("a");
                        }
                        String DATE = date.html();


                        Element time = cols.get(1);
                        String TIME = time.html().replace("&nbsp;", "");

                        Element isSignedIn = cols.get(5);
                        String ISSIGNEDIN;
                        if (isSignedIn.html().contains("<a")) {
                            ISSIGNEDIN = "?";
                        } else {
                            ISSIGNEDIN = isSignedIn.html();
                        }
                        newEvent(ID_INT, NAME, USERS, DATE, TIME, Cookies, ISSIGNEDIN);

                    } else {

                        Element id = cols.get(3);
                        String ID = id.html();
                        ID = ID.substring(47, 53).replace("\"", "").replace(">", "");
                        int ID_INT = Integer.parseInt(ID);

                        Elements eventname = cols.get(3).select("a");
                        String NAME = eventname.html().replace("<b>", "").replace("</b>", "");

                        Element users = cols.get(5);

                        int USERS = Integer.parseInt(users.html());

                        Elements date = cols.get(0).select("a");
                        String DATE = date.html();


                        Element time = cols.get(2);
                        String TIME = time.html().replace("&nbsp;", "");

                        Element isSignedIn = cols.get(6);
                        String ISSIGNEDIN;
                        if (isSignedIn.html().contains("<a")) {
                            ISSIGNEDIN = "?";
                        } else {
                            ISSIGNEDIN = isSignedIn.html();
                        }
                        newEvent(ID_INT, NAME, USERS, DATE, TIME, Cookies, ISSIGNEDIN);
                    }

                    returnString = "Login erfolgreich";
                    resultStr = "login";


                }


            }

            //
            //
            //
            //
            //
        } else {

            returnString = "Es konnte keine Verbindung zum Internet aufgebaut werden!";
            resultStr = "noInet";

        }

        result.put("result", resultStr);
        result.put("returnString", returnString);
        result.put("cookie", Cookies.get("PHPSESSID"));

        return result;


    }

    public Map<String, String> EventsToDatabase(Map<String, String> CookiesMap) {

        Map<String, String> result = new HashMap<>();
        String resultStr = "";
        String returnString = "";
        Map<String, String> Cookies = CookiesMap;


        if (checkForConnection()) {
            Connection.Response res = null;
            try {
                res = Jsoup.connect("https://www.wittighaeuser-musikanten.de/kalender/index.php?show=events")
                        .timeout(0)
                        .userAgent("Mozilla")
                        .cookie("PHPSESSID", Cookies.get("PHPSESSID"))
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            org.jsoup.nodes.Document doc = null;
            try {
                doc = res.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            //
            //      Table into Database
            //
            //


            Elements noLogin = doc.select("b");


            if (noLogin.html().contains("anmelden")) {
                returnString = "Login fehlgeschlagen (Name/Passwort falsch).";
                resultStr = "noLogin";

            } else {
                deleteDatabase();


                Element table = doc.select("table").get(4);
                Elements rows = table.select("tr");

                //TODO keine Uhrzeit beim Event
                for (int i = 1; i < rows.size(); i++) { //first row skipped (Beschreibung)
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    Element prevrow = rows.get(i - 1);
                    Elements prevcols ;
                    int intj = 1;
                    if (cols.get(0).html().equalsIgnoreCase("&nbsp;")) { //Test if date is displayed in previous row
                        //events on same date as previous
                        while (prevrow.select("td").get(0).html().equalsIgnoreCase("&nbsp;")){
                            intj++;
                            prevrow = rows.get(i-intj);
                        }
                        prevcols = prevrow.select("td");

                        //TODO if more than 2 events are on 1 day
                        Element id = cols.get(2);
                        String ID = id.html();
                        ID = ID.substring(47, 53).replace("\"", "").replace(">", "");
                        int ID_INT = Integer.parseInt(ID);

                        Elements eventname = cols.get(2).select("a");
                        String NAME = eventname.html().replace("<b>", "").replace("</b>", "");

                        Element users = cols.get(4);
                        int USERS = Integer.parseInt(users.html());

                        Elements date = prevcols.get(0).select("a");

                        int j = 0;
                        while (date.html().equalsIgnoreCase("&nbsp;")) {
                            j++;
                            prevrow = rows.get(i - j);
                            prevcols = prevrow.select("td");
                            date = prevcols.get(0).select("a");
                        }
                        String DATE = date.html();


                        Element time = cols.get(1);
                        String TIME = time.html().replace("&nbsp;", "");

                        Element isSignedIn = cols.get(5);
                        String ISSIGNEDIN;
                        if (isSignedIn.html().contains("<a")) {
                            ISSIGNEDIN = "?";
                        } else {
                            ISSIGNEDIN = isSignedIn.html();
                        }
                        newEvent(ID_INT, NAME, USERS, DATE, TIME, Cookies, ISSIGNEDIN);

                    } else {

                        Element id = cols.get(3);
                        String ID = id.html();
                        ID = ID.substring(47, 53).replace("\"", "").replace(">", "");
                        int ID_INT = Integer.parseInt(ID);

                        Elements eventname = cols.get(3).select("a");
                        String NAME = eventname.html().replace("<b>", "").replace("</b>", "");

                        Element users = cols.get(5);

                        int USERS = Integer.parseInt(users.html());

                        Elements date = cols.get(0).select("a");
                        String DATE = date.html();


                        Element time = cols.get(2);
                        String TIME = time.html().replace("&nbsp;", "");

                        Element isSignedIn = cols.get(6);
                        String ISSIGNEDIN;
                        if (isSignedIn.html().contains("<a")) {
                            ISSIGNEDIN = "?";
                        } else {
                            ISSIGNEDIN = isSignedIn.html();
                        }
                        newEvent(ID_INT, NAME, USERS, DATE, TIME, Cookies, ISSIGNEDIN);
                    }



                }
                returnString = "Login erfolgreich";
                resultStr = "login";


            }

            //
            //
            //
            //
            //
        } else {

            returnString = "Es konnte keine Verbindung zum Internet aufgebaut werden!";
            resultStr = "noInet";

        }

        result.put("result", resultStr);
        result.put("returnString", returnString);
        result.putAll(Cookies);

        return result;


    }

    public Map<String, String> refreshSignedIn(Map<String, String> CookiesMap) {


        Map<String, String> result = new HashMap<>();
        String resultStr = "";
        String returnString = "";
        Map<String, String> Cookies = CookiesMap;


        if (checkForConnection()) {
            Connection.Response res = null;
            try {
                res = Jsoup.connect("https://www.wittighaeuser-musikanten.de/kalender/index.php?show=events")
                        .timeout(0)
                        .userAgent("Mozilla")
                        .cookie("PHPSESSID", Cookies.get("PHPSESSID"))
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            org.jsoup.nodes.Document doc = null;
            try {
                doc = res.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Elements noLogin = doc.select("b");


            if (noLogin.html().contains("anmelden")) {
                returnString = "Login fehlgeschlagen (Name/Passwort falsch).";
                resultStr = "noLogin";

            } else {


                Element table = doc.select("table").get(4);
                Elements rows = table.select("tr");


                DBHandler DBH = new DBHandler(context, null, null, 1);
                for (int i = 1; i < rows.size(); i++) { //first row skipped (Beschreibung)
                    Element row = rows.get(i);

                    Elements cols = row.select("td");
                    Elements eventname = cols.get(3).select("a");
                    String NAME = eventname.html().replace("<b>", "").replace("</b>", "");


                    Element isSignedIn = cols.get(6);
                    String ISSIGNEDIN;
                    if (isSignedIn.html().contains("<a")) {
                        ISSIGNEDIN = "?";
                    } else {
                        ISSIGNEDIN = isSignedIn.html();
                    }
                    if (DBH.findEvent(NAME) != null) {
                        DBH.setSignedIn(NAME,ISSIGNEDIN);
                    }

                    returnString = "Login erfolgreich";
                    resultStr = "login";


                }


            }

            //
            //
            //
            //
            //
        } else

        {

            returnString = "Es konnte keine Verbindung zum Internet aufgebaut werden!";
            resultStr = "noInet";

        }

        result.put("result", resultStr);
        result.put("returnString", returnString);
        result.putAll(Cookies);

        return result;


    }

    public String UserToDatabase(String cookie, boolean StayLoggedIn) {
        String returnString;
        int userId;
        String username;
        String rights;
        Map<String, String> Cookies = new HashMap<>();
        Cookies.put("PHPSESSID", cookie);
        if (checkForConnection()) {
            Connection.Response res = null;
            try {
                res = Jsoup.connect("https://www.wittighaeuser-musikanten.de/kalender/index.php?show=events")
                        .timeout(0)
                        .userAgent("Mozilla")
                        .cookie("PHPSESSID", cookie)
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            org.jsoup.nodes.Document doc = null;
            try {
                doc = res.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //####################################

            Elements tables = doc.select("table");
            Element table = tables.get(2);
            Element row = table.selectFirst("tr");
            Elements cells = row.select("td");

            userId = Integer.valueOf(cells.get(4).selectFirst("b").html());

            username = cells.get(1).selectFirst("b").html();

            rights = cells.get(6).selectFirst("b").html();

            DBHandlerOnlineData dbHandlerOnlineData = new DBHandlerOnlineData(context, null, null, 1);

            User user = new User();

            user.set_name(username);
            user.set_userid(userId);
            user.set_rights(rights);

            if (StayLoggedIn) {

                user.set_cookies(Cookies);

            } else {
                Map<String, String> map = new HashMap<>();
                map.put("PHPSESSID", "nonelol");

                user.set_cookies(map);

            }
            dbHandlerOnlineData.deleteDatabase();
            dbHandlerOnlineData.addUser(user);
            returnString = "";

        } else {
            returnString = "noInet";
        }

        return returnString;

    }

    public void signUpToEvent(int EventID, int UserID, String cookie) {
        if (checkForConnection()) {
            Connection res = Jsoup.connect("https://wittighaeuser-musikanten.de/kalender/index.php?action=edit&do=joinevent&eventid=" + EventID + "&show=events&user=" + UserID + "&Abgesagt=0")
                    .cookie("PHPSESSID", cookie)
                    .timeout(0)
                    .userAgent("Mozilla");
            try {
                res.execute();
            } catch (IOException e) {

            }
        }


    }

    public void signOutOfEvent(int EventID, int UserID, String cookie) {
        if (checkForConnection()) {
            try {
                Jsoup.connect("https://wittighaeuser-musikanten.de/kalender/index.php?action=edit&do=joinevent&eventid=" + EventID + "&show=showevents&tag=user=" + UserID + "&Abgesagt=1")
                        .cookie("PHPSESSID", cookie)
                        .timeout(0)
                        .userAgent("Mozilla")
                        .execute();
            } catch (IOException e) {

            }
        }

    }

    public void revertSignInOut(int EventID, String cookie) {
        if (checkForConnection()) {
            try {
                Jsoup.connect("https://wittighaeuser-musikanten.de/kalender/index.php?action=edit&do=leaveevent&eventid=" + EventID + "&show=showevents&tag=")
                        .cookie("PHPSESSID", cookie)
                        .timeout(0)
                        .userAgent("Mozilla")
                        .execute();
            } catch (IOException e) {

            }

        }

    }

    private void deleteDatabase() {
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        dbHandler.deleteDatabase();

    }


    private void newEvent(int id, String eventname, int users, String date, String time, Map<String, String> Cookies, String isSignedIn) {
        DBHandler dbHandler = new DBHandler(context, null, null, 1);

        //###Get "creator","signedIn","signedOut"
        //TODO if no one is signed in on event
        String info;
        String creator;
        ArrayList<String> signedIn = new ArrayList<>();
        ArrayList<String> signedOut = new ArrayList<>();

        Connection.Response res = null;
        try {
            res = Jsoup.connect("https://wittighaeuser-musikanten.de/kalender/index.php?show=showevents&eventid=" + id)
                    .timeout(0)
                    .userAgent("Mozilla")
                    .cookies(Cookies)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


        org.jsoup.nodes.Document doc = null;
        try {
            doc = res.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element table = doc.select("table").get(4);
        Elements rows = table.select("tr");

        creator = rows.get(5).select("td").get(1).wholeText();

        if (rows.get(6).select("td").get(0).wholeText().contains("Info")) {
            info = rows.get(6).select("td").get(1).wholeText();

            if (rows.get(7).select("td").get(0).wholeText().contains("Teilnehmer")) {

                Element signedInTable = rows.get(7).select("td").get(1).selectFirst("table");
                Elements signedInTableRows = signedInTable.select("tr");

                signedIn = getSignedIn(signedInTableRows);
                int kl = 7;
                Element signedOutTable = null;
                while(!rows.get(kl).selectFirst("td").html().contains("Abgesagt")){
                    kl++;
                    signedOutTable = rows.get(kl).selectFirst("table");
                }

                if (signedOutTable != null && signedOutTable.html().contains("tr")) {
                    Elements signedOutTableRows = signedOutTable.select("tr");
                    signedOut = getSignedOut(signedOutTableRows);
                }

            } else {
                Element signedOutTable = rows.get(8).select("td").get(1).selectFirst("table");
                Element signedOutTablePre = rows.get(8).select("td").get(1);

                if (signedOutTablePre.html().contains("tr")) {
                    Elements signedOutTableRows = signedOutTable.select("tr");
                    signedOut = getSignedOut(signedOutTableRows);
                }

            }

        } else {
            info = null;

            if (rows.get(6).select("td").get(0).wholeText().contains("Teilnehmer")) {

                Element signedInTable = rows.get(6).select("td").get(1).selectFirst("table");
                Elements signedInTableRows = signedInTable.select("tr");

                signedIn = getSignedIn(signedInTableRows);

                int kl = 7;
                Element signedOutTable = null;
                while(!rows.get(kl).selectFirst("td").html().contains("Abgesagt")){
                    kl++;
                    signedOutTable = rows.get(kl).selectFirst("table");
                }
                if (signedOutTable != null && signedOutTable.html().contains("tr")) {
                    Elements signedOutTableRows = signedOutTable.select("tr");
                    signedOut = getSignedOut(signedOutTableRows);
                }


            } else {

                int kl = 7;
                Element signedOutTable = null;
                while(!rows.get(kl).selectFirst("td").html().contains("Abgesagt")){
                    kl++;
                    signedOutTable = rows.get(kl).selectFirst("table");
                }
                if (signedOutTable.html().contains("tr")) {
                    Elements signedOutTableRows = signedOutTable.select("tr");
                    signedOut = getSignedOut(signedOutTableRows);
                }

            }


        }


        /*if (rows.get(6).select("td").get(0).wholeText().contains("Teilnehmer")) {


                Element signedInTable = table.select("table").get(2);

                Elements signedInTableRows = signedInTable.select("tr");
                String person = "";
                for (int i = 0; i < signedInTableRows.size(); i++) {
                    person = signedInTableRows.get(i).select("td").get(1).wholeText();
                    if (person.length() <= 2) {
                        //TODO if Guests are in one line and seperated by comma the are not displayed correctly
                        person = signedInTableRows.get(i).select("td").get(2).wholeText();
                        person = person + "Guest";
                    }
                    signedIn.add(person);


                }


                Element signedOutTable = table.select("table").get(3);

                Elements signedOutTableRows = signedOutTable.select("tr");
                String person2 = "";
                for (int i = 0; i < signedOutTableRows.size(); i++) {
                    person2 = signedOutTableRows.get(i).select("td").get(1).wholeText();
                    signedOut.add(person2);


                }
            } else {
                Element signedOutTable = table.select("table").get(2);

                Elements signedOutTableRows = signedOutTable.select("tr");
                String person2 = "";
                for (int i = 0; i < signedOutTableRows.size(); i++) {
                    person2 = signedOutTableRows.get(i).select("td").get(1).wholeText();
                    signedOut.add(person2);


                }


            }*/


        //###

        Event event = new Event(id, eventname, users, date, time, creator, signedIn, signedOut, info, isSignedIn);
        dbHandler.addEvent(event);

    }

    private ArrayList<String> getSignedIn(Elements signedInTableRows) {

        ArrayList<String> signedIn = new ArrayList<>();

        String person = "";
        for (int i = 0; i < signedInTableRows.size(); i++) {
            person = signedInTableRows.get(i).select("td").get(1).wholeText();
            if (person.length() <= 2) {
                //TODO if Guests are in one line and seperated by comma the are not displayed correctly
                person = signedInTableRows.get(i).select("td").get(2).wholeText();
                person = person + "Guest";
            }
            signedIn.add(person);


        }
        return signedIn;

    }


    private ArrayList<String> getSignedOut(Elements signedOutTableRows) {

        ArrayList<String> signedOut = new ArrayList<>();

        String person = "";
        for (int i = 0; i < signedOutTableRows.size(); i++) {
            person = signedOutTableRows.get(i).select("td").get(1).wholeText();

            signedOut.add(person);


        }
        return signedOut;

    }


    private boolean checkForConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


}