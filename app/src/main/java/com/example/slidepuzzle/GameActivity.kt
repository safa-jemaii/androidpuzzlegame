package com.example.slidepuzzle

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Size
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.slidepuzzle.ui.boardoptions.BoardOptionsViewModel
import com.example.slidepuzzle.ui.boardoptions.BoardTitledSize
import com.example.slidepuzzle.ui.game.GameBoard

data class BoardActivityParams(val bitmap: Bitmap, val size: BoardTitledSize)

class GameActivity : AppCompatActivity() {

    var score = 0
    var handler: Handler = Handler()
    var runnable:Runnable= Runnable {  }
    lateinit var textView : TextView
    companion object {
        lateinit var initialConfig: BoardActivityParams
    }

    private val viewModel: BoardOptionsViewModel by lazy {
        ViewModelProviders.of(this).get(BoardOptionsViewModel::class.java)
    }

    private fun mountBoard() {
        val board = findViewById<GameBoard>(R.id.boardView)

        //timer
        textView = findViewById(R.id.textView)
        // time count down for 30 seconds,
        // with 1 second as countDown interval
        object : CountDownTimer(30000, 1000) {

            override fun onFinish() {

//                TimeText.text= "Time: 0"
                score=0
//                ScoreText.text="Score: 0"
                handler.removeCallbacks(runnable)


                var alertDialog= AlertDialog.Builder(this@GameActivity)
                alertDialog.setTitle("Game Over")
//                alertDialog.setMessage("Do you want to play again?")
//               alertDialog.setPositiveButton("Yes, Play Again") { dialog: DialogInterface?, which: Int ->
//                   Game()
//                }
//                alertDialog.setNegativeButton("No"){ dialog: DialogInterface?, which: Int -> finish() }
                alertDialog.show()


                intent = Intent(applicationContext, BoardOptionsActivity::class.java)
                    startActivity(intent)


            }


            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                textView.setText("seconds remaining: " + millisUntilFinished / 1000)

//                if(millisUntilFinished == 0){
//                    intent = Intent(applicationContext, BoardOptionsActivity::class.java)
//                    startActivity(intent)
//
//
//                }
            }

            // Callback function, fired
            // when the time is up
//            override fun onFinish() {
//                textView.setText("time finish!")
//            }
        }.start()

        //puzzle
        viewModel.boardSize.observe(this, Observer {
            it?.let {
                board.resize(
                    Size(it.width, it.height),
                    viewModel.boardImage.value
                )
            }
        })

        findViewById<Button>(R.id.shuffle).setOnClickListener {
            board.shuffle()
        }

        findViewById<Button>(R.id.reset).setOnClickListener {
            board.shuffle(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.apply {
            boardSize.value = initialConfig.size
            boardImage.value = initialConfig.bitmap
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.game_activity)





        //puzzle
        setSupportActionBar(findViewById(R.id.board_options_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mountBoard()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
