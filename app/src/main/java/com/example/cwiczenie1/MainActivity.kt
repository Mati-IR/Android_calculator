import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cwiczenie1.R

class MainActivity : AppCompatActivity()
{
    private val workTV: TextView by lazy { findViewById<TextView>(R.id.workTV) }
    private val resultsTV: TextView by lazy { findViewById<TextView>(R.id.resultsTV) }
    private val outputLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.outputLayout) }
    private var canAddOperation = false
    private var canAddDecimal = true

    private val KEY_WORKINGS = "workings"
    private val KEY_RESULTS = "results"
    private val KEY_CAN_ADD_OPERATION = "canAddOperation"
    private val KEY_CAN_ADD_DECIMAL = "canAddDecimal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentOrientation = resources.configuration.orientation

        if (currentOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_horizontal)
        } else {
            setContentView(R.layout.activity_main_vertical)
        }

        // Restore state if available
        if (savedInstanceState != null) {
            workTV.text = savedInstanceState.getString(KEY_WORKINGS, "")
            resultsTV.text = savedInstanceState.getString(KEY_RESULTS, "")
            canAddOperation = savedInstanceState.getBoolean(KEY_CAN_ADD_OPERATION, false)
            canAddDecimal = savedInstanceState.getBoolean(KEY_CAN_ADD_DECIMAL, true)
        }
    }

    // Save the instance state to retain data after rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_WORKINGS, workTV.text.toString())
        outState.putString(KEY_RESULTS, resultsTV.text.toString())
        outState.putBoolean(KEY_CAN_ADD_OPERATION, canAddOperation)
        outState.putBoolean(KEY_CAN_ADD_DECIMAL, canAddDecimal)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workTV.append(view.text)

                canAddDecimal = false
            } else
                workTV.append(view.text)

            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button) {
            // Check if the last character in workingsTV is an operator
            val currentWorkings = workTV.text.toString()
            if (currentWorkings.isNotEmpty() && currentWorkings.last().isOperator()) {
                // Remove the last operator
                workTV.text = currentWorkings.dropLast(1)
            }

            if (canAddOperation) {
                val result = calculateResults()
                resultsTV.text = result
                workTV.text = "$result${view.text}"
                canAddOperation = false
                canAddDecimal = true
            } else {
                workTV.append(view.text)
            }
        }
    }

    // Add an extension function to Char to check if it's an operator
    fun Char.isOperator(): Boolean {
        return this == '+' || this == '-' || this == 'x' || this == '/'
    }

    fun allClearAction(view: View) {
        workTV.text = ""
        resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workTV.length()
        if (length > 0)
            workTV.text = workTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View) {
        val result = calculateResults()
        resultsTV.text = result
        setCurrentResult(result)
    }

    private fun setCurrentResult(result: String) {
        workTV.text = result
        canAddOperation = true
        canAddDecimal = true
    }

    private fun calculateResults(): String
    {

        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in workTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }

}