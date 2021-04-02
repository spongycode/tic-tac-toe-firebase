package com.spongycode.tictactoe.ui.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.TabLayoutAdapter
import com.spongycode.tictactoe.ui.drawer.ProfileActivity
import com.spongycode.tictactoe.ui.drawer.SettingsActivity
import com.spongycode.tictactoe.utils.Helper
import com.spongycode.tictactoe.utils.Helper.updateToken
import kotlinx.android.synthetic.main.activity_home.*


@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tabLayout: TabLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var viewPager: ViewPager

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnToggle = nav_view.getHeaderView(0).findViewById<Switch>(R.id.toggle_day_night)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Helper.tablayout_color)))

        auth = Firebase.auth


        updateToken(FirebaseInstanceId.getInstance().token)


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


        btnToggle.setOnClickListener {
            if (btnToggle.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Helper.tablayout_color = "#212d3b"

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Helper.tablayout_color = "#407c46"

            }
        }

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

        nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_head_fname).text = Helper.userlogged.fname
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_head_lname).text = Helper.userlogged.lname

        Glide.with(applicationContext).load(Helper.userlogged.imageurl)
                .into(nav_view.getHeaderView(0).findViewById(R.id.nav_profile_pic))
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