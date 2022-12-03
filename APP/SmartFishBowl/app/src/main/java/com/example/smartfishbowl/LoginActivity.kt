package com.example.smartfishbowl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.smartfishbowl.api.APIS
import com.example.smartfishbowl.api.Token
import com.example.smartfishbowl.databinding.ActivityLoginBinding
import com.example.smartfishbowl.sharedpreferences.PreferencesUtil
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val apis = APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferencesUtil(applicationContext)
        setContentView(binding.root)
        initFirebase()
        binding.kakaoLogin.setOnClickListener {
            val intent = Intent(this, ChangeActivity::class.java)
            lifecycleScope.launch{
                try {
                    val oAuthToken = UserApiClient.loginWithKakao(this@LoginActivity)
                    Log.d("LoginActivity", "beanbean > $oAuthToken")
                    Log.d("accessToken", oAuthToken.accessToken)
                    prefs.setString("oAuthToken", oAuthToken.accessToken)
                    val tok = Token(prefs.getString("oAuthToken", "error"), prefs.getString("FirebaseToken", "error"))
                    apis.sendToken(tok).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.body().toString().isNotEmpty()){
                                Log.d("JWT", response.body().toString())
                                prefs.setString("JWT", response.body().toString())
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            // 실패
                            Log.d("log", t.message.toString())
                            Log.d("sendToken", "Fail")
                        }
                    })
                    startActivity(intent)
                }catch (error: Throwable){
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled){
                        Log.d("LoginActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.d("LoginActivity", "인증 에러 발생", error)
                    }
                }
            }
        }
    }

    private fun initFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val prefs = PreferencesUtil(applicationContext)
            if (task.isSuccessful) {
                Log.d("FirebaseToken", task.result)
                binding.tokenValue.setText(task.result)
                prefs.setString("FirebaseToken", task.result)
            }else{
                Toast.makeText(applicationContext, "FirebaseToken is unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }
}