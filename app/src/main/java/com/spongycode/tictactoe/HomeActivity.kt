package com.spongycode.tictactoe

import TabLayoutAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()


        // Tab Layout init
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout.addTab(tabLayout.newTab().setText("Blogs"))
        tabLayout.addTab(tabLayout.newTab().setText("Live Games"))
        tabLayout.addTab(tabLayout.newTab().setText("Friends"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabLayoutAdapter(
                this, supportFragmentManager,
                tabLayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        // Tab Layout end


        // Nav Drawer init
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.miItem1 -> {
                    drawer_layout.closeDrawers()
                    startActivity(Intent(this, ProfileActivity::class.java))
                }

                R.id.miItem2 -> {
                    drawer_layout.closeDrawers()
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.miItem3 -> {
                    Firebase.auth.signOut()
                    startActivity(Intent(this, SigninActivity::class.java))
                    finish()
                }
            }
            true
        }
        // Nav Drawer end


        firestore.collection("users")
                .addSnapshotListener { snapshot, e ->
                    getProfileNameAndPic()
                }

        getProfileNameAndPic()


    }

    private fun getProfileNameAndPic() {
        firestore.collection("users")
                .whereEqualTo("userid", auth.currentUser?.uid.toString())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            val fname = data.toObject(UserDataClass::class.java).fname
                            val lname = data.toObject(UserDataClass::class.java).lname
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_head_fname)
                                    .setText(fname)
                            nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_head_lname)
                                    .setText(lname)
                            //
                            //
                            Glide.with(this).load(imageurl)
                                    .into(
                                            nav_view.getHeaderView(0)
                                                    .findViewById<ImageView>(R.id.nav_profile_pic)
                                    )
                        }
                    } else {
                        // to handle
                    }
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(
                ev
        )
    }

}