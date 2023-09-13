package com.example.voiceassistent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var textToSpeech: TextToSpeech
    private lateinit var questionText: EditText
    private lateinit var chatMessageList: RecyclerView
    private lateinit var text: String
    private lateinit var sendButton: Button
    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()
    var sPref: SharedPreferences? = null
    val APP_PREFERENCES = "mysettings"
    private var isLight = true
    private val THEME = "THEME"
    val map: AI = AI()
    var dBHelper: DBHelper? = null
    var database: SQLiteDatabase? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE)

        isLight = sPref!!.getBoolean(THEME,true)
        if(isLight)
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            messageListAdapter.messageList = savedInstanceState?.getSerializable("items") as ArrayList<Message>
        }

        setContentView(R.layout.activity_main)
        Log.i("LOG", "onCreate")



        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { it ->
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
                Locale.setDefault(Locale("ru"))
            }
        })
        Locale.setDefault(Locale("ru"))

        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)
        chatMessageList = findViewById(R.id.chatMessageList)

        chatMessageList.layoutManager = LinearLayoutManager(this)
        chatMessageList.adapter = messageListAdapter

        dBHelper = DBHelper(this);
        database = dBHelper!!.getWritableDatabase();

        if (savedInstanceState == null) {
            val cursor: Cursor = database!!
                .query(dBHelper?.TABLE_NAME, null, null, null, null, null, null)
            // дописать
            if (cursor.moveToFirst()) {
                val messageIndex = cursor.getColumnIndex(dBHelper!!.FIELD_MESSAGE)
                val dateIndex = cursor.getColumnIndex(dBHelper!!.FIELD_DATE)
                val sendIndex = cursor.getColumnIndex(dBHelper!!.FIELD_SEND)
                do {
                    val entity = MessageEntity(
                        cursor.getString(messageIndex),
                        cursor.getString(dateIndex), cursor.getInt(sendIndex)==1
                    )
                    val message = Message(entity)
                    messageListAdapter.messageList.add(message)
                } while (cursor.moveToNext())
            }
            cursor.close()
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size-1)
        }


        sendButton.setOnClickListener {
            onSend()
        }
    }



    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun onSend(){
        text = questionText.text.toString()

        map.getAnswer(text, Consumer {
            val answer = it
                messageListAdapter.messageList.add(Message(text, isSend = true))
                messageListAdapter.messageList.add(Message(answer, isSend = false))
                messageListAdapter.notifyDataSetChanged()
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size -1 )
                questionText.text.clear()
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null, null )
                dismissKeyboard()
        })

    }
    private fun dismissKeyboard() {
        val view: View? = this.currentFocus // элемент, который имеет текущий фокус ввода
        if (view != null) {
            // определить менеджер, отвечающий  за ввод
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // менеджер скрывает экранную клавиатуру
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> {
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                isLight = true
            }
            R.id.night_settings -> {
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                isLight = false
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop()
    {
        super.onStop()
        val editor: SharedPreferences.Editor = sPref!!.edit()
        editor.putBoolean(THEME, isLight)
        editor.apply()
        database?.delete(dBHelper?.TABLE_NAME, null, null)

        var i = 0
        while (i<messageListAdapter.messageList.size) {
            val entity = MessageEntity(messageListAdapter.messageList[i])
            val contentValues = ContentValues()
            contentValues.put(dBHelper?.FIELD_MESSAGE, entity.text)
            contentValues.put(dBHelper?.FIELD_SEND, if (entity.isSend) 1 else 0)
            contentValues.put(dBHelper?.FIELD_DATE, entity.date)
            database?.insert(dBHelper?.TABLE_NAME,null,contentValues);
            i++
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("LOG", "onSaveInstanceState")
        // Сохранение данных в объект Bundle
        outState.putSerializable("items", messageListAdapter.messageList)
    }

    override fun onDestroy() {
        database!!.close()
        super.onDestroy()
    }


}