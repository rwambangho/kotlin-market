package com.example.carrotmarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.carrotmarket.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity  : AppCompatActivity(){
    private val fragmentHome by lazy {HomeFragment()}
    private val fragmentChat by lazy {ChatFragment()}
    private val fragmentMyPage by lazy {MyPageFragment()}

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //프래그먼트 시작
        //setTheFirstFragment()
        initNavigationBar()

    }


    private fun initNavigationBar() {
        var selectedItemId = 0
        binding.bottomNavi.run {
            setOnItemSelectedListener { item->

                when(item.itemId) {
                    R.id.home -> {
                        changeFragment(fragmentHome)
                    }
                    R.id.chat -> {
                        changeFragment(fragmentChat)
                    }
                    R.id.myPage -> {
                        changeFragment(fragmentMyPage)
                    }
                }
                true
            }
            selectedItemId = R.id.home
        }
    }
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
