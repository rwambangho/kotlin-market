package com.example.carrotmarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.carrotmarket.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity(), ConfirmDialogInterface {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val inf = intent.getSerializableExtra("info") as? HashMap<String, Any>

        if (inf != null) {
            binding.detailprice.text = inf["price"] as? String + "원"
            binding.detailtitle.text = inf["title"] as? String

            val sell = inf["value"] as? String
            if (sell == "0") {
                binding.detailstatus.text = "판매중"
            } else {
                binding.detailstatus.text = "판매완료"
            }

            val uid = inf["uid"] as? String
            val id = inf["id"] as? String

            binding.detailcontent.text = inf["content"] as? String

            Glide.with(binding.detailimg)
                .load(inf["uri"] as? String)
                .into(binding.detailimg)

            // 게시글 수정 버튼
            binding.detailEditButton.setOnClickListener {
                val intent = Intent(this, EditArticleActivity::class.java)
                intent.putExtra("title", inf["title"] as? String)
                intent.putExtra("price", inf["price"] as? String)
                intent.putExtra("sell", sell)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }

            // 채팅 버튼
            binding.detailChatButton.setOnClickListener {
                val dialog = ConfirmDialog(
                    this, uid.toString(), id.toString()
                )
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "ConfirmDialog")
            }
        } else {
            // "info" 키에 대한 Serializable 데이터가 올바르게 전달되지 않은 경우에 대한 처리
            // 예를 들어, 적절한 오류 처리 또는 로깅을 수행할 수 있습니다.
            // 여기서는 간단하게 로그 메시지를 출력합니다.
            // 적절한 오류 처리 방식에 따라 수정하세요.
            println("Error: Intent data with key 'info' is null.")
            finish() // 액티비티 종료 또는 다른 처리 방식을 선택하세요.
        }
    }

    override fun onYesButtonClick(id: Int) {
        super.onDestroy()
    }
}