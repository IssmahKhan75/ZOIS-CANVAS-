package com.example.paintapplication;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

public class MENUScreen extends AppCompatActivity {
    private Button drawButton;
    private SharedPreferences sharedPreferences;
    private Button settingButton;
    private Button cloudbtn;
    private Button showsavebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuscreen);
        getSupportActionBar().hide();
        //////////draw button
        drawButton = findViewById(R.id.imageButton);

        /////////////////////setting button
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        settingButton = findViewById(R.id.settingbtn);
//////////////draw on click////////////////////////
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        /////////////////////////draw setting onclick///////////////////
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        cloudbtn = findViewById(R.id.cloudbtn);

        cloudbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Azure Blob Storage container in a web browser
                String storageContainerUrl = "https://portal.azure.com/#view/Microsoft_Azure_Storage/ContainerMenuBlade/~/overview/storageAccountId/%2Fsubscriptions%2F3abda69e-e8f0-42d1-9017-363766066075%2FresourceGroups%2FZois_World%2Fproviders%2FMicrosoft.Storage%2FstorageAccounts%2Fzoiscanvas/path/paintapplication/etag/%220x8DB6F6AE92B0D01%22/defaultEncryptionScope/%24account-encryption-key/denyEncryptionScopeOverride~/false/defaultId//publicAccessVal/None";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storageContainerUrl));
                startActivity(intent);
            }
        });
        ///////////save drawings here ////////////////
        showsavebtn = findViewById(R.id.savefilebtn);
        showsavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSavedrawings();
            }
        });
    }



    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_portrait:
                                // Perform action for Portrait canvas size
                                // e.g., set canvas size to portrait dimensions
                                int portraitColor = Color.parseColor("#E5E4E3");
                                int portraitWidth = 800; // Update with the desired width
                                int portraitHeight = 800; // Update with the desired height
                                navigateToOtherActivity(portraitWidth, portraitHeight,portraitColor);
                                return true;
                            case R.id.menu_landscape:
                                // Perform action for Landscape canvas size
                                // e.g., set canvas size to landscape dimensions
                                int landscapetColor =Color.parseColor("#E5E4E3");
                                int landscapeWidth = 1200; // Update with the desired width
                                int landscapeHeight = 300; // Update with the desired height
                                navigateToOtherActivity(landscapeWidth, landscapeHeight,landscapetColor);
                                return true;
                            case R.id.menu_square:
                                // Perform action for Square canvas size
                                // e.g., set canvas size to square dimensions
                                int squareColor = Color.parseColor("#E5E4E3");
                                int squareSize = 500; // Update with the desired size
                                navigateToOtherActivity(squareSize, squareSize,squareColor);
                                return true;
                            default:
                                return false;
                        }
            }
        });
        popupMenu.show();
    }
    private void navigateToOtherActivity(int canvasWidth, int canvasHeight,int canvasColor) {
        Intent intent = new Intent(MENUScreen.this, Canvaspage.class);
        intent.putExtra("canvasWidth", canvasWidth);
        intent.putExtra("canvasHeight", canvasHeight);
        intent.putExtra("canvasColor", canvasColor);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    private void openSavedrawings() {
        Intent intent = new Intent(MENUScreen.this, SaveDrawing.class);
        startActivity(intent);
    }

}
