package com.test.mynote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Call Button
        ImageButton call = findViewById(R.id.call);
        call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+94751441764")); // Replace with the desired phone number
            startActivity(intent);
        });

        // Email Button
        ImageButton email = findViewById(R.id.email);
        email.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:deltacodexsoftwares@gmail.com")); // Replace with the recipient email
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry for Delta Codex Software Solution");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello! My Inquiry is.........");
            startActivity(intent);
        });

        // Web Button
        ImageButton web = findViewById(R.id.web);
        web.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://deltacodexgraphics.site/")); // Replace with the desired web URL
            startActivity(intent);
        });

        // Facebook Button
        ImageButton facebook = findViewById(R.id.facebook);
        facebook.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.facebook.com/profile.php?id=61553083106877")); // Replace with the Facebook URL
            startActivity(intent);
        });

        // Messenger Button
        ImageButton messenger = findViewById(R.id.messenger);
        messenger.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.facebook.com/profile.php?id=61570899520562")); // Replace with the Messenger profile ID or link
            startActivity(intent);
        });

        // WhatsApp Button
        ImageButton whatsapp = findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/qr/LYBYW3SM4QBOO1")); // Replace with the WhatsApp number (without '+' or spaces)
            startActivity(intent);
        });
    }
}

