package teeu.android.mykotlinapplication

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE: Int = 1000

class MainActivity : AppCompatActivity() {

    //onPause -> SIS -> onStop 순서로 호출됨
    //ViewModel은 안드로이드 OS가 메모리에서 삭제하면 다 날라가지만, SIS는 일시적으로 계속 남아있음
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG,"onSaveInstanceState called")
        outState.putInt(KEY_INDEX,quizViewModel.currentIndex)
    }

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

    private lateinit var cheatButton : Button
    private lateinit var launcher: ActivityResultLauncher<Intent> //startActivityForResult가 deprecated 되면서 대체
    private lateinit var versionTextView : TextView

    private var waitTime = 0L
    private val quizViewModel : QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java) //최초의 viewModel이 리턴되고 변경되지 않으므로 val을 쓰기위해 lazy 사용
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if(time - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"한번 더 누르면 종료",Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate() called")
        setContentView(R.layout.activity_main)

        //launcher : ActivityResultLauncher<Intent> 를 아래와 같이 초기화 , 미리 초기화시켜놓는게 안전함
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                RESULT_OK -> {
                    quizViewModel.currentQuestionCheated = it.data?.getBooleanExtra(EXTRA_SHOWN, false) ?: false
                }
            }
        }

        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX,0) ?: 0

        // 화면 회전시 onCreate가 호출될 때 마다 ViewModel을 요청하는데, 최초로 생성했던 걸 그대로 리턴해줌. MainActivity와 생명주기 동일함
        // 액티비티를 완전히 종료하는게 아니라 구성 변경이 발생해서 액티비티가 소멸되고 생성될 때 ViewModel은 계쏙 메모리에 남아있음
//        val quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java) //viewModel 가져옴
        Log.d(TAG, "Got a QuizViewModel : $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)

        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)

        questionTextView = findViewById(R.id.question_text_view)

        cheatButton = findViewById(R.id.cheat_button)
        cheatButton.setOnClickListener { view : View ->
//            val intent = Intent(this,CheatActivity::class.java)
            val answer = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answer)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this) //액티비티 전환시 애니메이션
            launcher.launch(intent,options)
//            startActivityForResult(intent, REQUEST_CODE) //이 코드는 deprecated
//            startActivity(intent)
        }

        versionTextView = findViewById(R.id.os_version_text_view)
        versionTextView.setText("API 레벨 : " + Build.VERSION.SDK_INT)
        
        questionTextView.setOnClickListener { view : View ->
//            currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
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
//            currentIndex = if(currentIndex > 0) {
//                currentIndex - 1
//            } else {
//                questionBank.size - 1
//            }
            quizViewModel.moveToPrev()
            updateQuestion()
            updateButton()
        }

        nextButton.setOnClickListener { view : View ->
//            currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            updateQuestion()
            updateButton()
        }

        updateQuestion()
        updateButton()
    }

    private fun updateButton() {
//        val flag = !questionBank[currentIndex].solved
        val flag = !quizViewModel.currentQuestionSolved
        trueButton.isClickable = flag
        falseButton.isClickable = flag
    }
    private fun updateQuestion() {
//        val questionTextResId = questionBank[currentIndex].textResId
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnser(userAnswer : Boolean) {
//        val answer = questionBank[currentIndex].answer
        val answer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.cheat_toast
            userAnswer == answer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
//        val messageResId = if(userAnswer == answer) {
//            R.string.correct_toast
//        } else {
//            R.string.incorrect_toast
//        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show()
//        questionBank[currentIndex].solved = true
        quizViewModel.currentQuestionSolved = true
        if(messageResId == R.string.correct_toast) {
            quizViewModel.getCorrectCount++
        }
        quizViewModel.getCount++
        if(quizViewModel.getCount == quizViewModel.getBankSize())
            Toast.makeText(this,"점수 : ${quizViewModel.getCorrectCount * 100 / quizViewModel.getBankSize()}점",Toast.LENGTH_SHORT).show()
    }
}