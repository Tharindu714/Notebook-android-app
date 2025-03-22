package com.test.mynote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.test.mynote.model.SQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        EditText editTitleText = findViewById(R.id.editTextText);
        EditText editContentText = findViewById(R.id.editTextTextMultiLine);

        if (title != null) {
            editTitleText.setText(title);
        }

        if (content != null) {
            editContentText.setText(content);
        }

        Button saveNoteBtn = findViewById(R.id.button2);
        saveNoteBtn.setOnClickListener(view -> {


            if (editTitleText.getText().toString().isEmpty()) {
                Toast.makeText(CreateNoteActivity.this, "Please Fill The Title", Toast.LENGTH_LONG).show();
            } else if (editContentText.getText().toString().isEmpty()) {
                Toast.makeText(CreateNoteActivity.this, "Please Fill The Content", Toast.LENGTH_LONG).show();

            } else {
                SQLiteHelper sqLiteHelper = new SQLiteHelper(
                        CreateNoteActivity.this,
                        "MyNoteBook.sqlite",
                        null, 1
                );
                new Thread(() -> {
                    SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put("title", editTitleText.getText().toString());
                    contentValues.put("content", editContentText.getText().toString());

                    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    contentValues.put("date_created", SDF.format(new Date()));

                    if (id!=null){
                        int count = sqLiteDatabase.update(
                                "notes",
                                contentValues,
                                "`id`=?",
                                new String[]{id}
                        );
                        Log.i("MyNoteBookLog",count+" Row Updated");
                    }else{
                        long insertID = sqLiteDatabase.insert("notes", null, contentValues);
                        Log.i("MyNoteBookLog", String.valueOf(insertID));
                    }
                    sqLiteDatabase.close();

                    runOnUiThread(() -> {
                        editTitleText.setText("");
                        editContentText.setText("");

                        editTitleText.requestFocus();
                    });

                }).start();
                Toast.makeText(CreateNoteActivity.this, "Note Saved Successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreateNoteActivity.this, MainActivity.class);
                startActivity(i);
            }

        });
    }
}
