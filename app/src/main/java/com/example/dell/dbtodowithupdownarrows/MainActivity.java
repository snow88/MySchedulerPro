package com.example.dell.dbtodowithupdownarrows;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Todo> tasklist= new ArrayList<>();
    EditText etadd;
    Button btnadd;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean l;
        SharedPreferences sp = getSharedPreferences("mypref", 0);
        if(sp.getBoolean("firstrun",true)) {
            l = true;
            sp.edit().putBoolean("firstrun", false).commit();
        }
        else
            l= false;
        if(!l) {
            load();
            Toast.makeText(getApplicationContext(), "load() called.", Toast.LENGTH_SHORT).show();
        }

        etadd = findViewById(R.id.etadd);
        btnadd = findViewById(R.id.btnadd);
        lv = findViewById(R.id.lv);

        taskadapter tadap = new taskadapter();
        lv.setAdapter(tadap);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etadd.getText().toString();
                Todo t = new Todo(0, content,false);
                tasklist.add(t);
                tasklist.trimToSize();
                taskadapter tadap = new taskadapter();
                lv.setAdapter(tadap);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        save();
        Toast.makeText(getApplicationContext(), "save() called.", Toast.LENGTH_SHORT).show();
    }

    public void load()
    {
        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        SQLiteDatabase readDb = myDbHelper.getReadableDatabase();
        tasklist= TodoDBTable.getAllTodos(readDb);
    }

    public void save()
    {
        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        SQLiteDatabase writeDb = myDbHelper.getWritableDatabase();
        writeDb.execSQL("DELETE FROM " + TodoDBTable.TABLE_NAME + ";");
        for (Todo t : tasklist)
        {
            TodoDBTable.insertTodo(t, writeDb);
        }
    }

    class taskadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tasklist.size();
        }

        @Override
        public Todo getItem(int i) {
            return tasklist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater li = getLayoutInflater();
            View itemview = li.inflate(R.layout.listview_itemdetails,viewGroup,false);

            TextView tvtask = itemview.findViewById(R.id.tvtask);
            CheckBox chkbox = itemview.findViewById(R.id.chkbox);
            ImageButton imgbtn = itemview.findViewById(R.id.imgbtn);
            ImageButton btnarrowup = itemview.findViewById(R.id.btnarrowup);
            ImageButton btnarrowdown = itemview.findViewById(R.id.btnarrowdown);

            final Todo thisitem = getItem(i);

            tvtask.setText(thisitem.getTask());
            if(thisitem.getDone())
                chkbox.setChecked(true);
            else
                chkbox.setChecked(false);

            btnarrowup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = tasklist.indexOf(thisitem);
                    if(i!=0) {
                        Todo t = tasklist.remove(i);
                        tasklist.add(i - 1, thisitem);
                        notifyDataSetChanged();
                    }
                }
            });

            btnarrowdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = tasklist.indexOf(thisitem);
                    if (i<tasklist.size()-1)
                    {
                        Todo t = tasklist.remove(i);
                        tasklist.add(i + 1, thisitem);
                        notifyDataSetChanged();
                    }
                }
            });

            imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tasklist.remove(i);
                    notifyDataSetChanged();
                }
            });

            chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    tasklist.get(i).setDone(b);
                    notifyDataSetChanged();
                }
            });

            return itemview;
        }
    }

}
