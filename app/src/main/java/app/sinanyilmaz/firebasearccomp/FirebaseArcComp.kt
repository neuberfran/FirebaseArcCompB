package app.sinanyilmaz.firebasearccomp

import android.app.Application

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase


class FirebaseArcComp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
}
