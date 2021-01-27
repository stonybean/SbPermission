# sbpermission

<br>

# Setup

To get a Git project into your build:
## Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}
```
<br>

## Step 2. Add the dependency
```gradle
dependencies {
    implementation 'com.github.stonybean:sbpermission:1.0.3'
}
```
Share this release:
<br>
[![](https://jitpack.io/v/stonybean/sbpermission.svg)](https://jitpack.io/#stonybean/sbpermission)

<br>

# How to use

## Simple
```gradle
// Start checkPermission
val permissionBuilder = PermissionBuilder(context)
permissionBuilder.checkPermissions()
```
<br>

## With options

```gradle
val permissionBuilder = PermissionBuilder(context)

// Add Listener
val permissionListener = object : PermissionListener {
    override fun onPermissionGranted(grantedPermissions: ArrayList<String>) {
        // Do somthing..
        for (grantedPermission in grantedPermissions) {
            if (grantedPermission == Manifest.permission.CAMERA) {
                // do something..
            }
            ...
        }
    }

    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        // Do somthing..
        for (deniedPermission in deniedPermissions) {
            if (deniedPermission == Manifest.permission.CAMERA) {
                // do something..
            }
            ...
        }
    }
}

// Start checkPemission with options
permissionBuilder.setWindowPermission(true)
            .setWindowDialogMessage("message..")
            .setDeniedDialog(true)
            .setDeniedDialogMessage(R.string.deniedDialogMessage)
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS
            )
            .setPermissionListener("MyListener", permissionListener)
            .checkPermissions()
```

<br>

## Details


- `setWindowPermission(Boolean)`
<br>: Set up a overlay permission
<br>

- `setWindowDialogMessage(String or R.string.xxx)` `(default getString(R.string.windowDialogMessage))`
<br>: Set up a dialog message when setting up overlay permission
<br>

- `setDeniedDialog(Boolean)`
<br>: Show dialog when rejecting to set up a permission
<br>

- `setDeniedDialogMessage(String or R.string.xxx)` `(default getString(R.string.deniedDialogMessage))`
<br>: Set up a dialog message when rejecting to set up a permission
<br>

- `setPermissions(vararg String)` `(If you do not add permissions, it will be added default all dangerous permissions automatically.)`
<br>: Add permission list you need
<br>

- `setPermissionListener(String, PermissionListener)`
<br>: Set permission listener
<br>

- `checkPermissions`
<br>: Start checkPermissions
<br>

