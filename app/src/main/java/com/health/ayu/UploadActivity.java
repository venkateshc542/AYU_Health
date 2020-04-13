package com.health.ayu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.health.pojo.CropOption;
import com.health.utils.CoreApplication;
import com.health.utils.CustomSharedPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static android.view.View.VISIBLE;

/**
 * Created by venkatesh on 08-Apr-20.
 */

public class UploadActivity extends AppCompatActivity {

    CoreApplication application = null;
    TextView upload_text,
            upload_succes_text,
            review_upload_text;
    Button upload_submit_btn;
    public static final int FILE_SELECT_CODE = 130;
    TextInputEditText upload_name_edit;

    private String upload_pdf_data = "";
    public final static double MAX_FILE_SIZE = 5;
    private static final String AUTHORITY = "ayu";
    TextView name_text, gender_text;

    private static final int CAMERA_REQUEST = 123;
    private static final int CROP_FROM_CAMERA = 124;
    private String directory = "Photo1";
    private Uri mImageCaptureUri;

    TextView item_img_attach_text;

    LinearLayout item_image_capture_layout;
    ImageView item_camera_img;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        application = (CoreApplication) getApplication();

        //setup toolbar
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Upload Documents");
        tv.setTextSize(18);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initilaising the UI
        upload_name_edit = findViewById(R.id.upload_name_edit);
        upload_succes_text = findViewById(R.id.upload_succes_text);
        upload_submit_btn = findViewById(R.id.upload_submit_btn);
        upload_text = findViewById(R.id.upload_text);
        review_upload_text = findViewById(R.id.review_upload_text);

        item_camera_img = findViewById(R.id.item_camera_img);
        item_img_attach_text = findViewById(R.id.item_img_attach_text);
        item_image_capture_layout = findViewById(R.id.item_image_capture_layout);

