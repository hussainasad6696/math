package com.example.math.util

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.math.interfaces.PermissionInterfaces

class PermissionUtils(private val appCompatActivity: AppCompatActivity) {

    private lateinit var permissionInterface: PermissionInterfaces

    fun withPermissionListener(permissionListener: PermissionInterfaces): PermissionUtils {
        this.permissionInterface = permissionListener
        return this
    }

    private val permissionLauncher = appCompatActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            if (::permissionInterface.isInitialized)
                permissionInterface.permissionGrantedListener()
            else Log.e("TAG", ": permissionGranted")
        } else {
            if (::permissionInterface.isInitialized)
                permissionInterface.permissionDeniedListener()
            else Log.e("TAG", ": permissionDenied")
        }
    }

    fun withCameraPermission(){
        if (ContextCompat.checkSelfPermission(appCompatActivity.applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            if (::permissionInterface.isInitialized)
                permissionInterface.permissionGrantedListener()
            else Log.e("TAG", "withCameraPermission: Permission Granted", )
        }else {
            permissionLauncher.launch(Manifest.permission.CAMERA).also {
                if (::permissionInterface.isInitialized) {
                    permissionInterface.permissionDeniedListener()
                    Log.e("TAG", "withCameraPermission: Requesting permission")
                }else Log.e("TAG", "withCameraPermission: Requesting permission")
            }
        }
    }


    companion object{
        fun withPermissionUtils(appCompatActivity: AppCompatActivity): PermissionUtils {
            return PermissionUtils(appCompatActivity)
        }
    }
}