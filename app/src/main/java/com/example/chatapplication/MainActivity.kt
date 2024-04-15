package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter:UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private  lateinit var mDbRef: DatabaseReference

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("user")

        sharedPreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE)

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        // To Enter in the database and read values.
        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        // Get the FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token)

//                 Send the token to your server if needed
                sendTokenToServer(token)
            } else {
                Log.e("FCM Token", "Failed to get token")
            }
        }
    }

    private fun sendTokenToServer(token: String?) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

            // Update the FCM token in the database
            dbRef.child("fcmToken").setValue(token)
                .addOnSuccessListener {
                    Log.d("TokenUpdate", "FCM token updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TokenUpdate", "Error updating FCM token", e)
                }
        } else {
            Log.e("TokenUpdate", "Current user is null, cannot update FCM token")
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logOut){

            val editor = sharedPreferences.edit()
            editor.remove("email")
            editor.apply()

            mAuth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}