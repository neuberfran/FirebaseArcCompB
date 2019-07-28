package app.sinanyilmaz.firebasearccomp.viewmodel

import android.arch.core.util.Function
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.net.Uri
import android.util.Log

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import java.util.ArrayList

import app.sinanyilmaz.firebasearccomp.data.FirebaseQueryLiveData
import app.sinanyilmaz.firebasearccomp.data.Entity

class MessagesListViewModel : ViewModel() {

    private val mList = ArrayList<Entity>()

    val messageListLiveData: LiveData<List<Entity>>
        get() {
            val mLiveData = FirebaseQueryLiveData(dataRef)

            return Transformations.map(mLiveData, Deserializer())
        }


    var photoUrl = ""

    //NewPostFragment
    val pictureUploadIsSuccessful = MutableLiveData<Boolean>()
    val messageUploadIsSuccessful = MutableLiveData<Boolean>()

    private inner class Deserializer : Function<DataSnapshot, List<Entity>> {

        override fun apply(dataSnapshot: DataSnapshot): List<Entity> {
            mList.clear()
            for (snap in dataSnapshot.children) {
                val msg = snap.getValue<Entity>(Entity::class.java!!)
                mList.add(msg!!)
            }
            return mList
        }
    }


    fun createAndSendToDataBase(userName: String, descriptionText: String, mPhoto: String) {
        val entity = Entity(descriptionText, userName, mPhoto)

        // push the new message to Firebase
        val uploadTask = FirebaseDatabase.getInstance()
                .reference
                .child("messages")
                .push()
                .setValue(entity)
        uploadTask.addOnSuccessListener { o -> messageUploadIsSuccessful.setValue(true) }
    }

    fun uploadPicture(intentData: Intent) {
        val selectedUri = intentData.data
        val photoRef = FirebaseStorage.getInstance()
                .reference.child("photos")
                .child(selectedUri!!.lastPathSegment!!)

        val uploadTask = photoRef.putFile(selectedUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            photoUrl = taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            //            taskSnapshot.getMetadata().getReference().getDownloadUrl() toString()""?><<<""}{""?±!±±±±!!@#?++_))((*&&&ˆˆ%$#@!
            pictureUploadIsSuccessful.setValue(true)
        }
        uploadTask.addOnFailureListener { e -> pictureUploadIsSuccessful.setValue(false) }
    }

    companion object {
        private val TAG = "ListViewModel"

        private val dataRef = FirebaseDatabase.getInstance().reference.child("messages")
    }


}


