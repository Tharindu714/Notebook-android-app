package com.test.mynote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.mynote.model.SQLiteHelper;

public class MainActivity extends AppCompatActivity {
    private SQLiteHelper sqLiteHelper;
    private RecyclerView recyclerView;
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        sqLiteHelper = new SQLiteHelper(
                MainActivity.this,
                "MyNoteBook.sqlite",
                null, 1
        );

        // Button to create a new note
        findViewById(R.id.button).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView1 = findViewById(R.id.recyclerView);

        TouchHelper touchHelper = new TouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView1);

        loadNotes(); // Refresh the notes list when returning to this activity

    }


    protected void loadNotes() {
        new Thread(() -> {
            SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    "notes",
                    null,
                    null,
                    null, null,
                    null,
                    "`id` DESC");

            runOnUiThread(() -> {
                noteListAdapter = new NoteListAdapter(cursor);
                recyclerView.setAdapter(noteListAdapter);
            });
        }).start();
    }
}

class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView contentView;
        TextView date_Created;

        View containerView;

        String id;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleText);
            contentView = itemView.findViewById(R.id.contentText);
            date_Created = itemView.findViewById(R.id.dateText);
            containerView = itemView;
        }
    }

    Cursor cursor;

    public NoteListAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.id = cursor.getString(0);
        String title = cursor.getString(1);
        String content = cursor.getString(2);
        String date = cursor.getString(3);

        holder.titleView.setText(title);
        holder.contentView.setText(content);
        holder.date_Created.setText(date);
        holder.containerView.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(), CreateNoteActivity.class);
            intent.putExtra("id", holder.id);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }
}

class TouchHelper extends ItemTouchHelper.Callback {
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        NoteListAdapter.NoteViewHolder holder = (NoteListAdapter.NoteViewHolder) viewHolder;
        Context context = viewHolder.itemView.getContext();
        // Create the confirmation dialog
        new AlertDialog.Builder(context)
                .setTitle("Delete My Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the deletion
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(
                            context,
                            "MyNoteBook.sqlite",
                            null, 1
                    );

                    new Thread(() -> {
                        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                        int row = sqLiteDatabase.delete(
                                "notes",
                                "`id`=?",
                                new String[]{holder.id}
                        );
                        Log.i("MyNoteBookLog", row + " Row Deleted");
                        // Notify the MainActivity to refresh the list
                        ((Activity) context).runOnUiThread(() -> {
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).loadNotes();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Cancel the swipe action and reset the item
                    ((Activity) context).runOnUiThread(() -> {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).loadNotes();
                        }
                    });
                })
                .setOnCancelListener(dialog -> {
                    // Reset the item if the dialog is canceled
                    ((Activity) context).runOnUiThread(() -> {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).loadNotes();
                        }
                    });
                })
                .show();
    }
}