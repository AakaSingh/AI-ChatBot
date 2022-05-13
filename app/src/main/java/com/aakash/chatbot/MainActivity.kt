package com.aakash.chatbot

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.aakash.chatbot.db.NotesTable
import com.aakash.chatbot.entities.AIResponse
import com.aakash.chatbot.entities.Chat
import com.aakash.chatbot.network.BrainShopApi
import com.aakash.chatbot.recyclerview.ChatAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val BOT_KEY = "bot"
        const val USER_KEY = "user"
    }

    lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageContent: EditText
    private lateinit var senderButton: FloatingActionButton
    private var chatsList = mutableListOf<Chat>()
    private lateinit var notesTable: NotesTable

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        run {
            senderButton.setImageDrawable(resources.getDrawable(R.drawable.microphone))
            if (result.resultCode == RESULT_OK &&
                result.data != null)
            {
                val speechInput = result.data
                val speechInputText = speechInput?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                if(checkOptionsAdded(speechInputText as String))
                    messageContent.setText(speechInputText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.main_recycler)
        messageContent = findViewById(R.id.main_userMessage)
        senderButton = findViewById<FloatingActionButton>(R.id.main_sendMessage)
        notesTable = NotesTable(this)

        chatsList.add(Chat("Hey! What's on your mind?", BOT_KEY))
        chatAdapter = ChatAdapter(chatsList)
        recyclerView.adapter = chatAdapter
        senderButton.setOnClickListener(this::onSenderButtonClicked)

        recyclerView.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if(bottom < oldBottom){
                recyclerView.postDelayed(Runnable{
                    recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
                },0)
            }
        }

        messageContent.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(messageContent.text.toString()  == "")
                    senderButton.setImageDrawable(resources.getDrawable(R.drawable.microphone))
                else
                    senderButton.setImageDrawable(resources.getDrawable(R.drawable.ic_send))
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

    }

    private fun onSenderButtonClicked(view: View){
        if(messageContent.text.toString() == "")
            getSpeechInput()
        else
            getResponse()

        messageContent.setText("")
    }

    private fun checkOptionsAdded( userRequest : String) : Boolean{
        if(userRequest.contains("open camera",true) && userRequest.indexOf("don't")==-1 && userRequest.indexOf("not")==-1){
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                chatAdapter.addData(Chat("Opening Camera...", BOT_KEY))
                recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                dispatchTakePictureIntent()
            }, 1000)
            return false
        }

        if(userRequest.contains("send email", true)){
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                chatAdapter.addData(Chat("Opening Email Dialog...", BOT_KEY))
                recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                openEmailDialog()
            }, 1000)
            return false
        }

        if(userRequest.contains("take notes",true)){
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                chatAdapter.addData(Chat("Opening Notes Dialog...", BOT_KEY))
                recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                openNotesDialog()
            }, 1000)
            return false
        }

        if(userRequest.contains("view notes",true)){
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                chatAdapter.addData(Chat("Showing Your Notes...", BOT_KEY))
                recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                openNotesActivity()
            }, 1000)
            return false
        }

        if(userRequest.contains("call",true)){
            val listOfInputs = userRequest.split(" ")
            var numberPosition = -1

            for (input in listOfInputs){
                if(PhoneNumberUtils.isGlobalPhoneNumber(input))
                    numberPosition = listOfInputs.indexOf(input)
            }

            if(numberPosition > -1){
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listOfInputs[numberPosition]))
                startActivity(intent)
                return false
            }
        }
        return true
    }

    private fun getResponse(){
        chatAdapter.addData(Chat(messageContent.text.toString(), USER_KEY))
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount)


        if(checkOptionsAdded(messageContent.text.toString())) {
            val url =
                "http://api.brainshop.ai/get?bid=163746&key=qvm2SAD7av4gVkMx&uid=[uid]&msg=" + messageContent.text.toString()
            val getResponse = BrainShopApi.retrofitService.getAiResponse(url)
            getResponse.enqueue(object : Callback<AIResponse> {
                override fun onResponse(call: Call<AIResponse>, response: Response<AIResponse>) {
                    val dataReceived = response.body() as AIResponse

                    Handler(Looper.getMainLooper()).postDelayed(Runnable { // Do something after 5s = 5000ms
                        chatAdapter.addData(Chat(dataReceived.message, BOT_KEY))
                        recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
                    }, 500)

                }

                override fun onFailure(call: Call<AIResponse>, t: Throwable) {
                    chatAdapter.addData(Chat("I didn't understand", BOT_KEY))
                }
            })
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(
            packageManager.getLaunchIntentForPackage(
                intent.resolveActivity(packageManager).packageName
            )
        )
    }

    private fun openEmailDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView : View = layoutInflater.inflate(R.layout.activity_send_email_dialog, null)
        builder.setView(dialogView)
        builder.setPositiveButton("Send Email") { dialog, _ ->
            val receiver = dialogView.findViewById<EditText>(R.id.email_dialog_to)
            val subject = dialogView.findViewById<EditText>(R.id.email_dialog_subject)
            val body = dialogView.findViewById<EditText>(R.id.email_dialog_body)

            val email = Intent(Intent.ACTION_SENDTO)
            email.data = Uri.parse("mailto:")
            email.type = "text/plain"
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(receiver.text.toString()))
            email.putExtra(Intent.EXTRA_SUBJECT, subject.text.toString())
            email.putExtra(Intent.EXTRA_TEXT, body.text.toString())

            startActivity(Intent.createChooser(email,"Choose Email Client"))
        }
        builder.setNeutralButton("Cancel"){ dialog,_ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun openNotesDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView : View = layoutInflater.inflate(R.layout.notes_dialog, null)
        builder.setView(dialogView)
        builder.setPositiveButton("Save") { dialog, _ ->
            val noteName = dialogView.findViewById<EditText>(R.id.notes_dialog_name)
            val noteContent = dialogView.findViewById<EditText>(R.id.notes_dialog_content)

            if(noteName.text.toString() == "" || noteContent.text.toString() == ""){
                Toast.makeText(this,"You left a field empty",Toast.LENGTH_SHORT).show()
            }
            else{
                notesTable.insertData(noteName.text.toString(), noteContent.text.toString())
                dialog.dismiss()
            }
        }
        builder.setNeutralButton("Cancel"){ dialog,_ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun openNotesActivity(){
        val intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }

    private fun getSpeechInput()
    {
        senderButton.setImageDrawable(resources.getDrawable(R.drawable.mic))
        val intent = Intent(
            RecognizerIntent
            .ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault())
            resultLauncher.launch(intent)
    }
}