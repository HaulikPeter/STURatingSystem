package sk.stuba.fei.uim.sturating

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class STURatingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}