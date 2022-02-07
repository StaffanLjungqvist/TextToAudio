package se.agara.texttoaudio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var mAudioFilename = ""
    private val mUtteranceID = "totts"
    private var tts: TextToSpeech? = null
    private val EXTERNAL_STORAGE_PERMISSION_CODE = 23
    lateinit var etText : EditText
    lateinit var tvInputText : TextView
    private var mMediaPlayer = MediaPlayer()
    lateinit var testFile : File
    var path = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        path = this.filesDir.toString() + "/myreq.wav"

        testFile = File(path)

        Log.d("TTS", "skapade fil ${testFile.name}")

        val btnSpeak = findViewById<Button>(R.id.btnSpeak)
        etText = findViewById<EditText>(R.id.etText)
        val btnReadAudio = findViewById<Button>(R.id.btnReaduAudio)
        val btnWriteToAudioFile = findViewById<Button>(R.id.btnWriteToAudioFIle)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            EXTERNAL_STORAGE_PERMISSION_CODE
        )


        btnWriteToAudioFile.setOnClickListener{
            saveToAudioFile()
        }

        btnReadAudio.setOnClickListener {
            readAudioFile()
        }


        btnSpeak.setOnClickListener {
            if (etText.text.isEmpty()) {
                Toast.makeText(this, "Enter a text to speak", Toast.LENGTH_LONG).show()
            } else {
                speakOut(etText.text.toString())
            }
        }
    }



    private fun createAudioFile() {
        // Create audio file location
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/My File/")
        sddir.mkdir()
        mAudioFilename = sddir.absolutePath.toString() + "/" + mUtteranceID + ".wav"
        val testFile = File(path)
        Log.d("TTS", "skapade fil : ${mAudioFilename}")
    }

    private fun saveToAudioFile() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.synthesizeToFile(etText.text, null, testFile, mUtteranceID)
            Log.d("TTS", "Saved to " + testFile.absolutePath)

        } else {
            val hm = HashMap<String, String>()
            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,mUtteranceID)
            tts!!.synthesizeToFile("testing", hm, mAudioFilename)
            Log.d("TTS","Saved to " + mAudioFilename)
        }
    }

    private fun readAudioFile() {
        try {
            val mp = MediaPlayer.create(this, Uri.parse(path))
            mp.start()
        } catch (e : java.lang.Exception) {
            Log.d("TTS", "Something went wrong : ${e}")
        }
    }





    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "The language specified is not supported")
            }
        } else {
            Log.e("TTS", "TTS Initialization Failed")
        }
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}