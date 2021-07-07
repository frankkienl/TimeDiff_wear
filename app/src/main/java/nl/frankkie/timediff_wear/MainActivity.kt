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
    private var unit: MyTimeUnit = MyTimeUnit.HOUR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        handler = Handler()
    }

    fun initUI() {
        binding.btnUnit.setOnClickListener {
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
                    0 -> unit = MyTimeUnit.SEC
                    1 -> unit = MyTimeUnit.MIN
                    2 -> unit = MyTimeUnit.HOUR
                    3 -> unit = MyTimeUnit.DAY
                }
                updateUI()
            }

            builder.create().show()
        }
    }

    fun updateUI() {
        if (isPaused) return

        //get input
        val amountString = binding.edAmount.text.toString()
        var amount = 0
        try {
            amount = Integer.parseInt(amountString)
        } catch (e: NumberFormatException) {
            //ignore
        }

        //calc
        val sdf = SimpleDateFormat.getDateTimeInstance()
        val now = GregorianCalendar()
        val soon = GregorianCalendar()
        soon.add(unit.calendarField, amount)

        binding.fromTv.text =
            getString(R.string.now_time, sdf.format(Date.from(now.toZonedDateTime().toInstant())))
        binding.soonTv.text =
            getString(R.string.soon_time, sdf.format(Date.from(soon.toZonedDateTime().toInstant())))

        binding.btnUnit.text = getString(unit.stringRes)

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