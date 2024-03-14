package com.example.currentplacedetailsonmap;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class pdfViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        WebView floorplan = findViewById(R.id.floorplan_webview);

        floorplan.loadUrl("https://www.bcit.ca/files/facilities/campusdev/pdf/floorplans/ne/bne0101.pdf");
        //floorplan.
    }
}