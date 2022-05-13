package com.aakash.chatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.recyclerview.widget.RecyclerView
import com.aakash.chatbot.db.NotesTable
import com.aakash.chatbot.entities.Note
import com.aakash.chatbot.recyclerview.ChatAdapter
import com.aakash.chatbot.recyclerview.NotesAdapter

class NotesActivity : AppCompatActivity() {

    lateinit var notesAdapter: NotesAdapter
    var notesList = mutableListOf<Note>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesTable: NotesTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        notesTable = NotesTable(this)
        notesList = notesTable.getAll()
        recyclerView = findViewById(R.id.notes_recycler)
        notesAdapter = NotesAdapter(notesList)
        recyclerView.adapter = notesAdapter

        registerForContextMenu(recyclerView)
    }
}