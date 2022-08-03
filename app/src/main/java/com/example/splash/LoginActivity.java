package com.example.splash;




//import static com.example.splash.EZyNotificationService.CHANNEL_1_ID;
//import static com.example.splash.EZyNotificationService.CHANNEL_2_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.androidadvance.topsnackbar.TSnackbar;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //variables to be used in input validation
    EditText clientcode;
    EditText email;
    EditText password;
    Button button;
    //private NotificationManagerCompat notificationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notificationManager = NotificationManagerCompat.from(this);


//instantiate variables
        email = findViewById(R.id.editTextTextEmailAddress2);
        button = findViewById(R.id.button);
        password = findViewById(R.id.editTextTextPassword2);
        clientcode = findViewById(R.id.editTextTextPersonName3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEmailAddress(email);
                errorText(email);

                //uncomment when live
                //validatePassword(password);
                try {
                    String token = GetToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    /*public void login(View v) {
        String title = "Notification Title";
        String message = "Notification Message";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }*/

    //this function validates email address
    private boolean validateEmailAddress(EditText email) {
        String emailInput = email.getText().toString();

        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
        }
          //comment out this  line when live
        else
            //Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            highPrioritySnackbar("Invalid Email Address");
        return false;
    }

    /*this function validates password using regular expression
    8-20 characters, 1 lower case, 1 upper case, 1 special character

     */
    private boolean validatePassword(EditText password) {
        String passwordInput = password.getText().toString();
        String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@#$%^&+=])" + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(passwordInput);

        if (!passwordInput.isEmpty() && m.matches()) {
            Toast.makeText(this, "Password Validated Successfully!", Toast.LENGTH_SHORT).show();
            return true;

        } else
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        return false;
    }

    private String GetToken() throws IOException {
        ApplicationInfo ai = null;
        try {
            ai = getBaseContext().getPackageManager().getApplicationInfo(getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String Mode = (String) ai.metaData.get("Mode");
        String DevURL = (String) ai.metaData.get("DevURL");
        String ProdURL = (String) ai.metaData.get("ProdURL");
        String EZyTrackVersion = (String) ai.metaData.get("EZyTrackVersion");

        String ezyTrackURL = "";

        if (Mode.equals("Dev")) {
            ezyTrackURL = DevURL + "/" + EZyTrackVersion + "/" + clientcode.getText().toString() + "/sessions";


        } else {
            ezyTrackURL = ProdURL + "/" + EZyTrackVersion + "/" + clientcode.getText().toString() + "/sessions";

        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\r\n    \"email\":\"admin@ilunlimited.com\",\r\n    \"password\":\"12345678\"\r\n}",mediaType );
        Request request = new Request.Builder()
                .url("https://bbush.local.api.ezytrack.dev.ilunlimited.com/v0/testtest/sessions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        String TAG = "MyActivity";
        Log.i(TAG, ("entering execution"));
//defining asynchronous task to execute the httpclient to gather token
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Log.i("MyTag","made it to async");
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        Log.i("MyTag", "Made it further");
                        return null;
                    }
                    Log.i("Token", response.body().string());
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    Log.i("MyTag",s);
                }
            }
        };

//executes asynchronous command to hit api and gather token
        asyncTask.execute();



        return null;
    }
    //this function is high priority snackbar
    private boolean highPrioritySnackbar(String messagetext) {

         TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content),messagetext,TSnackbar.LENGTH_LONG);
         View snackbarView = snackbar.getView();
         snackbar.setActionTextColor(Color.parseColor("#275AA7"));
         snackbarView.setBackgroundColor(Color.parseColor("#FFDADA"));
         TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
         textView.setTextColor(Color.BLACK);
         snackbar.setAction("Dismiss",new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 snackbar.dismiss();
             }
                 });
         snackbar.show();


        return false;
    }
    private boolean mediumPrioritySnackbar(String messagetext) {

        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content),messagetext,TSnackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbar.setActionTextColor(Color.parseColor("#275AA7"));
        snackbarView.setBackgroundColor(Color.parseColor("#D5E6FF"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.setAction("Dismiss",new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
        return false;
    }
    private boolean lowPrioritySnackbar(String messagetext) {

        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content),messagetext,TSnackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbar.setActionTextColor(Color.parseColor("#bfe9a7"));
        snackbarView.setBackgroundColor(Color.parseColor("#FFDADA"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.setAction("Dismiss",new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();

        return false;
    }


    private boolean errorText (EditText email) {
        if (email.length()==0)
        {
            email.setError("Enter Email");
            highPrioritySnackbar("Please fill out any missing information, then try again.");
        }
        if (password.length()==0)
        {
            password.setError("Enter Password");
            highPrioritySnackbar("Please fill out any missing information, then try again.");
        }
        if (clientcode.length()==0)
        {
            clientcode.setError("Enter Client Code");
            highPrioritySnackbar("Please fill out any missing information, then try again.");

        }

        return false;
    }}











