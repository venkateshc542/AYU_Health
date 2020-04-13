package com.health.ayu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.health.utils.CoreApplication;
import com.health.utils.CustomSharedPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

/**
 * Created by venkatesh on 08-Apr-20.
 */

public class PatientDetailsActivity extends AppCompatActivity {

    CoreApplication application = null;
    TextInputEditText patient_name_edit,
            patient_age_edit,
            patient_location_edit,
            patient_desc_edit;

    RadioGroup patient_gender_rg;
    RadioButton patient_male_rb,
            patient_female_rb;


    TextView patient_pdf_text;
    Button patient_call_btn;

    private static final String AUTHORITY = "ayu";
    String name, age, gender, location, email, mobilenumber, apmtdate, desc;

    TextView name_text, gender_text;
    TextInputEditText patient_date_edit;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        application = (CoreApplication) getApplication();
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Appointment Details");
        tv.setTextSize(18);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        patient_name_edit = findViewById(R.id.patient_name_edit);
        patient_age_edit = findViewById(R.id.patient_age_edit);
        patient_gender_rg = findViewById(R.id.patient_gender_rg);
        patient_male_rb = findViewById(R.id.patient_male_rb);
        patient_female_rb = findViewById(R.id.patient_female_rb);
        patient_location_edit = findViewById(R.id.patient_location_edit);
        patient_desc_edit = findViewById(R.id.patient_desc_edit);
        patient_pdf_text = findViewById(R.id.patient_pdf_text);
        patient_call_btn = findViewById(R.id.patient_call_btn);

        name_text = findViewById(R.id.name_text);
        gender_text = findViewById(R.id.gender_text);
        patient_date_edit = findViewById(R.id.patient_date_edit);

        //getting data from the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            age = extras.getString("age");
            gender = extras.getString("gender");
            location = extras.getString("location");
            email = extras.getString("email");
            mobilenumber = extras.getString("mobilenumber");
            apmtdate = extras.getString("apmtdate");
            desc = extras.getString("desc");
        }

        name_text.setText(name);
        gender_text.setText(gender + " (" + age + ")");
        patient_name_edit.setText(email);
        patient_age_edit.setText(mobilenumber);
        patient_date_edit.setText(apmtdate);
        patient_location_edit.setText(location);
        patient_desc_edit.setText(desc);

        CustomSharedPreferences.saveStringData(PatientDetailsActivity.this, name, CustomSharedPreferences.SP_KEY.PATIENT_NAME);
        CustomSharedPreferences.saveStringData(PatientDetailsActivity.this, age, CustomSharedPreferences.SP_KEY.AGE);
        CustomSharedPreferences.saveStringData(PatientDetailsActivity.this, gender, CustomSharedPreferences.SP_KEY.GENDER);

        patient_name_edit.setEnabled(false);
        patient_age_edit.setEnabled(false);
        patient_gender_rg.setEnabled(false);
        patient_male_rb.setChecked(true);
        patient_male_rb.setEnabled(false);
        patient_female_rb.setEnabled(false);
        patient_location_edit.setEnabled(false);
        patient_desc_edit.setEnabled(false);
        patient_date_edit.setEnabled(false);


        patient_pdf_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermissionForWrite()) {
                        Log.i("IF", "if");
                        requestPermissionForWrite(1002);
                    } else {
                        CopyReadAssets(PatientDetailsActivity.this);
                        Log.i("ELSE", "else");
                    }
                } else {
                    CopyReadAssets(PatientDetailsActivity.this);
                }
            }
        });
        patient_call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientDetailsActivity.this, VideoCallActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void CopyReadAssets(PatientDetailsActivity activity) {
        AssetManager assetManager = activity.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Documents";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            in = assetManager.open("patient.pdf");
            out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Ayu/Documents/" + name + "_patient.pdf");
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        byte[] baos = null;
        //to view the pdf document
        viewPdf(activity, name + "_patient" + ".pdf", "Ayu/Documents", baos);
    }

    public void viewPdf(Context context, String file, String directory, byte[] baos) {
        try {
            File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directory + "/" + file);
            Uri path = Uri.fromFile(pdfFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (pdfFile.exists()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path.normalizeScheme(), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(context, AUTHORITY + ".provider", pdfFile));
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Intent ii = Intent.createChooser(i, "Open PDF with..");
                    try {
                        context.startActivity(ii);
                    } catch (ActivityNotFoundException e) {
                        //errorDialogue("Activity Not viewFound Exception");
                    }
                } else {
                    //errorDialogue("The file not exists!");
                }
            } else {
                if (pdfFile.exists()) {
                    //Checking for the file is exist or not
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //errorDialogue("Activity Not Found Exception");
                    }
                } else {
                    //errorDialogue("The file not exists!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void requestPermissionForWrite(int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PatientDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            errorDialogue("External storage permission needed. Please allow in App Settings for additional functionality");
        } else {
            ActivityCompat.requestPermissions(PatientDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, i);
        }
    }

    private boolean checkPermissionForWrite() {
        Log.i("checkPermission", "checkPermissionForCamera");
        int result = ContextCompat.checkSelfPermission(PatientDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent = new Intent(PatientDetailsActivity.this, PatientListActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PatientDetailsActivity.this, PatientListActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void errorDialogue(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientDetailsActivity.this, R.style.MyDialogTheme);
        //Creating dialog box
        builder.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true; // Consumed
                } else {
                    return false; // Not consumed
                }
            }
        });
        AlertDialog dialog = builder.create();
        //Setting the title manually
        builder.setCancelable(false);
        dialog.setTitle(Html.fromHtml("<font color='#E40046'>Error</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

    }

}