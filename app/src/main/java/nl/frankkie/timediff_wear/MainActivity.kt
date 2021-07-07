package nl.frankkie.timediff_wear

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
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
    private var unit1: MyTimeUnit = MyTimeUnit.HOUR
    private var unit2: MyTimeUnit = MyTimeUnit.MIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        handler = Handler()
    }

    fun initUI() {
        binding.btnUnit1.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setItems(
                arrayOf(
                    getString(MyTimeUnit.SEC.stringRes),
                    getString(MyTimeUnit.MIN.stringRes),
                    getString(MyTimeUnit.HOUR.stringRes),
                    getString(MyTimeUnit.DAY.stringRes)
                )
            ) { _, index ->
                when (index) {
                    0 -> unit1 = MyTimeUnit.SEC
                    1 -> unit1 = MyTimeUnit.MIN
                    2 -> unit1 = MyTimeUnit.HOUR
                    3 -> unit1 = MyTimeUnit.DAY
                }
                updateUI()
            }
            builder.create().show()
        }
        binding.btnUnit2.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setItems(
                arrayOf(
                    getString(MyTimeUnit.SEC.stringRes),
                    getString(MyTimeUnit.MIN.stringRes),
                    getString(MyTimeUnit.HOUR.stringRes),
                    getString(MyTimeUnit.DAY.stringRes)
                )
            ) { _, index ->
                when (index) {
                    0 -> unit1 = MyTimeUnit.SEC
                    1 -> unit1 = MyTimeUnit.MIN
                    2 -> unit1 = MyTimeUnit.HOUR
                    3 -> unit1 = MyTimeUnit.DAY
                }
                updateUI()
            }
            builder.create().show()
        }
    }

    fun updateUI() {
        if (isPaused) return

        //get input
        val amountString1 = binding.edAmount1.text.toString()
        val amountString2 = binding.edAmount2.text.toString()
        var amount1 = 0
        var amount2 = 0
        try {
            amount1 = Integer.parseInt(amountString1)
            amount2 = Integer.parseInt(amountString2)
        } catch (e: NumberFormatException) {
            //ignore
        }

        //calc
        val sdf = SimpleDateFormat.getDateTimeInstance()
        val now = GregorianCalendar()
        val soon = GregorianCalendar()
        soon.add(unit1.calendarField, amount1)
        soon.add(unit2.calendarField, amount2)

        binding.fromTv.text =
            getString(R.string.now_time, sdf.format(Date.from(now.toZonedDateTime().toInstant())))
        binding.soonTv.text =
            getString(R.string.soon_time, sdf.format(Date.from(soon.toZonedDateTime().toInstant())))

        binding.btnUnit1.text = getString(unit1.stringRes)
        binding.btnUnit2.text = getString(unit2.stringRes)

        //next iteration
        handler.postDelayed(updaterinator, 1000)
    }

    override fun onResume() {
        super.onResume()
        isPaused = false
        handler.postDelayed(updaterinator, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
        handler.removeCallbacks(updaterinator)
    }

    enum class MyTimeUnit(@StringRes val stringRes: Int, val calendarField: Int) {
        SEC(R.string.time_unit_second, GregorianCalendar.SECOND),
        MIN(R.string.time_unit_minute, GregorianCalendar.MINUTE),
        HOUR(R.string.time_unit_hour, GregorianCalendar.HOUR_OF_DAY),
        DAY(R.string.time_unit_day, GregorianCalendar.DAY_OF_YEAR)
    }
}