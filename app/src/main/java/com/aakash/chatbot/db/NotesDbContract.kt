package com.aakash.chatbot.db

import android.provider.BaseColumns

object NotesDbContract {
    //Tables
    object NotesTable : BaseColumns {
        const val ID = "id"
        const val TABLE_NAME = "notes"
        const val NOTE_NAME= "note_name"
        const val NOTE_CONTENT = "note_content"
    }
}