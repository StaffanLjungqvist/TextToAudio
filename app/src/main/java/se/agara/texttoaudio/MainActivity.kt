package se.agara.texttoaudio

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        val btnSpeak = findViewById<Button>(R.id.btnSpeak)
        val edText = findViewById<EditText>(R.id.etText)
        val btnWrite = findViewById<Button>(R.id.btnWrite)
        val btnRead = findViewById<Button>(R.id.btnRead)
        val tvInputText = findViewById<TextView>(R.id.tvInputText)
        val btnWriteAudio = findViewById<Button>(R.id.btnWriteAudio)
        val btnReadAudio = findViewById<Button>(R.id.btnReaduAudio)


        val outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        Log.e("TTS", "file location : ${outputFile}")
        btnWriteAudio.setOnClickListener {
       //     writeAudioToFile(edText.text.toString())
        }

        btnWrite.setOnClickListener {

            if (edText.text.isEmpty()) {
                Toast.makeText(this, "Enter a text to speak", Toast.LENGTH_LONG).show()
            } else {
                writeToFile("testFile.txt", edText.text.toString())
            }

        }

        btnRead.setOnClickListener {
            val readText = readFromFile(this)
            tvInputText.text = readText
        }



        btnSpeak.setOnClickListener {

            if (edText.text.isEmpty()) {
                Toast.makeText(this, "Enter a text to speak", Toast.LENGTH_LONG).show()
            } else {
                speakOut(edText.text.toString())
            }

        }
    }

    private fun writeAudioToFile(text: String) {



        val myHashRender: HashMap<String, String> = HashMap()
        val ttsText = text
        val destFileName = "/sdcard/myAppCache/wakeUp.wav"

        try {
            myHashRender[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = ttsText
            tts!!.synthesizeToFile(ttsText, myHashRender, destFileName)
            Log.e("TTS", "Succesfully wrote to file")
        } catch (e : Exception) {
            Log.e("TTS", "Någonting gick fel : ${e}")
        }

    }


    private fun writeToFile(filename: String, fileText: String) {

        try {
            var filePath = applicationContext.filesDir
            var writer = FileOutputStream(File(filePath, filename))

            //Sparas under /data/data/app.id/
            writer.write(fileText.toByteArray())
            writer.close()
            Toast.makeText(this, "Succesfully wrote to file ${filename}", Toast.LENGTH_LONG)
            Log.e("TTS", "Succesfully wrote to file ${filename}")
        } catch (e: Exception) {

            Log.e("TTS", "Det gick ej att skriva till fil")

        }
    }


    private fun readFromFile(context: Context): String? {
        var ret = ""
        try {
            val inputStream: InputStream = context.openFileInput("testFile.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: " + e.toString())
        }
        return ret
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