        item_camera_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagebtnOnclick(CAMERA_REQUEST);
            }
        });

        final String name = CustomSharedPreferences.getStringData(UploadActivity.this, CustomSharedPreferences.SP_KEY.PATIENT_NAME);
        String age = CustomSharedPreferences.getStringData(UploadActivity.this, CustomSharedPreferences.SP_KEY.AGE);
        String gender = CustomSharedPreferences.getStringData(UploadActivity.this, CustomSharedPreferences.SP_KEY.GENDER);
        upload_name_edit.setText(name);
        upload_name_edit.setEnabled(false);

        name_text = findViewById(R.id.name_text);
        gender_text = findViewById(R.id.gender_text);
        name_text.setText(name);
        gender_text.setText(gender + " (" + age + ")");

        review_upload_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upload_succes_text.getVisibility() == View.VISIBLE || item_img_attach_text.getVisibility() == VISIBLE) {
                    if (upload_pdf_data != null && !upload_pdf_data.isEmpty()) {
                        Bitmap bitmap = stringToBitmap(upload_pdf_data);
                        try {
                            String imageDataBytes = upload_pdf_data.substring(upload_pdf_data.indexOf(",") + 1);
                            byte[] encodeByte = Base64.decode(imageDataBytes, Base64.DEFAULT);
                            //To find Mime type of the document
                            String imageMimeType = getImageType(encodeByte);
                            if (imageMimeType.equals("application/pdf")) {
                                //selected document as a PDF
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Documents";
                                File dir = new File(path);
                                if (!dir.exists())
                                    dir.mkdirs();
                                File file = new File(dir, name + "_rx.pdf");
                                //byte[] pdfAsBytes = Base64.decode(imageDataBytes, 0);
                                FileOutputStream os;
                                os = new FileOutputStream(file, false);
                                os.write(encodeByte);
                                os.flush();
                                os.close();
                                //to view Document
                                viewPdf(UploadActivity.this, name + "_rx.pdf", "Ayu/Documents", encodeByte);
                            } else {
                                imageDialogue(bitmap, upload_pdf_data, item_img_attach_text, "SUBMIT", 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        upload_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("*/*");      //all files
                //intent.setType("text/xml");   //XML file only
                intent.setType("application/pdf");//PDF only
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(UploadActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        upload_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upload_pdf_data.equals("") || upload_pdf_data.isEmpty()) {
                    errorDialogue("Please upload the prescription document to the patient..! ");
                    return;
                } else {
                    Intent intent = new Intent(UploadActivity.this, FinalActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    private void imagebtnOnclick(int typeOfRequest) {
        if (!checkPermissionForCamera()) {
            Log.i("IF", "if");
            requestPermissionForCamera(typeOfRequest);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionForWrite()) {
                Log.i("IF", "if");
                requestPermissionForWrite(1002);
            } else {
                Log.i("ELSE", "else");
                takePhoto(directory, typeOfRequest);
            }
        } else {
            takePhoto(directory, typeOfRequest);
        }
    }

    private void takePhoto(String dirName, int requestCode) {
        Log.i("ELSE", "else");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Images";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, dirName + ".jpg");
        mImageCaptureUri = FileProvider.getUriForFile(UploadActivity.this, AUTHORITY + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void requestPermissionForCamera(int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, android.Manifest.permission.CAMERA)) {
            errorDialogue("Camera permission needed. Please allow in App Settings for additional functionality");
        } else {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[]{android.Manifest.permission.CAMERA}, i);
        }
    }

    private boolean checkPermissionForCamera() {
        Log.i("checkPermission", "checkPermissionForCamera");
        int result = ContextCompat.checkSelfPermission(UploadActivity.this, android.Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void viewLoanCardDoc(String upload_pdf_data, String filename) {
        if (upload_pdf_data != null && !upload_pdf_data.isEmpty()) {
            Bitmap bitmap = stringToBitmap(upload_pdf_data);
            try {
                String imageDataBytes = upload_pdf_data.substring(upload_pdf_data.indexOf(",") + 1);
                byte[] encodeByte = Base64.decode(imageDataBytes, Base64.DEFAULT);
                //To find Mime type of the document
                String imageMimeType = getImageType(encodeByte);
                if (imageMimeType.equals("application/pdf")) {
                    //selected document as a PDF
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Documents";
                    File dir = new File(path);
                    if (!dir.exists())
                        dir.mkdirs();
                    File file = new File(dir, filename + "_rx.pdf");
                    //byte[] pdfAsBytes = Base64.decode(imageDataBytes, 0);
                    FileOutputStream os;
                    os = new FileOutputStream(file, false);
                    os.write(encodeByte);
                    os.flush();
                    os.close();
                    //to view Document
                    viewPdf(UploadActivity.this, filename + "_rx.pdf", "Ayu/Documents", encodeByte);
                } else {
                    errorDialogue("Not supported file type..!");
                    //imageDialogue(bitmap, layout, imgText, imageData, "VIEW", number);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getImageType(byte[] data) {
        //        filetype    magic number(hex)
//        jpg         FF D8 FF
//        gif         47 49 46 38
//        png         89 50 4E 47 0D 0A 1A 0A
//        bmp         42 4D
//        tiff(LE)    49 49 2A 00
//        tiff(BE)    4D 4D 00 2A89 50 4E 47 0D 0A 1A 0A
//        PDF         25 50 44 46

        final byte[] pngPattern = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        final byte[] jpgPattern = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        final byte[] gifPattern = new byte[]{0x47, 0x49, 0x46, 0x38};
        final byte[] bmpPattern = new byte[]{0x42, 0x4D};
        final byte[] tiffLEPattern = new byte[]{0x49, 0x49, 0x2A, 0x00};
        final byte[] tiffBEPattern = new byte[]{0x4D, 0x4D, 0x00, 0x2A};
        final byte[] pdfPattern = new byte[]{0x25, 0x50, 0x44, 0x46};
        if (isMatch(pngPattern, data))
            return "image/png";

        if (isMatch(jpgPattern, data))
            return "image/jpg";

        if (isMatch(gifPattern, data))
            return "image/gif";

        if (isMatch(bmpPattern, data))
            return "image/bmp";

        if (isMatch(tiffLEPattern, data))
            return "image/tif";

        if (isMatch(tiffBEPattern, data))
            return "image/tif";

        if (isMatch(pdfPattern, data))
            return "application/pdf";

        return "image/png";
    }

    private boolean isMatch(byte[] pattern, byte[] data) {
        if (pattern.length <= data.length) {
            for (int idx = 0; idx < pattern.length; ++idx) {
                if (pattern[idx] != data[idx])
                    return false;
            }
            return true;
        }
        return false;
    }

    private Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case CAMERA_REQUEST:
                doCrop(124);
                break;
            case CROP_FROM_CAMERA:
                File file0 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "/Ayu/" + "Images" + "/" + directory + ".jpg");
                mImageCaptureUri = FileProvider.getUriForFile(UploadActivity.this, AUTHORITY + ".provider", file0);


                if (file0.exists()) {
                    Bitmap photo1 = BitmapFactory.decodeFile(file0.getAbsolutePath());
                    Bitmap mPhoto1 = Bitmap.createScaledBitmap(photo1, 500, 500, true);
                    try {
                        String fileSize = getSize(file0.length());
                        String[] size = fileSize.split(" ");
                        String MB_value = size[0];
                        String finalSize = size[0] + size[1];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        if (finalSize.contains("MB")) {
                            if (Double.parseDouble(MB_value) < 1) {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 75, baos); //bm is the bitmap object
                            } else if (Double.parseDouble(MB_value) < 2) {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 50, baos); //bm is the bitmap object
                            } else if (Double.parseDouble(MB_value) < 3) {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 50, baos); //bm is the bitmap object
                            } else if (Double.parseDouble(MB_value) < 4) {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 40, baos); //bm is the bitmap object
                            } else if (Double.parseDouble(MB_value) < 5) {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 30, baos); //bm is the bitmap object
                            } else {
                                mPhoto1.compress(Bitmap.CompressFormat.PNG, 75, baos); //bm is the bitmap object
                            }
                        } else {
                            mPhoto1.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        }
                        byte[] imageData = baos.toByteArray();
                        upload_pdf_data = Base64.encodeToString(imageData, Base64.NO_WRAP);
                        imageDialogue(mPhoto1, upload_pdf_data, item_img_attach_text, "SUBMIT", 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File f1 = new File(mImageCaptureUri.getPath());
                if (f1.exists()) f1.delete();
                break;

            case FILE_SELECT_CODE:
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //String picturePath = cursor.getString(columnIndex);
                int size = 0;
                int read = 0;

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;

                    String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Documents";
                    File dir1 = new File(path1);
                    if (!dir1.exists())
                        dir1.mkdirs();
                    File file2 = new File(dir1, upload_name_edit.getText().toString() + "_rx.pdf");
                    FileOutputStream fOut = new FileOutputStream(file2);
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                        fOut.write(buffer, 0, len);
                    }
                    fOut.flush();
                    fOut.close();

                    String fileSize = getSize(file2.length());
                    String[] size00 = fileSize.split(" ");
                    String MB_value = size00[0];
                    String finalSize = size00[0] + size00[1];
                    if (finalSize.contains("MB")) {
                        if (Double.parseDouble(MB_value) > MAX_FILE_SIZE) {
                            errorDialogue("Maximum upload file size is: 5 MB.  Please choose size below 5MB file.");
                            upload_pdf_data = "";
                            upload_succes_text.setVisibility(View.GONE);
                            review_upload_text.setVisibility(View.GONE);
                            upload_succes_text.setText("");
                        } else {
                            upload_pdf_data = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
                            upload_succes_text.setVisibility(View.VISIBLE);
                            upload_succes_text.setText("Prescription document uploaded successfully..!");
                            review_upload_text.setVisibility(View.VISIBLE);
                            item_img_attach_text.setVisibility(View.GONE);
                        }
                    } else {
                        upload_pdf_data = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
                        upload_succes_text.setVisibility(View.VISIBLE);
                        review_upload_text.setVisibility(View.VISIBLE);
                        upload_succes_text.setText("Prescription document uploaded successfully..!");
                        item_img_attach_text.setVisibility(View.GONE);

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    upload_succes_text.setVisibility(View.GONE);
                    review_upload_text.setVisibility(View.GONE);
                    upload_succes_text.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                    upload_succes_text.setVisibility(View.GONE);
                    review_upload_text.setVisibility(View.GONE);
                    upload_succes_text.setText("");
                }
                cursor.close();
            default:
                break;
        }
    }

    private void imageDialogue(Bitmap mPhoto1, String image_data, final TextView item_img_attach_text, String submit, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this, R.style.MyDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.image_alert, null);
        ImageView imageView = v.findViewById(R.id.image);
        imageView.setImageBitmap(mPhoto1);
        builder.setView(v);
        item_img_attach_text.setVisibility(VISIBLE);
        review_upload_text.setVisibility(View.VISIBLE);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item_image_capture_layout.setVisibility(VISIBLE);
                upload_succes_text.setVisibility(View.GONE);
                upload_succes_text.setText("");
                //String image_name = prospect_details_fn_edit.getText().toString();
                item_img_attach_text.setText(".JPEG Attached");
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
        dialog.setTitle(Html.fromHtml("<font color='#18A4A8'>Ayu Health</font>"));
        builder.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void doCrop(final int requestCode) {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            //you must setup two line below
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("outputX", 360);
            intent.putExtra("outputY", 360);
            intent.putExtra("aspectX", 4);
            intent.putExtra("aspectY", 3);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, requestCode);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();
                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }
                CropOptionAdapter adapter = new CropOptionAdapter(UploadActivity.this, cropOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(1).appIntent, requestCode);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private String getSize(double size) {
        String s = "";
        double kb = size / 1024;
        double mb = kb / 1024;
        double gb = kb / 1024;
        double tb = kb / 1024;
        if (size < 1024) {
            s = size + " Bytes";
        } else if (size >= 1024 && size < (1024 * 1024)) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size >= (1024 * 1024) && size < (1024 * 1024 * 1024)) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size >= (1024 * 1024 * 1024) && size < (1024 * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", gb) + " GB";
        } else if (size >= (1024 * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    private void CopyReadAssets(UploadActivity activity) {
        AssetManager assetManager = activity.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayu/" + "Documents";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            in = assetManager.open("patient.pdf");
            out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Ayu/Documents/" + "patient.pdf");
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
        viewPdf(activity, "patient" + ".pdf", "Ayu/Documents", baos);
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            errorDialogue("External storage permission needed. Please allow in App Settings for additional functionality");
        } else {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, i);
        }
    }

    private boolean checkPermissionForWrite() {
        Log.i("checkPermission", "checkPermissionForCamera");
        int result = ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent = new Intent(UploadActivity.this, PatientListActivity.class);
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
        Intent intent = new Intent(UploadActivity.this, PatientListActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void errorDialogue(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this, R.style.MyDialogTheme);
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