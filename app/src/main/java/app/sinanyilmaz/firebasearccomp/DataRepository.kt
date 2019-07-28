package app.sinanyilmaz.firebasearccomp

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class DataRepository private constructor() {

    var userName: String? = null
    val firebaseDbReference: FirebaseDatabase
    private val mMessageDbReference: DatabaseReference


    init {
        firebaseDbReference = FirebaseDatabase.getInstance()
        mMessageDbReference = firebaseDbReference.reference

    }

    companion object {
        private var sInstance: DataRepository? = null

        val instance: DataRepository
            get() {
                if (sInstance == null) {
                    synchronized(DataRepository::class.java) {
                        if (sInstance == null) {
                            sInstance = DataRepository()
                        }
                    }
                }
                return sInstance!!
            }
    }

}
