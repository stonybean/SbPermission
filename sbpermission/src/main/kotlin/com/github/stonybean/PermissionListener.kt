package com.github.stonybean

import java.util.ArrayList

/**
 * Created by stonybean on 2020. 12. 22.
 */
interface PermissionListener {
    fun onPermissionGranted(grantedPermissions: ArrayList<String>)
    fun onPermissionDenied(deniedPermissions: ArrayList<String>)

}
