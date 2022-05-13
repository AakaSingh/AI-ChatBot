package com.aakash.chatbot.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * If you want to access database
    val dbHelper = DatingDbHelper(context)
 */

private const val SQL_CREATE_TABLE =
    "CREATE TABLE ${NotesDbContract.NotesTable.TABLE_NAME} (" +
            "${NotesDbContract.NotesTable.ID} INTEGER PRIMARY KEY, " + //"${BaseColumns._ID}"
            "${NotesDbContract.NotesTable.NOTE_NAME} TEXT, " +
            "${NotesDbContract.NotesTable.NOTE_CONTENT} TEXT" +
            ")"

private const val DROP_TABLE = "DROP TABLE IF EXISTS ${NotesDbContract.NotesTable.TABLE_NAME}"

class NotesDbHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

        companion object {
            const val DATABASE_NAME = "notes_db"
            const val DATABASE_VERSION = 1
        }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}


















