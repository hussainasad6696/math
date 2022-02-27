package com.example.math

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.math.interfaces.CloseFragmentInterface
import com.example.math.interfaces.PermissionInterfaces
import com.example.math.models.QuestionTypes
import com.example.math.ui.AnswerViewFragment
import com.example.math.ui.MainFragment
import com.example.math.ui.QuestionsListFragment
import com.example.math.util.PermissionUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions

class MainActivity : AppCompatActivity(), PermissionInterfaces, CloseFragmentInterface {
    private lateinit var functions: FirebaseFunctions
    private lateinit var closeFragmentInterface: CloseFragmentInterface

    private fun closeFragmentInterface(closeFragmentInterface: CloseFragmentInterface) {
        this.closeFragmentInterface = closeFragmentInterface
    }

    fun closeFragmentInterface(): CloseFragmentInterface {
        return this.closeFragmentInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        functions = FirebaseFunctions.getInstance()
        changeFragment(MainFragment(functions))
        PermissionUtils.withPermissionUtils(this)
            .withPermissionListener(this)
            .withCameraPermission()

        closeFragmentInterface(this)
    }


    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    override fun permissionGrantedListener() {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    override fun permissionDeniedListener() {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is MainFragment)
            super.onBackPressed()
        else changeFragment(MainFragment(functions))
    }

    override fun closeFragmentListener(questionTypes: QuestionTypes) {
        changeFragment(QuestionsListFragment(functions,questionTypes))
    }
}

