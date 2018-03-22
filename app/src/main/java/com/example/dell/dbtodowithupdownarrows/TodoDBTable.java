package com.example.dell.dbtodowithupdownarrows;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static com.example.dell.dbtodowithupdownarrows.Consts.*;

/**
 * Created by Dell on 12-01-2018.
 */

public class TodoDBTable {

    private TodoDBTable() {
    }

    public static final String TABLE_NAME = "todos";

    public interface Columns {
        String ID = "id";
        String TASK = "task";
        String DONE = "done";
    }

    public static final String CMD_CREATE_TABLE =
            CMD_CREATE_TABLE_INE + TABLE_NAME
                    + LBR
                    + Columns.ID + TYPE_INT + TYPE_PK_AI + COMMA
                    + Columns.TASK + TYPE_TEXT + COMMA
                    + Columns.DONE + TYPE_BOOLEAN
                    + RBR + SEMI;

    public static long insertTodo(Todo todo, SQLiteDatabase db) {
        ContentValues newTodo = new ContentValues();
        newTodo.put(Columns.TASK, todo.getTask());
        newTodo.put(Columns.DONE, todo.getDone());

        return db.insert(TABLE_NAME, null, newTodo);
    }

    public static ArrayList<Todo> getAllTodos(SQLiteDatabase db) {
        Cursor c = db.query(
                TABLE_NAME,
                new String[]{Columns.ID, Columns.TASK, Columns.DONE},
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<Todo> todos = new ArrayList<>();
        c.moveToFirst();
        int taskIndex = c.getColumnIndex(Columns.TASK);
        int idIndex = c.getColumnIndex(Columns.ID);
        int doneIndex = c.getColumnIndex(Columns.DONE);

        while (!c.isAfterLast()) {
            todos.add(new Todo(
                    c.getInt(idIndex),
                    c.getString(taskIndex),
                    c.getInt(doneIndex) == 1     
            ));
            c.moveToNext();
        }

        return todos;
    }
}
