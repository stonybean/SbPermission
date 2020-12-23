package com.github.stonybean

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.StringRes
import android.util.Log
import java.util.ArrayList

/**
 * Created by stonybean on 2020. 12. 22.
 */

class PermissionBuilder(private val context: Context) {

    private var listenerName: String = ""
    private val permissionListenerList = PermissionListenerList.getInstance()

    private var permissions: Array<String>? = null
    private var windowPermission: Boolean = false
    private var showDeniedDialog: Boolean = false
    private var deniedDialogMessage: CharSequence? = null
    private var windowDialogMessage: CharSequence? = null

    // Essential (check permissions)
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "Build version is low level")
            return
        }

        val intent = Intent(context, PermissionCheckActivity::class.java)
        if (listenerName.isNotEmpty()) {
            intent.putExtra("LISTENER", listenerName)
        }
        intent.putExtra("WINDOW_PERMISSION", windowPermission)
        intent.putExtra("WINDOW_DIALOG_MESSAGE", windowDialogMessage)
        intent.putExtra("PERMISSIONS", permissions)
        intent.putExtra("SET_DIALOG", showDeniedDialog)
        intent.putExtra("DENIAL", deniedDialogMessage)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // Optional
    fun setPermissionListener(listenerName: String, permissionListener: PermissionListener): PermissionBuilder {
        this.listenerName = listenerName
        permissionListenerList[listenerName] = permissionListener
        Log.d(TAG, "permissionListener = " + permissionListenerList[listenerName])
        return this
    }

    // Optional
    fun setWindowPermission(windowPermission: Boolean): PermissionBuilder {
        this.windowPermission = windowPermission
        return this
    }

    // Optional
    fun setWindowDialogMessage(windowDialogMessage: CharSequence): PermissionBuilder {
        this.windowDialogMessage = windowDialogMessage
        return this
    }

    // Optional
    fun setWindowDialogMessage(@StringRes windowDialogMessage: Int): PermissionBuilder {
        // ex) R.string.mytext
        this.windowDialogMessage = getText(windowDialogMessage)
        return this
    }

    // Optional
    fun setPermissions(vararg permissions: String): PermissionBuilder {
        val requiredPermissions: ArrayList<String> = ArrayList()

        for (permission in permissions) {
            requiredPermissions.add(permission)
            this.permissions = requiredPermissions.toArray(arrayOfNulls<String>(requiredPermissions.size))
        }
        return this
    }

    // Optional
    fun setDeniedDialog(showDeniedDialog: Boolean): PermissionBuilder {
        this.showDeniedDialog = showDeniedDialog
        return this
    }

    // Optional
    fun setDeniedDialogMessage(deniedDialogMessage: CharSequence): PermissionBuilder {
        this.deniedDialogMessage = deniedDialogMessage
        return this
    }

    // Optional
    fun setDeniedDialogMessage(@StringRes deniedDialogMessage: Int): PermissionBuilder {
        // ex) R.string.mytext
        this.deniedDialogMessage = getText(deniedDialogMessage)
        return this
    }

    private fun getText(stringRes: Int): CharSequence {
        if (stringRes <= 0) {
            throw IllegalArgumentException("Invalid String resource Id")
        }
        return context.getText(stringRes)
    }

    companion object {
        private val TAG = PermissionBuilder::class.java.simpleName
    }
}
