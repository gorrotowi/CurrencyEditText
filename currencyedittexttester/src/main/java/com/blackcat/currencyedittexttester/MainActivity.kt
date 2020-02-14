package com.blackcat.currencyedittexttester

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import com.blackcat.currencyedittext.CurrencyTextFormatter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureTestableLocalesTool()
        configureDecimalDigitsTool()
        configureListeners()
    }

    private fun configureListeners() {
        cet_reset_button?.setOnClickListener {
            onResetClicked()
        }

        button?.setOnClickListener {
            onRefreshClicked()
        }
    }

    fun onResetClicked() {
        cet?.setText("")
    }

    @SuppressLint("SetTextI18n")
    fun onRefreshClicked() {

        Log.d("MainActivity", "Locale: " + resources.configuration.locale.toString())
        Log.d("MainActivity", "DefaultLocale: " + Locale.getDefault())
        val maxRange: Long = 15000000
        val randNum = (Random().nextDouble() * maxRange).toLong()
        et_raw_val?.text = randNum.toString()
        var result: String? = "oops"
        try {
            val l = Locale.getDefault()
            result = CurrencyTextFormatter.formatText(randNum.toString(), l, Locale.getDefault())
        } catch (e: IllegalArgumentException) {
            Log.e("MainActivity", e.localizedMessage ?: "")
        }
        et_formatted_val?.text = result
    }

    private fun configureTestableLocalesTool() {
        val locales = Locale.getAvailableLocales()
        val spinnerContents: MutableList<String> = ArrayList()
        for (locale in locales) {
            if (locale.language == "" || locale.country == "") {
                continue
            }
            spinnerContents.add(locale.displayName + ", " + locale.language + ", " + locale.country)
        }
        var startingPosition = 0
        for (i in spinnerContents.indices) {
            if (spinnerContents[i] == "en,US") {
                startingPosition = i
                break
            }
        }
        spinnerContents.sortWith(Comparator { obj: String, str: String? ->
            obj.compareTo(str ?: "", ignoreCase = true)
        })
        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerContents)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_testable_locales?.adapter = spinnerArrayAdapter
        spinner_testable_locales?.setSelection(startingPosition)
        configureViewForLocale(spinner_testable_locales!!.selectedItem as String)
        spinner_testable_locales?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                configureViewForLocale(spinner_testable_locales?.selectedItem as String)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                configureViewForLocale("US")
            }
        }
    }

    //Using english for testing as not setting the language field results in odd formatting. Recommend not
    //building locales this way in a production environment if possible
    private fun configureViewForLocale(locale: String) {
        val localeParts = locale.split(", ").toTypedArray()
        val localeInQuestion = Locale.Builder().setRegion(localeParts[2]).setLanguage(localeParts[1]).build()
        val localeInfo = "Country: " +
                localeInQuestion.displayCountry +
                System.lineSeparator() +
                "Country Code: " +
                localeInQuestion.country +
                System.lineSeparator() +
                "Currency: " +
                Currency.getInstance(localeInQuestion).displayName +
                System.lineSeparator() +
                "Currency Code: " +
                Currency.getInstance(localeInQuestion).currencyCode +
                System.lineSeparator() +
                "Currency Symbol: " + Currency.getInstance(localeInQuestion).symbol
        testable_locales_locale_info?.text = localeInfo
        testable_locales_cet?.configureViewForLocale(localeInQuestion)
    }

    private fun configureDecimalDigitsTool() {
        decimal_digits_tool_number_picker?.minValue = 0
        decimal_digits_tool_number_picker?.maxValue = 340
        decimal_digits_tool_number_picker?.value = 2
        decimal_digits_tool_cet?.decimalDigits = decimal_digits_tool_number_picker?.value ?: 0
        decimal_digits_tool_number_picker?.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            decimal_digits_tool_cet?.decimalDigits = newVal
        }
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}