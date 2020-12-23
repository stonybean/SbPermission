package com.github.stonybean.sbpermission_kt

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.stonybean.PermissionBuilder
import com.github.stonybean.PermissionListener

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val test = PermissionBuilder(this@SampleActivity)

        /************ simple call ************/
//        test.checkPermissions()


        /************ with option call ************/
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted(grantedPermissions: ArrayList<String>) {
                // Do somthing..

//                for (grantedPermission in grantedPermissions) {
//                    Log.d("TEST", "grantedPermission : $grantedPermission")
//                    if (grantedPermission == Manifest.permission.CAMERA) {
//                        // do something..
//                    }
//                }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                // Do somthing..

//                for (deniedPermission in deniedPermissions) {
//                    Log.d("TEST", "deniedPermission : $deniedPermission")
//                    if (deniedPermission == Manifest.permission.CAMERA) {
//                        // do something..
//                    }
//                }
            }
        }

        test.setWindowPermission(true)
            .setWindowDialogMessage("message..")
            .setDeniedDialog(true)
            .setDeniedDialogMessage(R.string.denied_dialog_message)
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS
            )
            .setPermissionListener("SampleListener", permissionListener)
            .checkPermissions()
    }
}
