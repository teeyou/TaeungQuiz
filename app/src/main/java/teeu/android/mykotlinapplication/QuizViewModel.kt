package teeu.android.mykotlinapplication

import android.util.Log
import androidx.lifecycle.ViewModel
private const val TAG = "QuizViewModel"

// Home버튼을 누르거나, 다른 어플을 실행할 때 안드로이드 OS가 onStop()상태의 어플을 메모리에서 지울 수 있는데
// 이런 경우에는 onDestroy나 onCleared가 호출되지 않는다
// onSaveInstanceState 같은 경우에는 일시적이지만 외부에 저장되기 때문에 데이터가 바로 날라가지 않는다
// onPause이후에 onSaveInstanceState가 호출되기 때문에 자신의 상태를 Bundle 객체에 저장해서 onStop전에 저장할 수 있다
class QuizViewModel : ViewModel() {
    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"ViewModel instance about to be destroyed")
    }

    private val questionBank = listOf(
        Question(R.string.question_age,false, false),
        Question(R.string.question_dgu,true, false),
        Question(R.string.question_korean,true, false),
        Question(R.string.question_major,false, false),
    )
    var currentIndex = 0

    private var correctCount = 0
    private var count = 0

    val currentQuestionAnswer : Boolean
    get() = questionBank[currentIndex].answer

    val currentQuestionText : Int
    get() = questionBank[currentIndex].textResId

    var currentQuestionSolved : Boolean
    get() = questionBank[currentIndex].solved
    set(value) {
        questionBank[currentIndex].solved = value
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = if(currentIndex > 0)
            currentIndex - 1
        else
            questionBank.size - 1
    }

    fun getBankSize() : Int = questionBank.size

    var getCorrectCount : Int
    get() = correctCount
    set(value) {correctCount = value}

    var getCount : Int
    get() = count
    set(value) {count = value}
}