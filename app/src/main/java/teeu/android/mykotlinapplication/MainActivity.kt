package teeu.android.mykotlinapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    private lateinit var trueButton : Button
    private lateinit var falseButton : Button
    private lateinit var nextButton : ImageButton
    private lateinit var prevButton : ImageButton
    private lateinit var questionTextView : TextView

    private val questionBank = listOf(
        Question(R.string.question_age,false, false),
        Question(R.string.question_dgu,true, false),
        Question(R.string.question_korean,true, false),
        Question(R.string.question_major,false, false),
        )
    private var currentIndex = 0
    private var waitTime = 0L
    private var correctCount = 0
    private var count = 0

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        Log.d(TAG,"time : $time")
        if(time - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Log.d(TAG,"waitTime : $waitTime")
            Toast.makeText(this,"뒤로가기 버튼 누름",Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate() called")
        setContentView(R.layout.activity_main)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)

        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)

        questionTextView = findViewById(R.id.question_text_view)

        questionTextView.setOnClickListener { view : View ->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
            updateButton()
        }

        trueButton.setOnClickListener { view : View ->
//            val toastTop = Toast.makeText(applicationContext,R.string.correct_toast,Toast.LENGTH_SHORT)
//            toastTop.setGravity(Gravity.TOP,Gravity.AXIS_X_SHIFT,Gravity.AXIS_Y_SHIFT)
//            toastTop.setGravity(Gravity.CENTER,Gravity.AXIS_X_SHIFT,Gravity.AXIS_Y_SHIFT)
//            toastTop.show()
            checkAnser(true)
            updateButton()
        }

        falseButton.setOnClickListener { view : View ->
            checkAnser(false)
            updateButton()
        }

        prevButton.setOnClickListener { view : View ->
            currentIndex = if(currentIndex > 0) {
                currentIndex - 1
            } else {
                questionBank.size - 1
            }
            updateQuestion()
            updateButton()
        }

        nextButton.setOnClickListener { view : View ->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
            updateButton()
        }

        updateQuestion()
    }

    private fun updateButton() {
        val flag = !questionBank[currentIndex].solved
        trueButton.isClickable = flag
        falseButton.isClickable = flag
    }
    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnser(userAnswer : Boolean) {
        val answer = questionBank[currentIndex].answer
        val messageResId = if(userAnswer == answer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show()
        questionBank[currentIndex].solved = true
        if(messageResId == R.string.correct_toast) {
            correctCount++
        }
        count++
        if(count == questionBank.size)
            Toast.makeText(this,"점수 : ${correctCount * 100 / count}점",Toast.LENGTH_SHORT).show()
    }
}