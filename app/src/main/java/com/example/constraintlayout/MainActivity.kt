package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*


class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtPessoas: EditText
    private lateinit var valorConta: TextView
    private var ttsSucess: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtConta = findViewById<EditText>(R.id.edtConta)
        edtPessoas = findViewById(R.id.edtPessoas)
        valorConta = findViewById(R.id.valorConta)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularDivisao()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        }

        edtPessoas.addTextChangedListener(watcher)
        edtConta.addTextChangedListener(watcher)

        val btFalar = findViewById<Button>(R.id.btFalar)


        btFalar.setOnClickListener {
            val valor = valorConta.text.toString()
            if (valor != "R$ 0,00") {
                tts.speak("O valor por pessoa é $valor", TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                tts.speak("Informe os valores para calcular.", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")
            }
        }

        val btnCompartilhar = findViewById<Button>(R.id.button4)

        btnCompartilhar.setOnClickListener {
            val valor = valorConta.text.toString()
            val mensagem = "O valor por pessoa é $valor"

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mensagem)
                type = "text/plain"
            }

            startActivity(Intent.createChooser(intent, "Compartilhar valor"))
        }
    }

    private fun calcularDivisao(){
        var contaStr = edtConta.text.toString()
        val pessoasStr = edtPessoas.text.toString()

        val conta = contaStr.toDoubleOrNull()
        val pessoas = pessoasStr.toIntOrNull()

        if (conta != null && pessoas != null && pessoas > 0) {
            val resultado = conta / pessoas
            valorConta.text = "R$ %.2f".format(resultado)
        } else {
            valorConta.text = "R$ 0,00"
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       Log.d("PDM24","Antes de mudar")

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24","Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d ("PDM24", "Depois de mudar")

        val valor: Double

        if(s.toString().length>0) {
             valor = s.toString().toDouble()
            Log.d("PDM24", "v: " + valor)
        //    edtConta.setText("9")
        }
    }

    fun clickFalar(v: View){
        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            Log.d ("PDM23", tts.language.toString())
            tts.speak("Oi Sumido", TextToSpeech.QUEUE_FLUSH, null, null)
        }




    }
    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
                tts.language = Locale.getDefault()
                ttsSucess=true
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                // TTS engine failed to initialize
                Log.e("PDM23", "Failed to initialize TTS engine.")
                ttsSucess=false
            }
        }


}

