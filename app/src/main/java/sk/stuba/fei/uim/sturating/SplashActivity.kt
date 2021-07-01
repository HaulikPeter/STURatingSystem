package sk.stuba.fei.uim.sturating

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

// class for the splash activity
class SplashActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // asking for the instance of the auth service
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        // checking if the user was logged in previously
        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1500)
    }
}