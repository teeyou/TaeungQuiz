package teeu.android.mykotlinapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

const val EXTRA_SHOWN = "teeu.android.mykotlinapplication.extra_shown"
private const val EXTRA_SHOW_ANSWER = "teeu.android.mykotlinapplication.extra_show_answer"
class CheatActivity : AppCompatActivity() {
    private lateinit var warningTextView : TextView
    private lateinit var showAnswerButton : Button
    private val quizViewModel : QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java) //최초의 viewModel이 리턴되고 변경되지 않으므로 val을 쓰기위해 lazy 사용
    }

    companion object {
        fun newIntent(context : Context, answer : Boolean) : Intent {
            return Intent(context,CheatActivity::class.java).apply {
                putExtra(EXTRA_SHOW_ANSWER, answer)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        warningTextView = findViewById(R.id.warning_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        val answer = intent.getBooleanExtra(EXTRA_SHOW_ANSWER,false)
        showAnswerButton.setOnClickListener {
            quizViewModel.cheatMessage = when {
                answer -> R.string.true_button
                else -> R.string.false_button
            }
            warningTextView.setText(quizViewModel.cheatMessage)
            quizViewModel.isCheater = true
            setAnswerShownResult(quizViewModel.isCheater)
        }

        warningTextView.setText(quizViewModel.cheatMessage)
        setAnswerShownResult(quizViewModel.isCheater)
    }

    private fun setAnswerShownResult(isShow : Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_SHOWN,isShow)
        }
        setResult(Activity.RESULT_OK,data)
    }
}