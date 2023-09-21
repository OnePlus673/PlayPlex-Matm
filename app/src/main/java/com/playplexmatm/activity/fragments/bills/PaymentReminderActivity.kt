package com.playplexmatm.activity.fragments.bills

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.databinding.ActivityPaymentReminderBinding
import com.playplexmatm.extentions.CUSTOMER_BALANCE
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.extentions.extractNumber
import com.playplexmatm.extentions.getCurrentDateFormatted
import com.playplexmatm.model.bills.BusinessProfile
import com.playplexmatm.util.toast
import java.io.ByteArrayOutputStream

class PaymentReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentReminderBinding
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_reminder)
        setUpViews()
        businessProfileRecords()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        database = FirebaseDatabase.getInstance()
        binding.currentDate.text = "on ${getCurrentDateFormatted()}"
        val paymentToRemind = intent.getStringExtra("paymentToRemind")
        binding.payment.text = "₹ ${paymentToRemind?.extractNumber()}"
        if (paymentToRemind?.startsWith("get") == true) {
            binding.payment.setTextColor(Color.RED)
        } else if (paymentToRemind?.startsWith("give") == true) {
            binding.payment.setTextColor(Color.GREEN)
        } else {
            binding.payment.setTextColor(Color.GRAY)
        }

        binding.sendReminder.setOnClickListener {
            sendReminderScreenshot()
        }
    }

    private fun captureScreenshot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun sendReminderScreenshot() {
        val view = binding.container
        val screenshot = captureScreenshot(view)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        val uri = getImageUri(this@PaymentReminderActivity, screenshot)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Reminder"))
        finish()
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun businessProfileRecords() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseReference = database.reference
                .child("business_profiles")
                .child(userId)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val businessProfile = snapshot.getValue(BusinessProfile::class.java)

                        if (businessProfile != null) {
                            val businessName = businessProfile.businessName
                            val phone = businessProfile.phone
                            binding.ownerName.text = "Sent by ${businessName}"
                            binding.phone.text = phone
                        }
                    } else {
                        toast("Please Create Your Business Profile From Account")
                        // Data does not exist for the user
                        // Handle this case (e.g., show a message or perform a default action)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.wtf("Business Profile record error", error.message)
                }
            })
        }
    }
}