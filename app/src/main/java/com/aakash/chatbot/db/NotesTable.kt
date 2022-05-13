package com.aakash.chatbot.db

import android.content.ContentValues
import android.content.Context
import com.aakash.chatbot.entities.Note

class NotesTable(context : Context){
    private val dbHelper = NotesDbHelper(context)

    fun insertData(noteName: String, noteContent: String) {
        //Map of column name + row value
        val values = ContentValues().apply {
            put(NotesDbContract.NotesTable.NOTE_NAME, noteName)
            put(NotesDbContract.NotesTable.NOTE_CONTENT, noteContent)
        }

        val writeToDb = dbHelper.writableDatabase
        val newRowId = writeToDb.insert(NotesDbContract.NotesTable.TABLE_NAME, null, values)
    }

    fun getAll(): MutableList<Note> {
        val readFromDb = dbHelper.readableDatabase //EXPENSIVE if DB is closed.

        //Select Columns you want
        val projection = arrayOf(
            NotesDbContract.NotesTable.ID,
            NotesDbContract.NotesTable.NOTE_NAME,
            NotesDbContract.NotesTable.NOTE_CONTENT
        )

        //WHERE PART only to avoid SQL Injection
        //val selection = "${DatingDBContract.UserTable.USERNAME} = ? AND ${DatingDBContract.UserTable.PASSWORD} = ?"
        //val selectionArgs = arrayOf("rezaUser", "rezaPassword")

        val cursor = readFromDb.query(
            NotesDbContract.NotesTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val notesList = mutableListOf<Note>()

        with(cursor) {
            while (moveToNext()) {//Moves from -1 row to next one
                val note = Note(
                    getInt(getColumnIndexOrThrow(NotesDbContract.NotesTable.ID)),
                    getString(getColumnIndexOrThrow(NotesDbContract.NotesTable.NOTE_NAME)),
                    getString(getColumnIndexOrThrow(NotesDbContract.NotesTable.NOTE_CONTENT))
                )
                notesList.add(note)
            }
        }
        cursor.close()
        return notesList
    }

    fun delete(note: Note) : Boolean {

        val dbWrite = dbHelper.writableDatabase

        val whereClause = "${NotesDbContract.NotesTable.ID} LIKE ?"
        val whereClauseArgs = arrayOf(note.id.toString())

        val deletedRows = dbWrite.delete(
            NotesDbContract.NotesTable.TABLE_NAME,
            whereClause,
            whereClauseArgs
        )
        return deletedRows > 0
    }
}