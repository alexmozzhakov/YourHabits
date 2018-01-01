package com.doapps.habits.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.doapps.habits.BuildConfig
import com.doapps.habits.data.AvatarData
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.yongchun.library.view.ImageSelectorActivity
import java.io.ByteArrayOutputStream
import java.util.*

class EditPhotoActivity : AppCompatActivity() {
  private lateinit var bitmap: Bitmap
  private val requestImage: Int = ImageSelectorActivity.REQUEST_IMAGE

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val permissionCheck = ContextCompat
        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    when (permissionCheck) {
      PERMISSION_GRANTED -> openImageSelector()
      else ->
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }
  }

  private fun deniedToast() =
      Toast.makeText(this, "This function needs read/write permission", Toast.LENGTH_SHORT).show()


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == requestImage) {
      if (resultCode == RESULT_OK && data != null) {
        // Get the result list of select image paths

        @Suppress("UNCHECKED_CAST")
        val images = data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
        getSharedPreferences("pref", Context.MODE_PRIVATE).edit().putString("avatar", images[0]).apply()
        bitmap = BitmapFactory.decodeFile(images[0])
        if (BuildConfig.DEBUG) Log.i(TAG, String.format("%.2f Mb image is going to upload", bitmap.byteCount / 10e6))
        uploadImage()
      } else {
        finish()
      }
    }
  }

  private fun uploadImage() {
    Toast.makeText(applicationContext, "Uploading...", Toast.LENGTH_LONG).show()
    val stringRequest = object : StringRequest(Request.Method.POST, UPLOAD_URL,
        { s ->
          val user = FirebaseAuth.getInstance().currentUser
          if (user != null) {
            Toast.makeText(this, "Upload complete", Toast.LENGTH_LONG).show()

            val uri = Uri.parse(s)
            AvatarData.setValue(uri)
            Picasso.with(applicationContext).invalidate(uri)
          } else {
            Toast.makeText(this, s, Toast.LENGTH_LONG).show()
          }
        },
        { volleyError ->
          Toast.makeText(this, "Server error " + volleyError.networkResponse.statusCode,
              Toast.LENGTH_LONG).show()
        }) {
      override fun getParams(): Map<String, String> {
        //Creating parameters
        val params = HashMap<String, String>(1)

        //Adding parameters
        params.put(KEY_IMAGE, getStringImage(bitmap))
        val user = FirebaseAuth.getInstance().currentUser!!
        params.put("uid", user.uid)

        //returning parameters
        return params
      }
    }
    stringRequest.retryPolicy = object : RetryPolicy {
      override fun getCurrentTimeout() = 15000
      override fun getCurrentRetryCount() = 0
      override fun retry(error: VolleyError) = Unit // ignored
    }
    //Creating a Request Queue
    val requestQueue = Volley.newRequestQueue(this)

    //Adding request to the queue
    requestQueue.add(stringRequest)
    finish()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                          grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      1 -> openImageSelector()
      else -> deniedToast()
    }
  }

  companion object {
    /**
     * The [String] instance representing backend server API for base64 image uploading
     */
    private val UPLOAD_URL = "http://habit.esy.es/upload.php"
    /**
     * Key which server parses as an image
     */
    private val KEY_IMAGE = "image"
    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = EditPhotoActivity::class.java.simpleName

    private fun getStringImage(bmp: Bitmap): String {
      val stream = ByteArrayOutputStream()
      bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      val imageBytes = stream.toByteArray()
      return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
  }

  private fun openImageSelector() {
    ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, false, true)
  }
}
