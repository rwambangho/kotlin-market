package com.example.carrotmarket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.carrotmarket.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val adapter = ArticleAdapter("all")
        binding.articleRecyclerView.adapter = adapter

        binding.switch4.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.switch4.text = "판매 완료"
                val adapter = ArticleAdapter("complete")
                binding.articleRecyclerView.adapter = adapter
            }else{
                binding.switch4.text = "판매중"
                val adapter = ArticleAdapter("selling")
                binding.articleRecyclerView.adapter = adapter
            }

        }


        binding.addFloatingButton.setOnClickListener {
            context?.let {
                if (auth.currentUser != null) {
                    val intent = Intent(it, AddArticleActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용하실 수 있습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}