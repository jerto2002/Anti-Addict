package com.example.appduration

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.TestFirebase.UserTest
import com.example.appduration.databinding.TestfirebaseBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random
//https://www.youtube.com/watch?v=miJooBq9iwE&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=4

class FirebaseActivity : AppCompatActivity() {
    private lateinit var binding: TestfirebaseBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = TestfirebaseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        testfirebase();
    }
    private lateinit var dbRef: DatabaseReference;
    fun testfirebase(){
        //val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference();
        binding.btnfetch.setOnClickListener(){

        }
        dbRef = FirebaseDatabase.getInstance("https://appduration-default-rtdb.europe-west1.firebasedatabase.app").getReference("Testusers");
        binding.btninsert.setOnClickListener(){

            sendData();
        }
    }

    fun sendData(){
        val userId = dbRef.push().key!!
        val names = listOf("jeroen", "tom", "chris", "joey", "hello","peter", "clark", "ash")
        var name = names.get(Random.nextInt(0, names.size -1))
        val userTest = UserTest(userId, name);

        dbRef.child(userId).setValue(userTest).addOnCompleteListener{
            Toast.makeText(this, "Data has been send", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            err -> Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
        }

    }
}