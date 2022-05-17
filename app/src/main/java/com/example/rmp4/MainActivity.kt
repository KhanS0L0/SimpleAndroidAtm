package com.example.rmp4

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.content.Context
import java.io.IOException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var outputTextView: TextView
    private var currencies: List<Currency> = listOf()
    var lastNumeric: Boolean = false
    var stateError: Boolean = false
    var lastDot :Boolean = false
    var isMax :Boolean = true
    var currentCurrency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        readFile()
    }

    fun euro(view: View){
        currentCurrency = "EUR"
        setContentView(R.layout.activity_main)
        outputTextView = findViewById(R.id.outputTextView)
        outputTextView.text = ""
        Log.i("currency current", currentCurrency)
    }

    fun dollar(view: View){
        currentCurrency = "USD"
        setContentView(R.layout.activity_main)
        outputTextView = findViewById(R.id.outputTextView)
        outputTextView.text = ""
        Log.i("currency current", currentCurrency)
    }

    fun ruble(view: View){
        currentCurrency = "RUB"
        setContentView(R.layout.activity_main)
        outputTextView = findViewById(R.id.outputTextView)
        outputTextView.text = ""
        Log.i("currency current", currentCurrency)
    }

    fun wtf(view: View){
        currentCurrency = "WTF"
        setContentView(R.layout.activity_main)
        outputTextView = findViewById(R.id.outputTextView)
        outputTextView.text = ""
        Log.i("currency current", currentCurrency)
    }

    fun addDigit(view: View) {
        if(stateError) {
            outputTextView.text=((view as Button).text)
            stateError=false
        }else {
            outputTextView.append((view as Button).text)
        }
        lastNumeric=true
    }

    fun addPoint(view: View) {
        if(lastNumeric && !stateError && !lastDot) {
            outputTextView.append(".")
            lastNumeric=false
            lastDot=true
        }
    }

    fun clear(view: View) {
        lastNumeric=false
        stateError=false
        lastDot=false
        currentCurrency=""
        setContentView(R.layout.activity_welcome)
    }

    fun result(view: View) {
        if(lastNumeric && !stateError) {
            try {
                var number = outputTextView.text.toString().toInt()
                val banknotesList = currencies.filter { it.currency == currentCurrency }
                val banknotes = banknotesList[0].banknotes.toIntArray()
                var output: String = ""
                banknotes.sort()
                if(isMax){
                    banknotes.reverse()
                }
                for (value in banknotes) {
                    val count = number / value
                    number -= value * count
                    output += "$value: $count шт, "
                }

                Log.i("answer", output)
                outputTextView.text= output
                lastDot=true
            } catch (ex:Exception) {
                outputTextView.text="Error"
                ex.printStackTrace()
                stateError=true
                lastNumeric=false
            }
        }
    }

    fun change(view: View){
        isMax = !isMax
        clear(view)
    }

    fun exit(view: View) { this.finishAffinity(); }

    private fun readFile(){
        val jsonFileString = getJsonDataFromAsset(applicationContext)

        if(jsonFileString != null) Log.i("JSON DATA", jsonFileString)

        val gson = Gson()
        val listCurrencyType = object : TypeToken<List<Currency>>() {}.type
        val localCurrencies: List<Currency> = gson.fromJson(jsonFileString, listCurrencyType)
        currencies = localCurrencies
        currencies.forEachIndexed { id, currency -> Log.i("JSON DATA", "==> ITEM $id: \n$currency") }
    }

    private fun getJsonDataFromAsset(context: Context): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open("currencies.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    data class Currency(val currency: String, val banknotes: List<Int>)
}