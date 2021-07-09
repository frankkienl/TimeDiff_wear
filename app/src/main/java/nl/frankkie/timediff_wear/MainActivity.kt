package nl.frankkie.timediff_wear

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import nl.frankkie.timediff_wear.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private val updaterinator = Runnable {
        updateUI()
    }
    private var isPaused = false
    private val rows = mutableListOf<MyInputRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        handler = Handler()
    }

    private fun initUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAddInputRow.setOnClickListener {
            addRow()
        }
        binding.btnAddAlarm.setOnClickListener {
            //calc
            val soon = GregorianCalendar()
            rows.forEach {
                soon.add(it.unit.calendarField, it.amount)
                it.view.findViewById<Button>(R.id.btn_edit_amount).text = "${it.amount}"
                it.view.findViewById<Button>(R.id.btn_unit).text = getString(it.unit.stringRes)
            }
            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "TimeDiff")
                putExtra(AlarmClock.EXTRA_HOUR, soon.get(GregorianCalendar.HOUR_OF_DAY))
                putExtra(AlarmClock.EXTRA_MINUTES, soon.get(GregorianCalendar.MINUTE))
            }
            if (alarmIntent.resolveActivity(packageManager) != null) {
                startActivity(alarmIntent)
            }
        }
        //add first row
        addRow()
    }

    private fun addRow() {
        val row = layoutInflater.inflate(
            R.layout.input_row,
            binding.inputRowsContainer,
            false
        )
        val myInputRow = MyInputRow(rows.size, row, 0, MyTimeUnit.MIN)
        row.tag = myInputRow
        row.findViewById<Button>(R.id.btn_unit).setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setItems(
                arrayOf(
                    getString(MyTimeUnit.SEC.stringRes),
                    getString(MyTimeUnit.MIN.stringRes),
                    getString(MyTimeUnit.HOUR.stringRes),
                    getString(MyTimeUnit.DAY.stringRes)
                )
            ) { _, index ->
                val newUnit = when (index) {
                    0 -> MyTimeUnit.SEC
                    1 -> MyTimeUnit.MIN
                    2 -> MyTimeUnit.HOUR
                    3 -> MyTimeUnit.DAY
                    else -> MyTimeUnit.MIN
                }
                myInputRow.unit = newUnit
                if (it is Button) {
                    //Kotlin smart cast
                    it.text = getString(newUnit.stringRes)
                }
            }
            builder.create().show()
        }
        row.findViewById<Button>(R.id.btn_edit_amount).setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INDEX, myInputRow.index)
            intent.putExtra(INTENT_EXTRA_OLD_AMOUNT, myInputRow.amount)
            startActivityForResult(intent, REQUEST_CODE)
        }
        //Add to layout
        binding.inputRowsContainer.addView(row)
        rows.add(myInputRow)
    }

    private fun updateUI() {
        if (isPaused) return

        val sdf = SimpleDateFormat.getDateTimeInstance()
        val now = GregorianCalendar()
        val soon = GregorianCalendar()
        rows.forEach {
            soon.add(it.unit.calendarField, it.amount)
            it.view.findViewById<Button>(R.id.btn_edit_amount).text = "${it.amount}"
            it.view.findViewById<Button>(R.id.btn_unit).text = getString(it.unit.stringRes)
        }

        binding.fromTv.text =
            getString(R.string.now_time, sdf.format(Date.from(now.toZonedDateTime().toInstant())))
        binding.soonTv.text =
            getString(R.string.soon_time, sdf.format(Date.from(soon.toZonedDateTime().toInstant())))

        //next iteration
        handler.postDelayed(updaterinator, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            val index = it.getIntExtra(INTENT_EXTRA_INDEX, 0)
            val amount = it.getIntExtra(INTENT_EXTRA_NEW_AMOUNT, 1)
            rows[index].amount = amount
        }
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        isPaused = false
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
        handler.removeCallbacks(updaterinator)
    }

    data class MyInputRow(val index: Int, val view: View, var amount: Int, var unit: MyTimeUnit)

    enum class MyTimeUnit(@StringRes val stringRes: Int, val calendarField: Int) {
        SEC(R.string.time_unit_second, GregorianCalendar.SECOND),
        MIN(R.string.time_unit_minute, GregorianCalendar.MINUTE),
        HOUR(R.string.time_unit_hour, GregorianCalendar.HOUR_OF_DAY),
        DAY(R.string.time_unit_day, GregorianCalendar.DAY_OF_YEAR)
    }

    companion object {
        const val REQUEST_CODE = 1337
        const val INTENT_EXTRA_INDEX = "index"
        const val INTENT_EXTRA_OLD_AMOUNT = "oldValue"
        const val INTENT_EXTRA_NEW_AMOUNT = "newValue"
    }
}