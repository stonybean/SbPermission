package com.github.stonybean

import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import java.util.*

import com.github.stonybean.sbpermission.R

/**
 * Created by stonybean on 2020. 12. 22.
 */

@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class PermissionCheckActivity : AppCompatActivity() {

    private val permissionListenerList = PermissionListenerList.getInstance()
    private var permissionListener: PermissionListener? = null
    private var requiredPermissions: Array<String>? = null
    private val dangerousPermissions = arrayOf(
            /* CALENDAR */
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            /* CAMERA */
            Manifest.permission.CAMERA,
            /* CONTACTS */
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS,
            /* LOCATION */
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            /* MICROPHONE */
            Manifest.permission.RECORD_AUDIO,
            /* PHONE */
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS,
            /* SENSORS */
            Manifest.permission.BODY_SENSORS,
            /* SMS */
            Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS,
            /* STORAGE */
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var showDeniedDialog: Boolean = false
    private var deniedDialogMessage: String? = null
    private var dialogBuilder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionListener = permissionListenerList[intent.getStringExtra("LISTENER")] // permission listener
        val windowPermission = intent.getBooleanExtra("WINDOW_PERMISSION", false)
        var windowDialogMessage = intent.getStringExtra("WINDOW_DIALOG_MESSAGE")                 // dialog message (window)
        val permissions = intent.getStringArrayExtra("PERMISSIONS")             // required permission list
        showDeniedDialog = intent.getBooleanExtra("SET_DIALOG", false)    // set the dialog
        deniedDialogMessage = intent.getStringExtra("DENIAL")                        // dialog message (denial)

        // check permission(s) to request
        requiredPermissions = if (permissions == null || permissions.isEmpty()) {
            addRequiredPermissions(this, *dangerousPermissions)
        } else {
            addRequiredPermissions(this, *permissions)
        }

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(deniedDialogMessage)) {
            deniedDialogMessage = getString(R.string.deniedDialogMessage)
        }

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(windowDialogMessage)) {
            windowDialogMessage = getString(R.string.windowDialogMessage)
        }

        // check window (overlay) permission
        if (windowPermission && !hasWindowPermission()) {
            requestWindowPermission(windowDialogMessage)
        } else {
            if (requiredPermissions!!.isNotEmpty()) {
                setDeniedDialog(showDeniedDialog, deniedDialogMessage)
                ActivityCompat.requestPermissions(this, requiredPermissions!!, REQUEST_PERMISSION)
            } else {
                finish()
            }
        }
    }

    // add required permissions
    private fun addRequiredPermissions(context: Context?, vararg permissions: String): Array<String> {
        val requiredPermissions: ArrayList<String> = ArrayList()

        if (context == null) return requiredPermissions.toArray(arrayOfNulls<String>(1))

        // Determine whether or not to add the permission by the number of parameters(permissions to request)
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) !== PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission)
                Log.d(TAG, "Required permission : $permission")
            }
        }
        return requiredPermissions.toArray(arrayOfNulls<String>(requiredPermissions.size))
    }

    // set window permission
    @TargetApi(Build.VERSION_CODES.M)
    private fun hasWindowPermission(): Boolean {
        return Settings.canDrawOverlays(applicationContext)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestWindowPermission(message: String) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.windowDialogNegativeButton) { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, REQUEST_SYSTEM_ALERT_WINDOW)
                }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    var grantedNum = 0
                    for (grantResult in grantResults) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            grantedNum += 1
                        }
                    }

                    val grantedList: ArrayList<String> = ArrayList()
                    for (permission in permissions) {
                        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "granted permission = $permission")
                            grantedList.add(permission)
                        }
                    }
                    permissionListener?.onPermissionGranted(grantedList)

                    if (grantedNum == grantResults.size) {
                        // (all) permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Log.i(TAG, "permission was granted")
                        finish()
                    } else {
                        Log.i(TAG, "permission was denied")
                        val deniedList: ArrayList<String> = ArrayList()
                        for (permission in permissions) {
                            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                                Log.d(TAG, "denied permission = $permission")
                                deniedList.add(permission)
                            }
                        }

                        permissionListener?.onPermissionDenied(deniedList) // add denied permission list
                        var deniedNum = 0
                        for (permission in permissions) {
                            // Don't ask again
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                                deniedNum += 1
                            }
                        }

                        if (deniedNum == permissions.size) {
                            finish()
                            return
                        }

                        if (dialogBuilder != null) {
                            dialogBuilder!!.show()
                        } else {
                            finish()
                        }
                    }
                }
            }

            else -> Log.e(TAG, "Error..")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SYSTEM_ALERT_WINDOW -> if (requiredPermissions!!.isNotEmpty()) {
                setDeniedDialog(showDeniedDialog, deniedDialogMessage)
                ActivityCompat.requestPermissions(this, requiredPermissions!!, REQUEST_PERMISSION)
            } else {
                finish()
            }

            REQUEST_SYSTEM_SETTINGS ->
                // from System Settings..
                finish()
        }
    }

    // set denied dialog (true/false, message)
    private fun setDeniedDialog(showDeniedDialog: Boolean, deniedDialogMessage: String?) {
        if (showDeniedDialog) {
            dialogBuilder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
            dialogBuilder!!.setCancelable(false)
                    .setPositiveButton(R.string.deniedDialogPositiveButton
                    ) { _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:$packageName")
                            startActivityForResult(intent, REQUEST_SYSTEM_SETTINGS)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivityForResult(intent, REQUEST_SYSTEM_SETTINGS)
                        }
                    }
                    .setNegativeButton(R.string.deniedDialogNegativeButton
                    ) { dialog, _ ->
                        dialog.cancel()
                        finish()
                    }
                    .setMessage(deniedDialogMessage)
        }
    }

    companion object {
        private val TAG = PermissionCheckActivity::class.java.simpleName
        private const val REQUEST_PERMISSION = 0
        private const val REQUEST_SYSTEM_ALERT_WINDOW = 1
        private const val REQUEST_SYSTEM_SETTINGS = 2
    }
}