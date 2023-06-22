package com.example.paintapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DrawingAdapter extends ArrayAdapter<DrawingItem> {
    private Context context;
    private List<DrawingItem> drawingItems;

    public DrawingAdapter(Context context, List<DrawingItem> drawingItems) {
        super(context, 0, drawingItems);
        this.context = context;
        this.drawingItems = drawingItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drawing, parent, false);
        }

        DrawingItem item = drawingItems.get(position);

        TextView drawingNameTextView = convertView.findViewById(R.id.titleTextView);
        drawingNameTextView.setText(item.getDrawingName());

        ImageButton drawingImageButton = convertView.findViewById(R.id.imageButton);
        drawingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the drawing image button
                // You can perform any action here, such as opening a detailed view of the drawing
                Toast.makeText(context, "Drawing clicked: " + item.getDrawingName(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
