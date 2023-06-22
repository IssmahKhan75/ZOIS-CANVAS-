package com.example.paintapplication;



import android.graphics.Bitmap;

import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class DrawingItem {
    private String drawingName;

    public DrawingItem(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getDrawingName() {
        return drawingName;
    }
}
