package com.example.smartfishbowl

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "48fd6279a270d186c4afb28950eec450")
    }
}