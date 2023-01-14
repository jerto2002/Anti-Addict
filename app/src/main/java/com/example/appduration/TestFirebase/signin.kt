package com.example.appduration


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.Functions.SaveAndloadApplistFile.Companion.writeToFile
import com.example.appduration.TestFirebase.Backup
import com.example.appduration.View.MainActivity
import com.example.appduration.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


//https://www.youtube.com/watch?v=miJooBq9iwE&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=4

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference;
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        firebaseAuth = FirebaseAuth.getInstance();

        binding.textView.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java);
            startActivity(intent);
        }

        //https://www.youtube.com/watch?v=idbxxkF1l6k&t=2s
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString();
            val pass = binding.passET.text.toString();
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showpopup()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "not all parameters are filled", Toast.LENGTH_SHORT).show();
            }
        }
        setSupportActionBar(findViewById(R.id.my_toolbar))
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun showpopup(){//https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android
        // step 1
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popupsinc, null)

        // step 2
        val wid = LinearLayout.LayoutParams.WRAP_CONTENT
        val high = LinearLayout.LayoutParams.WRAP_CONTENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)

        // step 3
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        val btnNoSync = popupView.findViewById(R.id.btnNosync) as Button
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        val btnSync = popupView.findViewById(R.id.btnYessync) as Button
        btnNoSync.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            Toast.makeText(this, "succesfull login", Toast.LENGTH_SHORT).show();
        }
        btnSync.setOnClickListener{
            RestoreData();
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            Toast.makeText(this, "succesfull login", Toast.LENGTH_SHORT).show();
        }

    }
    fun RestoreData(){
        dbRef =
            FirebaseDatabase.getInstance("https://appduration-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Backup");
        firebaseAuth = FirebaseAuth.getInstance();
        val userId = firebaseAuth.uid;
        var empList = arrayListOf<Backup>()
        if (userId != null) {
            var test = dbRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userdata = snapshot.getValue(Backup::class.java);
                        var mPrefs = getSharedPreferences("time", 0)
                        var mEditor = mPrefs.edit()
                        mEditor.putFloat("savedtime", userdata?.UseTime!!).commit()
                        mPrefs = getSharedPreferences("battery", 0)
                        mEditor = mPrefs.edit()
                        mEditor.putFloat("percent",  userdata?.BatterySaveModePercent!!).commit()
                        mEditor.putBoolean("savemode", userdata?.BatterySaveMode!!).commit()
                        writeToFile(applicationContext, userdata?.Applist!!, packageManager);
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "logged out", Toast.LENGTH_SHORT).show()
                }
            })
        };
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }
}