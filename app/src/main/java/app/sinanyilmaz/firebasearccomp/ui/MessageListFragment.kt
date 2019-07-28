package app.sinanyilmaz.firebasearccomp.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import java.util.Arrays

import app.sinanyilmaz.firebasearccomp.DataRepository
import app.sinanyilmaz.firebasearccomp.R
import app.sinanyilmaz.firebasearccomp.data.Entity
import app.sinanyilmaz.firebasearccomp.databinding.MessageListFragmentBinding
import app.sinanyilmaz.firebasearccomp.viewmodel.MessagesListViewModel

import android.app.Activity.RESULT_CANCELED
import android.arch.lifecycle.Observer


class MessageListFragment : Fragment() {

    private var mModel: MessagesListViewModel? = null
    private var mBinding: MessageListFragmentBinding? = null
    private var mFragmentCallback: MyFragmentListenerImpl? = null
    private val mMessageAdapter = MessageAdapter()
    private val toast: Toast? = null

    private val mFirebaseAuth = FirebaseAuth.getInstance()
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    internal interface MyFragmentListenerImpl {
        fun onFabButtonClicked()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(activity!!).get(MessagesListViewModel::class.java!!)
        FirebaseApp.initializeApp(activity!!)
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = mFirebaseAuth.getCurrentUser()
            if (user != null) {
                DataRepository.instance.userName = user!!.getDisplayName()
            } else {
                Log.d(TAG, "user is null")
                DataRepository.instance.userName = ANONYMOUS
                // Choose authentication providers
                val providers = Arrays.asList(
                        //     new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        //   new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build() );
                        AuthUI.IdpConfig.GoogleBuilder().build())
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .setLogo(R.mipmap.ic_launcher)
                                .build(),
                        RC_SIGN_IN)
            }
        }


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.message_list_fragment,
                container, false)


        val layoutManager = LinearLayoutManager(activity)
        mBinding!!.recyclerview.layoutManager = layoutManager


        mBinding!!.fab.setOnClickListener { v -> mFragmentCallback!!.onFabButtonClicked() }

        return mBinding!!.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "on attach")

        try {
            mFragmentCallback = context as MyFragmentListenerImpl?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement OnHeadlineSelectedListener")
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")

        mBinding!!.recyclerview.adapter = mMessageAdapter

        // Update the list when the data changes
        if (mModel != null) {
            val liveData = mModel!!.messageListLiveData



            liveData.observe(activity!!, Observer {
                var mEntities = it
                mMessageAdapter.setMessageList(mEntities!!)
            })
            //{ mEntities: List<Entity> -> mMessageAdapter.setMessageList(mEntities) }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "result code = $resultCode")

        if (resultCode == RESULT_CANCELED) {
            return
        }
        when (requestCode) {
            RC_SIGN_IN -> Toast.makeText(activity, "Signed in", Toast.LENGTH_SHORT).show()

            else -> Log.w(TAG, "switch(requestCode), case not implemented.")
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        mFirebaseAuth.addAuthStateListener(mAuthStateListener!!)
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener!!)
        }

    }

    companion object {

        private val TAG = "MessageListFragment"
        private val ANONYMOUS = "anonymous"
        private val RC_SIGN_IN = 1
    }


}
