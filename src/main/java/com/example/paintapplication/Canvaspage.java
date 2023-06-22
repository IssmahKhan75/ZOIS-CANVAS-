package com.example.paintapplication;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;


import android.content.SharedPreferences;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;
import android.Manifest;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class Canvaspage extends AppCompatActivity {
    int defaultcolor;
    SignatureView SV;
    ImageButton Imgeraser, Imgcolor, Imgsave,Imgbrush,Imgundo,Imgredo,Imgmark;
    SeekBar SB;
    SharedPreferences SH;

    TextView TVSize,TVop;
    private static String filename;
    CustomView CV ;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZOIS");

ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;
    private Paint paint;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvaspage);
        getSupportActionBar().hide();
        //////////////////intializations////////////////
        SV = findViewById(R.id.signature_view);
        Imgcolor = findViewById(R.id.btncolor);
        Imgeraser = findViewById(R.id.btneraser);
        Imgsave = findViewById(R.id.btnsave);
        Imgbrush=findViewById(R.id.btnbrush);
        Imgundo = findViewById(R.id.btnUndo);
        Imgredo = findViewById(R.id.btnRedo);
        Imgmark = findViewById(R.id.btnmark);
        SH = PreferenceManager.getDefaultSharedPreferences(this);
        scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        filename = path + "/" + date + ".png";
        if (!path.exists()) {
            path.mkdirs();
        }

        defaultcolor = ContextCompat.getColor(Canvaspage.this, R.color.black);

        Imgcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

        Imgeraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SV.clearCanvas();
            }
        });

        Imgsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSavePopupMenu(view);
            }
        });

        Imgbrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrushSettingsDialog();
            }
        });


// Set touch listener to handle zooming, panning, and drawing
        SV.setOnTouchListener(new View.OnTouchListener() {
            private float initialScale = 1.0f;
            private float prevX, prevY;
            private boolean isDrawingEnabled = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Handle scale gesture detection
                scaleGestureDetector.onTouchEvent(event);

                // Handle other touch events
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getPointerCount() == 1 && !isDrawingEnabled) {
                            // Perform panning if only one finger is used and not in drawing mode
                            prevX = event.getX();
                            prevY = event.getY();
                        }
                        // Handle the drawing logic here when the user starts drawing
                        isDrawingEnabled = true;
                        SV.onTouchEvent(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isDrawingEnabled) {
                            // Handle the drawing logic here for ongoing drawing
                            SV.onTouchEvent(event);
                        } else if (event.getPointerCount() == 2) {
                            // Perform panning if two fingers are used and zoomed in
                            float currX = event.getX();
                            float currY = event.getY();

                            float deltaX = currX - prevX;
                            float deltaY = currY - prevY;

                            // Calculate the translation values based on the current scale
                            float translationX = SV.getTranslationX() + deltaX / initialScale;
                            float translationY = SV.getTranslationY() + deltaY / initialScale;

                            // Apply the translation values
                            SV.setTranslationX(translationX);
                            SV.setTranslationY(translationY);

                            // Update the previous touch positions
                            prevX = currX;
                            prevY = currY;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getPointerCount() == 1) {
                            // Update the initial scale factor based on the current scale
                            initialScale = Math.max(SV.getScaleX(), SV.getScaleY());
                        }
                        // Handle the drawing logic here when the user stops drawing
                        isDrawingEnabled = false;
                        SV.onTouchEvent(event);
                        break;
                }

                return true;
            }
        });

        Imgundo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CV != null) {
                    CV.undo();
                }
            }
        });

        Imgredo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CV != null) {
                    CV.redo();
                }
            }
        });
        // Set OnClickListener for the save button
        Imgmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the SaveFileActivity and pass the file path to display the saved drawing
                Intent intent = new Intent(Canvaspage.this, SaveDrawing.class);
                startActivity(intent);
            }
        });
    }




    public void showSavePopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.savepopup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_device:
                        // Save on device logic
                        if (!SV.isBitmapEmpty()) {
                            try {
                                saveImage();
                            } catch (IOException e) {
                                Toast.makeText(Canvaspage.this, "Could not Save", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }
                        }
                        return true;
                    case R.id.menu_cloud:

                        // Save on cloud logic
                            try {
                               saveCloud();
                            } catch (Exception e) {
                                Toast.makeText(Canvaspage.this, "Could not Save on cloud", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }


                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


    /////////////save on local device ////////////////////
    private void saveImage() throws IOException {
        File file = new File(filename);
        Bitmap bitmap = SV.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //for diff image formats
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Toast.makeText(this, "Painting is Save in ZOIS Drawer", Toast.LENGTH_SHORT).show();
    }
    private void saveCloud() {
        Bitmap bitmap = SV.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();

        String fileName = "ZOIS" + System.currentTimeMillis() + ".png";
        File file = saveBitmapToFile(bitmapData, fileName);

        // Get the file path from the saved file
        String filePath = file.getAbsolutePath();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadImageToBlobStorage(filePath, fileName);
                    downloadImageFromBlobStorage(fileName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Drawing  uploaded to Azure Blob Storage", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private File saveBitmapToFile(byte[] bitmapData, String fileName) {
        File file = new File(getFilesDir(), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImageToBlobStorage(String filePath, String fileName) {
        try {
            // Replace with your connection string and container name
            String connectionString = "DefaultEndpointsProtocol=https;AccountName=zoiscanvas;AccountKey=SQj+KQbGU5Zhj6cH24tvLZBoAiRNM4LAOe9m++9SoUQ84Va2ilCROfuRsiv8qNu+UU4eOg3lx9KU+ASteyOzDA==;EndpointSuffix=core.windows.net";
            String containerName = "paintapplication";

            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(fileName);
            blob.uploadFromFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadImageFromBlobStorage(String fileName) {
        try {
            // Replace with your connection string and container name
            String connectionString = "DefaultEndpointsProtocol=https;AccountName=zoiscanvas;AccountKey=SQj+KQbGU5Zhj6cH24tvLZBoAiRNM4LAOe9m++9SoUQ84Va2ilCROfuRsiv8qNu+UU4eOg3lx9KU+ASteyOzDA==;EndpointSuffix=core.windows.net";
            String containerName = "paintapplication";

            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(fileName);
            File localFile = new File(getFilesDir(), fileName);
            blob.downloadToFile(localFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void openColorPicker() {
            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultcolor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    defaultcolor = color;
                    SV.setPenColor(color);
                }
            });
            ambilWarnaDialog.show();
        }

        private void askPermission() {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            Toast.makeText(Canvaspage.this, "Granted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
        }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            // Apply the scale factor to the SignatureView
            SV.setScaleX(scaleFactor);
            SV.setScaleY(scaleFactor);

            return true;
        }
    }
    public void showBrushSettingsDialog() {
        // Create a dialog instance
        Dialog brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.dialoguebrush);
        brushDialog.setTitle("Brush Settings");
        askPermission();
// Initialize image buttons and seek bars
        ImageButton btnFlatBrush = brushDialog.findViewById(R.id.btnFlatBrush);
        ImageButton btnRoundBrush = brushDialog.findViewById(R.id.btnRoundBrush);
        ImageButton btnpencil = brushDialog.findViewById(R.id.btnpencil);
        ImageButton btneraser = brushDialog.findViewById(R.id.btneraser);
        SeekBar seekBarBrushSize = brushDialog.findViewById(R.id.seekBarBrushSize);
        SeekBar seekBarOpacity = brushDialog.findViewById(R.id.seekBarOpacity);
         TVop = findViewById(R.id.txtOpacity);
         TVSize = findViewById(R.id.txtPenSize);
        seekBarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                SV.setPenSize(i);
                seekBar.setMax(100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Opacity seek bar listener
        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                // Calculate the opacity based on the progress value
                float maxOpacity = 1.0f; // Maximum opacity value
                float opacity = (float) i / seekBar.getMax() * maxOpacity;

                SV.setAlpha(opacity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO: Handle opacity seek bar touch start
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO: Handle opacity seek bar touch stop
            }
        });

        // Apply button click listener
        Button btnApply = brushDialog.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle apply button click
                brushDialog.dismiss();
            }
        });
        // Set initial brush type
        btnFlatBrush.setSelected(true);
        btnRoundBrush.setSelected(false);

        // Brush type button click listeners
        btnFlatBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlatBrush.setSelected(true);
                btnRoundBrush.setSelected(false);
// Set the paint properties for the flat brush
          setFlatBrush();


            }
        });

        btnRoundBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlatBrush.setSelected(false);
                btnRoundBrush.setSelected(true);

