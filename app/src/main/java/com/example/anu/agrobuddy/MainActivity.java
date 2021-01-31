package com.example.anu.agrobuddy;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {
    Button Pic;
    Button Pic2;
    Button Remedy;
    File photofile;
    Interpreter tflite;
    TextView resultShow;
    ImageView picResult;
    String pathtoFile;
    String Response;
    Bitmap bitmap2;
    Bitmap bitmap1;
    Bitmap bitmap3;
    Bitmap image;
    Uri photoURI;
    private static final String TAG = "TfLiteCameraDemo";
    private static final String MODEL_PATH = "pulkit_ly_rm.tflite";
    private String UploadUrl = "https://agrobuddy.herokuapp.com/predict?api_key=deadalive";
    private StorageReference mStroage;

    /** Name of the label file stored in Assets. */
    private static final String LABEL_PATH = "labels.txt";

    /** Number of results to show in the UI. */
    private static final int RESULTS_TO_SHOW = 3;

    /** Dimensions of inputs. */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    static final int DIM_IMG_SIZE_X = 64;
    static final int DIM_IMG_SIZE_Y = 64;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;


    /* Preallocated buffers for storing image data in. */
    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    /** An instance of the driver class to run model inference with Tensorflow Lite. */

    /** Labels corresponding to the output of the vision model. */
    private List<String> labelList;

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs. */
    private ByteBuffer imgData = null;

    /** An array to hold inference results, to be feed into Tensorflow Lite as outputs. */
    private float[][] labelProbArray = null;
    /** multi-stage low pass filter **/
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i1 = getIntent();
        int x=i1.getIntExtra("x",0);
        if(x==0) {
            startActivity(new Intent(MainActivity.this, Splash.class));
        }


        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));


        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());
        mStroage= FirebaseStorage.getInstance().getReference();
        Pic=findViewById(R.id.pic);
        Pic.setVisibility(View.INVISIBLE);
        Pic2=findViewById(R.id.pic2);
        Remedy=findViewById(R.id.Remedy);

        picResult=findViewById(R.id.imageView2);
        picResult.setVisibility(View.INVISIBLE);
        resultShow=findViewById(R.id.result);
        resultShow.setVisibility(View.INVISIBLE);
        Remedy.setVisibility(View.INVISIBLE);


        if(Build.VERSION.SDK_INT>=23)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        /*try
        {
            tflite=new Interpreter(loadModelFile());
            labelList = loadLabelList();
            imgData =
                    ByteBuffer.allocateDirect(
                            4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
            imgData.order(ByteOrder.nativeOrder());
            labelProbArray = new float[1][labelList.size()];
            Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }*/

    }
    public void Cam(View view)
    {
        switch (view.getId())
        {
            case R.id.pic:
                PictureAction(1);
                break;
            case R.id.pic2:
                PictureAction(2);
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    if(photofile.exists())
                    {
                        //Toast.makeText(this,"The image file was save at "+photofile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"Predicting...",Toast.LENGTH_SHORT).show();
                        Bitmap bitmap= BitmapFactory.decodeFile(pathtoFile);
                        bitmap1=Bitmap.createScaledBitmap(bitmap,480,480,true);
                        picResult.setVisibility(View.VISIBLE);
                        picResult.setImageBitmap(bitmap1);
                        picResult.setRotation(270);
                        bitmap2=Bitmap.createScaledBitmap(bitmap,DIM_IMG_SIZE_X,DIM_IMG_SIZE_Y,true);
                        String finalResult=classifyFrame(bitmap2);
                        resultShow.setVisibility(View.VISIBLE);
                        resultShow.setText(finalResult);
                    }
                    else
                    {
                        Toast.makeText(this,"Error! in saving the image file",Toast.LENGTH_LONG).show();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        if(requestCode==2)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    if(photofile.exists()) {
                        //Toast.makeText(this,"The image file was save at "+photofile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Predicting...", Toast.LENGTH_SHORT).show();
                        //Uri uri=data.getData();
                        StorageReference filepath=mStroage.child(photoURI.getPath());
                        filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });

                        Bitmap bitmap = BitmapFactory.decodeFile(pathtoFile);
                        bitmap3=Bitmap.createScaledBitmap(bitmap,64,64,true);
                        try {
                            bitmap1=modifyOrientation(bitmap,pathtoFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bitmap2=Bitmap.createScaledBitmap(bitmap1,600,600,true);
                        picResult.setVisibility(View.VISIBLE);
                        picResult.setImageBitmap(bitmap2);

                        uploadImage(bitmap3);
                    }
                    else
                    {
                        Toast.makeText(this,"Error! in saving the image file",Toast.LENGTH_LONG).show();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }

    }
    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void PictureAction(int x)
    {
        Intent takePic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager())!=null)
        {
            photofile=null;
            photofile=createPhotoFile();
            if(photofile!=null) {
                pathtoFile = photofile.getAbsolutePath();
                photoURI= FileProvider.getUriForFile(MainActivity.this,"com.thecodecity.cameraandroid.fileprovider",photofile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                if(x==1) {
                    startActivityForResult(takePic, 1);
                }
                else {
                    startActivityForResult(takePic, 2);
                }
            }
        }
    }
    private File createPhotoFile()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try
        {
            image=File.createTempFile(imageFileName,".jpg",storageDir);
        }catch (Exception e)
        {
            Log.d("mylog","Excep"+e.toString());
        }
        return image;

    }
    String classifyFrame(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            return "Uninitialized Classifier.";
        }
        convertBitmapToByteBuffer(bitmap);
        // Here's where the magic happens!!!
        tflite.run(imgData, labelProbArray);


        // print the results
        String textToShow = printTopKLabels();
        return textToShow;
    }
    public void close() {
        tflite.close();
        tflite = null;
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }

    }
    private String printTopKLabels() {
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        String textToShow = "";
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            textToShow = String.format("\n%s: %4.2f",label.getKey(),label.getValue()) + textToShow;
        }
        return textToShow;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);

    }
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private void uploadImage(final Bitmap bitmap)
    {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("image", imageToString(bitmap));

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,UploadUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Response= null;
                        try {
                            Response = response.getString("Prediction");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultShow.setVisibility(View.VISIBLE);
                        resultShow.setText(Response);
                        if(Response!=null)
                        {
                            Remedy.setVisibility(View.VISIBLE);
                        }
                        //Process os success response
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

// add the request object to the queue to be executed
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request_json);


    }
    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.english)
        {
            setLocale("en");
            recreate();

        }
        if(item.getItemId()==R.id.hindi)
        {
            setLocale("hi");
            recreate();

        }
        return super.onOptionsItemSelected(item);
    }
    private void setLocale(String lang)
    {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }
    public void loadLocale()
    {
        SharedPreferences prefs=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        String language=prefs.getString("My_Lang","");
        setLocale(language);
    }
    public void Remedy(View view)
    {
        Intent i1 = new Intent(MainActivity.this, Remedy.class);
        i1.putExtra("message",Response);
        startActivity(i1);
        //startActivity(new Intent(MainActivity.this,Remedy.class));
    }



}

