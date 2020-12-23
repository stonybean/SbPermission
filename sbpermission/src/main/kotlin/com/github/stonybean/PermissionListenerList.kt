package com.github.stonybean

import java.util.HashMap

/**
 * Created by stonybean on 2020. 12. 22.
 */
class PermissionListenerList private constructor(): HashMap<String, PermissionListener>() {
    companion object {
        private var permissionListenerList: PermissionListenerList? = null

        @JvmStatic fun getInstance(): PermissionListenerList =
                permissionListenerList ?: synchronized(this) {
                    permissionListenerList
                            ?: PermissionListenerList().also {
                        permissionListenerList = it
                    }
                }
    }
}