setRoundBrush();
            }
        });
        btnpencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlatBrush.setSelected(false);
                btnRoundBrush.setSelected(true);

                setpencil();
            }
        });
        btneraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlatBrush.setSelected(false);
                btnRoundBrush.setSelected(true);

                seteraser();
            }
        });
        // Show the brush settings dialog box
        brushDialog.show();


    }
    private void setFlatBrush() {

        SV.setPenColor(Color.BLACK);
        SV.setPenSize(50);



    }
    private void setRoundBrush() {

        SV.setPenColor(Color.BLACK);
        SV.setPenSize(20);



    }
    private void setpencil() {

        SV.setPenColor(Color.GRAY);
        SV.setPenSize(1);



    }
    private void seteraser() {

        SV.setPenColor(Color.WHITE);
        SV.setPenSize(50);



    }
    public class CustomView extends View {
        private List<Path> paths = new ArrayList<>(); // List to store paths
        private List<Path> undonePaths = new ArrayList<>(); // List to store undone paths
        private Paint paint;
        private Path currentPath;

        public CustomView(Context context) {
            super(context);
            init();
        }

        public CustomView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            paths = new ArrayList<>();
            undonePaths = new ArrayList<>();

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5f);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw all the paths
            for (Path path : paths) {
                canvas.drawPath(path, paint);
            }

            // Draw the current path
            if (currentPath != null) {
                canvas.drawPath(currentPath, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Start a new path
                    currentPath = new Path();
                    currentPath.moveTo(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // Add points to the current path
                    if (currentPath != null) {
                        currentPath.lineTo(x, y);
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    // Finish the current path
                    if (currentPath != null) {
                        paths.add(currentPath);
                        currentPath = null;
                        invalidate();
                    }
                    break;
            }

            return true;
        }

        public void undo() {
            if (!paths.isEmpty()) {
                Path removedPath = paths.remove(paths.size() - 1);
                undonePaths.add(removedPath);
                invalidate();
            }
        }

        public void redo() {
            if (!undonePaths.isEmpty()) {
                Path restoredPath = undonePaths.remove(undonePaths.size() - 1);
                paths.add(restoredPath);
                invalidate();
            }
        }
    }

}

