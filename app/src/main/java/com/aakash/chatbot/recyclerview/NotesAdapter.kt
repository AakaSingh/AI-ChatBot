package com.aakash.chatbot.recyclerview

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aakash.chatbot.R
import com.aakash.chatbot.db.NotesDbContract
import com.aakash.chatbot.db.NotesTable
import com.aakash.chatbot.entities.Note


class NotesAdapter(private val dataSet: MutableList<Note>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>(){

    class NotesViewHolder(private val parentAdapter: NotesAdapter, private val containerView: View) : RecyclerView.ViewHolder(containerView){
        var note: Note? = null
        var noteName: TextView = containerView.findViewById(R.id.note_item_name)
        val noteContent: TextView = containerView.findViewById(R.id.note_item_content)
        private val delete: ImageView = containerView.findViewById(R.id.notes_item_delete)
        init {
            delete.setOnClickListener {
                parentAdapter.removeItem(note as Note, containerView.context)
            }
        }
    }

    fun removeItem(note: Note, context: Context){
        dataSet.remove(note)
        val notesTable = NotesTable(context)
        notesTable.delete(note)
        notifyDataSetChanged()
    }

    //inflate the customView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notes_item,parent, false) //false because the recycler will add to the view hierarchy when it is time
        return NotesViewHolder(this, view)
    }

    //Called by the layoutManager to replace the content(data) of the CustomView
    override fun onBindViewHolder(holder: NotesViewHolder, positionInDataSet: Int) {
        val currentData = dataSet[positionInDataSet]
        holder.note = currentData
        holder.noteName.text = currentData.name
        holder.noteContent.text = currentData.content
    }

    override fun getItemCount() = dataSet.size

}