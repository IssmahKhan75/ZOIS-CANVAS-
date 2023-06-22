package com.example.paintapplication;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
public class AzureBlobStorageManager {
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=zoiscanvas;AccountKey=SQj+KQbGU5Zhj6cH24tvLZBoAiRNM4LAOe9m++9SoUQ84Va2ilCROfuRsiv8qNu+UU4eOg3lx9KU+ASteyOzDA==;EndpointSuffix=core.windows.net";
    private static final String containerName = "paintapplication";

    public static void uploadImageToBlobStorage(String filePath, String fileName) {
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            File imageFile = new File(filePath);
            CloudBlockBlob blob = container.getBlockBlobReference(fileName);
            blob.upload(new FileInputStream(imageFile), imageFile.length());



            System.out.println("Image uploaded to Azure Blob Storage successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }
}