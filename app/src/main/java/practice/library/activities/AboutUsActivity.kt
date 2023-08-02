package practice.library.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import practice.library.R
import practice.library.databinding.ActivityAboutUsBinding
import practice.library.databinding.ActivityUpdateUserBinding
import practice.library.retrofit.Common

class AboutUsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.back.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}