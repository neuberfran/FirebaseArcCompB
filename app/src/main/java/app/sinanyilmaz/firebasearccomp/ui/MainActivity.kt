package app.sinanyilmaz.firebasearccomp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import app.sinanyilmaz.firebasearccomp.R


class MainActivity : AppCompatActivity(), MessageListFragment.MyFragmentListenerImpl {

    private val mComposerFragment = NewPostFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onFabButtonClicked() {

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mComposerFragment)
                .addToBackStack(null)
                .commit()
    }

    companion object {
        private val TAG = "MainActivity"
    }

}
