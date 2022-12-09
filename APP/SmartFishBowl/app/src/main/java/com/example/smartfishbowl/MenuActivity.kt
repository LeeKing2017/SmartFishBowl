package com.example.smartfishbowl

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.smartfishbowl.api.*
import com.example.smartfishbowl.databinding.AlertdialogEdittextDecimalBinding
import com.example.smartfishbowl.databinding.AlertdialogEdittextNumberBinding
import com.example.smartfishbowl.sharedpreferences.PreferencesUtil
import com.example.smartfishbowl.viewmodel.BowlViewModel
import com.google.android.material.navigation.NavigationView
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("SetTextI18n")
class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var viewModel: BowlViewModel
    private val apis = APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        drawerLayout = findViewById(R.id.activity_drawer)
        navigationView = findViewById(R.id.main_navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        val pref = PreferencesUtil(applicationContext)
        UserApiClient.instance.me { user, _ ->
            user?.kakaoAccount?.email?.let { pref.setString("Email", it) }
            user?.kakaoAccount?.email?.let { Log.d("카카오이메일이름", it) }
        }
        bowl.text = "현재 어항: " + pref.getString("CurrentDevice", "0")
        first_time.text = "첫번째 시간: " + pref.getInt("FirstHour", 0) + "시 " + pref.getInt("FirstMinute", 0) + "분, " + pref.getInt("FirstTotal", 0) + "회"
        second_time.text = "두번째 시간: "+pref.getInt("SecondHour", 0) + "시 " + pref.getInt("SecondMinute", 0) + "분, "+ pref.getInt("SecondTotal", 0) + "회"
        third_time.text = "세번째 시간: "+pref.getInt("ThirdHour", 0) + "시 " + pref.getInt("ThirdMinute", 0) + "분, "+ pref.getInt("ThirdTotal", 0) + "회"
        tmp.text = "희망 온도: ${pref.getString("tmp", "0")} ℃"
        hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
        ph.text = "희망 PH: ${pref.getString("ph", "0")}"
        drt.text = "희망 탁도: ${pref.getString("drt", "0")}"
        Log.d("CURRENTDEVICE", pref.getString("CurrentDevice", "0"))
        val curD = CurrentDevice(pref.getString("CurrentDevice", "0").toLong())
        apis.getValues("Bearer " + pref.getString("JWT", "error"), curD).enqueue(object : Callback<Getting>{

            override fun onResponse(call: Call<Getting>, response: Response<Getting>) {
                if(response.body()!=null){
                    tmp_cur.text = "현재 온도: "+response.body()!!.measuredTemperature+"℃"
                    hgt_cur.text = "현재 수위: " + response.body()!!.measuredWaterLevel+"CM"
                    ph_cur.text = "현재 PH: " + response.body()!!.measuredPh
                    drt_cur.text = "현재 탁도: " + response.body()!!.measuredTurbidity
                }
            }
            override fun onFailure(call: Call<Getting>, t: Throwable) {
                Log.d("Get_Values", t.message.toString())
                tmp_cur.text = "현재 온도: _℃"
                hgt_cur.text = "현재 수위: _CM"
                ph_cur.text = "현재 PH: _"
                drt_cur.text = "현재 탁도: _"
            }
        })
        change_tmp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderItem = AlertdialogEdittextDecimalBinding.inflate(layoutInflater)
            val edittext = builderItem.editText
            with(builder){
                setTitle("적정 온도 변경")
                setMessage("적정 온도를 입력하세요.")
                setView(builderItem.root)
                setPositiveButton("확인"){ _: DialogInterface, _: Int ->
                    if(edittext.text!=null) {
                        tmp.text = "희망 온도: ${edittext.text} ℃"
                        pref.setString("tmp", edittext.text.toString())
                        settingValue()
                    }
                }
                setNegativeButton("취소"){ _: DialogInterface, _: Int ->
                    Toast.makeText(context, "적정 온도 변경 취소", Toast.LENGTH_SHORT).show()
                }
                show()
            }
        }
        change_hgt.setOnClickListener {
            showPopup(change_hgt)
        }
        change_ph.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderItem = AlertdialogEdittextDecimalBinding.inflate(layoutInflater)
            val edittext = builderItem.editText
            with(builder){
                setTitle("적정 PH 변경")
                setMessage("적정 PH를 입력하세요.")
                setView(builderItem.root)
                setPositiveButton("확인"){ _: DialogInterface, _: Int ->
                    if(edittext.text!=null) {
                        ph.text = "희망 PH: ${edittext.text}"
                        pref.setString("ph", edittext.text.toString())
                        settingValue()
                    }
                }
                setNegativeButton("취소"){ _: DialogInterface, _: Int ->
                    Toast.makeText(context, "적정 PH 변경 취소", Toast.LENGTH_SHORT).show()
                }
                show()
            }
        }
        change_drt.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderItem = AlertdialogEdittextDecimalBinding.inflate(layoutInflater)
            val edittext = builderItem.editText
            with(builder){
                setTitle("적정 탁도 변경")
                setMessage("적정 탁도를 입력하세요.")
                setView(builderItem.root)
                setPositiveButton("확인"){ _: DialogInterface, _: Int ->
                    if(edittext.text!=null) {
                        drt.text = "희망 탁도: ${edittext.text}"
                        pref.setString("drt", edittext.text.toString())
                        settingValue()
                    }
                }
                setNegativeButton("취소"){ _: DialogInterface, _: Int ->
                    Toast.makeText(context, "적정 탁도 변경 취소", Toast.LENGTH_SHORT).show()
                }
                show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = PreferencesUtil(applicationContext)
        when(item.itemId){
            android.R.id.home -> {
                activity_drawer.openDrawer(GravityCompat.START)
                drawer_header.header_account.text = pref.getString("Email", "No Email")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val pref = PreferencesUtil(applicationContext)
        when(item.itemId){
            R.id.item1 -> {
                val intent = Intent(this, ChangeActivity::class.java)
                startActivity(intent)
            }
            R.id.item2 -> {
                val intent = Intent(this, TimeActivity::class.java)
                startActivity(intent)
            }
            R.id.item3 -> {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
            R.id.item4 -> {
                viewModel = ViewModelProvider(this)[BowlViewModel::class.java]
                viewModel.deleteAll()
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        apis.signOut("Bearer " + pref.getString("JWT", "error")).enqueue(object : Callback<String>{
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Log.d("회원탈퇴", "성공")
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("회원탈퇴", "실패")
                            }

                        })
                        pref.setString("JWT", "")
                        pref.setString("CurrentDevice", "0")
                        pref.setString("oAuthToken", "")
                        pref.setString("FirebaseToken", "")
                        Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
            }
            R.id.item5 -> {
                val setting = Setting(
                    0.0,
                    0,
                    0.0,
                    0.0,
                    pref.getString("CurrentDevice", "0").toLong()
                )
                apis.settingValue("Bearer " + pref.getString("JWT", "error"), setting).enqueue(object : Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("Response", "SUCCESS")
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("Response", "FAIL")
                    }
                })
                Toast.makeText(this@MenuActivity, "푸시 알림 전체 해제", Toast.LENGTH_SHORT).show()
                pref.setString("tmp", "0")
                pref.setString("hgt", "0")
                pref.setString("ph", "0")
                pref.setString("drt", "0")
                tmp.text = "희망 온도: 0℃"
                hgt.text = "희망 수위: 0CM"
                ph.text = "희망 PH: 0"
                drt.text = "희망 탁도: 0"
                activity_drawer.closeDrawers()
            }
            R.id.item6 -> {
                val foodSetting = FoodSetting(
                    pref.getString("CurrentDevice", "0").toLong(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
                apis.setTime("Bearer " + pref.getString("JWT", "error"), foodSetting).enqueue(object : Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("Response", "SUCCESS")
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("Response", "FAIL")
                    }

                })
                first_time.text = "첫번째 시간: 0시 0분, 0회"
                second_time.text = "두번째 시간: 0시 0분, 0회"
                third_time.text = "세번째 시간: 0시 0분, 0회"
                pref.setInt("FirstHour", 0)
                pref.setInt("SecondHour", 0)
                pref.setInt("ThirdHour", 0)
                pref.setInt("FirstMinute", 0)
                pref.setInt("SecondMinute", 0)
                pref.setInt("ThirdMinute", 0)
                pref.setInt("FirstTotal", 0)
                pref.setInt("SecondTotal", 0)
                pref.setInt("ThirdTotal", 0)
                Toast.makeText(this@MenuActivity, "자동 먹이 급여 해제", Toast.LENGTH_SHORT).show()
                activity_drawer.closeDrawers()
            }

        }
        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean{
        val pref = PreferencesUtil(applicationContext)
        when(item?.itemId){
            R.id.level1 -> {
                pref.setString("hgt", "1")
                hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
                settingValue()
            }
            R.id.level2 -> {
                pref.setString("hgt", "2")
                hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
                settingValue()
            }
            R.id.level3 -> {
                pref.setString("hgt", "3")
                hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
                settingValue()
            }
            R.id.level4 -> {
                pref.setString("hgt", "4")
                hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
                settingValue()
            }
        }

        return item != null
    }
    override fun onResume() {
        super.onResume()
        val pref = PreferencesUtil(applicationContext)
        bowl.text = ("현재 어항: " + pref.getString("CurrentDevice", "0"))
        first_time.text = "첫번째 시간: " + pref.getInt("FirstHour", 0) + "시 " + pref.getInt("FirstMinute", 0) + "분, " + pref.getInt("FirstTotal", 0) + "회"
        second_time.text = "두번째 시간: "+pref.getInt("SecondHour", 0) + "시 " + pref.getInt("SecondMinute", 0) + "분, "+ pref.getInt("SecondTotal", 0) + "회"
        third_time.text = "세번째 시간: "+pref.getInt("ThirdHour", 0) + "시 " + pref.getInt("ThirdMinute", 0) + "분, "+ pref.getInt("ThirdTotal", 0) + "회"
        tmp.text = "희망 온도: ${pref.getString("tmp", "0")} ℃"
        hgt.text = "희망 수위: ${pref.getString("hgt", "0")} CM"
        ph.text = "희망 PH: ${pref.getString("ph", "0")}"
        drt.text = "희망 탁도: ${pref.getString("drt", "0")}"
        val curD = CurrentDevice(pref.getString("CurrentDevice", "0").toLong())
        apis.getValues("Bearer " + pref.getString("JWT", "error"), curD).enqueue(object : Callback<Getting>{
            override fun onResponse(call: Call<Getting>, response: Response<Getting>) {
                if(response.body()!=null){
                    tmp_cur.text = "현재 온도: "+response.body()!!.measuredTemperature+"℃"
                    hgt_cur.text = "현재 수위: " + response.body()!!.measuredWaterLevel+"CM"
                    ph_cur.text = "현재 PH: " + response.body()!!.measuredPh
                    drt_cur.text = "현재 탁도: " + response.body()!!.measuredTurbidity
                }
            }
            override fun onFailure(call: Call<Getting>, t: Throwable) {
                Log.d("Get_Values", t.message.toString())
                tmp_cur.text = "현재 온도: _℃"
                hgt_cur.text = "현재 수위: _CM"
                ph_cur.text = "현재 PH: _"
                drt_cur.text = "현재 탁도: _"
            }
        })
    }

    override fun onBackPressed() { //뒤로가기 처리
        if(activity_drawer.isDrawerOpen(GravityCompat.START)){
            activity_drawer.closeDrawers()
        } else{
            super.onBackPressed()
        }
    }
    private fun settingValue(){
        val pref = PreferencesUtil(applicationContext)
        val setting = Setting(
            pref.getString("tmp", "0").toDouble(),
            pref.getString("hgt", "0").toInt(),
            pref.getString("ph", "0").toDouble(),
            pref.getString("drt", "0").toDouble(),
            pref.getString("CurrentDevice", "0").toLong()
        )
        apis.settingValue("Bearer " + pref.getString("JWT", "error"), setting).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Setting_Response", response.body().toString())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("sendSetting", "FAIL")
            }
        })
    }
    private fun showPopup(v: View){
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(R.menu.waterlevel, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }
}