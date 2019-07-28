package app.sinanyilmaz.firebasearccomp.data

import android.arch.lifecycle.LiveData
import android.os.Handler
import android.util.Log

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList


class FirebaseQueryLiveData : LiveData<DataSnapshot> {

    private var query: Query? = null
    private val valueListener = mValueEventListener()
    //private final ChildEventListener childListener = new MyEventListener();

    private val mQueryValuesList = ArrayList<Entity>()

    private var listenerRemovePending = false
    private val handler = Handler()
    private val removeListener = Runnable {
        query!!.removeEventListener(valueListener)
        listenerRemovePending = false
    }

    constructor(query: Query) {
        this.query = query
    }

    constructor(dbReference: DatabaseReference) {
        this.query = dbReference
    }

    override fun onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            query!!.addValueEventListener(valueListener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        // Listener removal is schedule on a two second delay

        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }


    private inner class mValueEventListener : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = dataSnapshot
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Cannot listen to query $query", databaseError.toException())
        }
    }


    private inner class MyEventListener : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            if (dataSnapshot != null) {
                Log.d(LOG_TAG, "onChildAdded(): previous child name = " + s!!)
                setValue(dataSnapshot)
                for (snap in dataSnapshot.children) {
                    val msg = snap.getValue<Entity>(Entity::class.java!!)
                    if (msg != null) {
                        mQueryValuesList.add(msg)
                    }
                }
            } else {
                Log.w(LOG_TAG, "onChildAdded(): data snapshot is NULL")
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Cannot listen to query $query", databaseError.toException())
        }

    }

    companion object {
        private val LOG_TAG = "FirebaseQueryLiveData"
    }

}
