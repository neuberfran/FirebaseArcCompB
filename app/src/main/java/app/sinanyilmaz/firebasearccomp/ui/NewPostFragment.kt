package app.sinanyilmaz.firebasearccomp.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.bumptech.glide.Glide

import app.sinanyilmaz.firebasearccomp.DataRepository
import app.sinanyilmaz.firebasearccomp.R
import app.sinanyilmaz.firebasearccomp.databinding.NewPostFragmentBinding
import app.sinanyilmaz.firebasearccomp.viewmodel.MessagesListViewModel

import android.app.Activity.RESULT_CANCELED
import android.arch.lifecycle.Observer
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream


class NewPostFragment : Fragment() {
    //public static final String ARG_POSITION = "USER_NAME";

    private var mBinding: NewPostFragmentBinding? = null
    private var mViewModel: MessagesListViewModel? = null
    private var mUserName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mUserName = DataRepository.instance.userName

        // DataBinding
        mBinding = DataBindingUtil.inflate(inflater, R.layout.new_post_fragment, container, false)

        mViewModel = ViewModelProviders.of(activity!!).get(MessagesListViewModel::class.java!!)

        // attach listeners
        mViewModel!!.pictureUploadIsSuccessful.observe(this, Observer {
            var isSuccess = it
            if (isSuccess!!) {

                if (!mViewModel!!.photoUrl.isEmpty()) {
                    GlideApp.with(mBinding!!.photoView.context)
                            .load(mViewModel!!.photoUrl)
                            .into(mBinding!!.photoView)
                    Toast.makeText(context, "Picture Upload successful", Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(context, "Could not fetch the picture!", Toast.LENGTH_LONG).show()
            }
        })
//                { isSuccess ->

//        })

        mViewModel!!.messageUploadIsSuccessful.observe(this,
            Observer {
                var isSuccessful = it
                if (isSuccessful!! && !mViewModel!!.photoUrl.isEmpty()) {
                    Toast.makeText(context, "Message Upload successful", Toast.LENGTH_SHORT).show()
                }
            }
        )


        mBinding!!.photoPickerButton.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "complete action using"), RC_PHOTO_PICKER)
        }


        mBinding!!.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0) {
                    mBinding!!.sendButton.isEnabled = true
                } else {
                    mBinding!!.sendButton.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        // Send button sends a message and clears the EditText
        mBinding!!.sendButton.setOnClickListener { view ->
            val mText = mBinding!!.messageEditText.text.toString()

            if (mViewModel!!.photoUrl != null && !mViewModel!!.photoUrl.isEmpty()) {
                mViewModel!!.createAndSendToDataBase(
                        mUserName!!,
                        mText,
                        mViewModel!!.photoUrl)

                mBinding!!.messageEditText.setText("")
                mViewModel!!.photoUrl = ""
                dismissKeyboard()
                activity!!.onBackPressed()
            } else {
                Toast.makeText(context, "Please, choose a picture.", Toast.LENGTH_SHORT).show()
            }

        }

        return mBinding!!.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "result code = $resultCode")

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(context, "Action canceled", Toast.LENGTH_SHORT).show()
            return
        }

        when (requestCode) {
            RC_PHOTO_PICKER ->

                mViewModel!!.uploadPicture(data!!)
            else -> Log.w(TAG, "switch(requestCode), case not implemented.")
        }

    }

    private fun dismissKeyboard() {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null && null != activity!!.currentFocus)
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!
                    .applicationWindowToken, 0)
    }

    companion object {

        private val TAG = "NewPostFragment"
        private val RC_PHOTO_PICKER = 1
    }

    @GlideModule
    inner class MyAppGlideModule : AppGlideModule() {

        override fun registerComponents(context: Context , glide: Glide , registry: Registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference::class.java , InputStream::class.java ,
                    FirebaseImageLoader.Factory())
        }
    }

}
