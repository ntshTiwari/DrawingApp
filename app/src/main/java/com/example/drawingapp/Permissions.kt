package com.example.drawingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale

object Permissions {

    fun getPermissionReadStorage(activity: Activity, cameraResultLauncher: ActivityResultLauncher<String>) {
        /// shouldShowRequestPermissionRationale => this checks if Whether you should show permission rationale UI
        /// this will return true if we should not show permission dialog
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            showDeniedDialog(
                " Permission Demo requires Storage access",
                "Storage cannot be used because Storage access is denied",
                activity,
            )
        } else {
            cameraResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun getPermissionWriteStorage(activity: Activity, cameraResultLauncher: ActivityResultLauncher<String>) {
        /// shouldShowRequestPermissionRationale => this checks if Whether you should show permission rationale UI
        /// this will return true if we should not show permission dialog
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ){
            showDeniedDialog(
                " Permission Demo requires write to Storage access",
                "Storage cannot be used because Storage access is denied",
                activity,
            )
        } else {
            cameraResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showDeniedDialog(
        title: String,
        message: String,
        activity: Activity
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        /// setPositiveButton -> https://developer.android.com/reference/android/app/AlertDialog.Builder#setPositiveButton(int,%20android.content.DialogInterface.OnClickListener)
        ///                     Set a listener to be invoked when the positive button of the dialog is pressed.
        builder.setPositiveButton("Cancel"){
            dialog, _ -> dialog.dismiss()
        }
        /// this block can also be written as
//        builder.setTitle(title)
//            .setMessage(message)
//            .setPositiveButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }

        builder.create().show()
    }
}