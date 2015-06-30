package com.mayur.splitbill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mayur on 6/22/2015.
 */
public class AddBills extends Fragment {
    static boolean FILE_UPLODED = false;
    EditText amount, description;
    String date, todate;
    Button reset, submit, showCamera, showDate;
    Context context;
    SharedPreferences logininfo;
    ProgressDialog progressDialog;
    ScrollView sv;
    LinearLayout ll;
    CheckBox[] checkBoxes;
    String[] username;
    File file;
    Data data;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addbills, container, false);

        context = getActivity();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        logininfo = context.getSharedPreferences("login", context.MODE_PRIVATE);
//        String filename = "BillImage.jpg";
//        file = new File(context.getFilesDir(), filename);

        amount = (EditText) rootView.findViewById(R.id.editTextAmount);
        description = (EditText) rootView.findViewById(R.id.editTextDescription);
        showDate = (Button) rootView.findViewById(R.id.buttonDate);
        showCamera = (Button) rootView.findViewById(R.id.buttonBill);
        reset = (Button) rootView.findViewById(R.id.buttonReset);
        submit = (Button) rootView.findViewById(R.id.buttonSubmit);
        sv = (ScrollView) rootView.findViewById(R.id.scrollView);

        ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        todate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        showDate.setText(todate);
        showDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "Date");
            }
        });

        showCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.getText().clear();
                description.getText().clear();
                showDate.setText(todate);
                FILE_UPLODED = false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().length() > 0) {
                    String Amount = amount.getText().toString();
                    String Description = description.getText().toString();
                    String Date = showDate.getText().toString();
                    List<String> Members = new ArrayList<String>();
                    for (int i = 0; i < checkBoxes.length; i++) {
                        if (checkBoxes[i].isChecked()) {
                            Members.add(username[i]);
                        }
                    }
                    data = new Data(Amount, Description, Date, Members);
                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        String url = Global.url + "add";
                        new UploadDataTask().execute(url);
                    } else {
                        Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Write Amount", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = Global.url + "fetchuser";
            new DownloadDataTask().execute(url);
        } else {
            Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SplitBill");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "BillImage.jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                file = new File(fileUri.getPath());
                if (file.exists()) {
                    FILE_UPLODED = true;
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        /*if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            //Bitmap imageBitmap = Bitmap.createScaledBitmap((Bitmap) extras.get("data"), 372, 663, true);
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                fout.close();
                FILE_UPLODED = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
            datePickerDialog.setCanceledOnTouchOutside(false);
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            date = new SimpleDateFormat("MM/dd/yyyy").format(new Date(year - 1900, month, day));
            showDate.setText(date);
        }
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                return Global.downloadUrl(urls[0], logininfo.getString("token", ""));
            } catch (IOException e) {
                return "Unable to access Webpage";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray users = new JSONArray(result);
                checkBoxes = new CheckBox[users.length()];
                username = new String[users.length()];
                for (int i=0; i<users.length();i++) {
                    JSONObject user = users.getJSONObject(i);
                    checkBoxes[i] = new CheckBox(context);
                    checkBoxes[i].setText(user.getString("name"));
                    checkBoxes[i].setChecked((user.getString("common").contentEquals("1")));
                    ll.addView(checkBoxes[i]);
                    username[i] = new String(user.getString("username"));
                }
            } catch (JSONException e) {

            }
            //progressDialog.dismiss();
        }
    }

    private class UploadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                Gson gson = new Gson();
                String jsondata = gson.toJson(data);
                if (FILE_UPLODED) {
                    return Global.downloadUrl(urls[0], logininfo.getString("token", ""), jsondata, file);
                } else {
                    return Global.downloadUrl(urls[0], logininfo.getString("token", ""), jsondata);
                }
            } catch (IOException e) {
                return "Unable to access Webpage";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            amount.getText().clear();
            description.getText().clear();
            showDate.setText(todate);
            FILE_UPLODED = false;
            progressDialog.dismiss();
        }
    }
}
