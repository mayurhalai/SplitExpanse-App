package com.mayur.splitbill;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    EditText username, password;
    TextView name;
    String token = null;
    ProgressDialog progess;
    Context context;
    SharedPreferences logininfo;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, MainActivity.class);
        progess = new ProgressDialog(this);
        progess.setTitle("Loading");
        progess.setCanceledOnTouchOutside(false);
        context = getApplicationContext();
        logininfo = getSharedPreferences("login", MODE_PRIVATE);
        if (logininfo.getBoolean("logged", false) == true) {
            setContentView(R.layout.activity_logout);
            name = (TextView) findViewById(R.id.textViewUser);
            name.setText("Hello, " + logininfo.getString("username", "None"));
        } else {
            setContentView(R.layout.activity_login);
            username = (EditText) findViewById(R.id.editTextuser);
            password = (EditText) findViewById(R.id.editTextpass);
        }
    }

    public void setlogin(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = Global.urlhome;
            new LoginTask().execute(url);
        } else {
            Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progess.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String content = null;
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(httpPost);
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                content = br.readLine();
            } catch (Exception e) {

            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contentEquals("fail")) {
                Toast.makeText(context, "Login not success", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = logininfo.edit();
                editor.putBoolean("logged", true);
                editor.putString("token", result);
                editor.putString("username", username.getText().toString());
                editor.commit();
                startActivity(intent);
                finish();
            }
            progess.dismiss();
        }
    }

    public void unsetlogin(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = Global.url + "logout";
            new LogoutTask().execute(url);
        } else {
            Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
        }
    }

    private class LogoutTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progess.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("token", logininfo.getString("token", "")));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(httpPost);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SharedPreferences.Editor editor = logininfo.edit();
            editor.putBoolean("logged", false);
            editor.putString("token", "");
            editor.putString("username", "");
            editor.commit();
            setContentView(R.layout.activity_login);
            username = (EditText) findViewById(R.id.editTextuser);
            password = (EditText) findViewById(R.id.editTextpass);
            progess.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
