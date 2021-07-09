package nl.frankkie.timediff_wear

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import nl.frankkie.timediff_wear.databinding.ActivityInputBinding

class InputActivity : Activity() {

    private lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val oldAmount = intent.getIntExtra(MainActivity.INTENT_EXTRA_OLD_AMOUNT, 0)
        binding.edAmount.setText("$oldAmount")

        binding.btnOk.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(
                MainActivity.INTENT_EXTRA_INDEX,
                intent.getIntExtra(MainActivity.INTENT_EXTRA_INDEX, 0)
            )
            var amountInt = 0
            try {
                amountInt = binding.edAmount.text.toString().toInt()
            } catch (e: Exception) {
                //ignore
            }
            returnIntent.putExtra(MainActivity.INTENT_EXTRA_NEW_AMOUNT, amountInt)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
        binding.btnMinus.setOnClickListener {
            var amountInt = 0
            try {
                amountInt = binding.edAmount.text.toString().toInt()
            } catch (e: Exception) {
                //ignore
            }
            amountInt -= 1
            binding.edAmount.setText("$amountInt")
        }
        binding.btnPlus.setOnClickListener {
            var amountInt = 0
            try {
                amountInt = binding.edAmount.text.toString().toInt()
            } catch (e: Exception) {
                //ignore
            }
            amountInt += 1
            binding.edAmount.setText("$amountInt")
        }
    }
}