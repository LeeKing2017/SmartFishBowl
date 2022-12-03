package com.example.smartfishbowl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.smartfishbowl.database.BowlData
import com.example.smartfishbowl.database.BowlDatabase

class BowlViewModel(app: Application) : AndroidViewModel(app) {
    var allBowls : MutableLiveData<List<BowlData>> = MutableLiveData()
    fun getAllIdsObservers(): MutableLiveData<List<BowlData>> {
        return allBowls
    }

    fun getAllIds(){
        val bowlDao = BowlDatabase.getDatabase((getApplication())).bowlDao()
        val list = bowlDao.getAllId()

        allBowls.postValue(list)
    }

    fun insertBowl(entity: BowlData){
        val bowlDao = BowlDatabase.getDatabase((getApplication())).bowlDao()
        bowlDao.insertBowl(entity)
        getAllIds()
    }

    fun deleteBowl(entity: BowlData){
        val bowlDao = BowlDatabase.getDatabase((getApplication())).bowlDao()
        bowlDao.deleteBowl(entity)
        getAllIds()
    }

    fun deleteAll(){
        val bowlDao = BowlDatabase.getDatabase((getApplication())).bowlDao()
        bowlDao.deleteAll()
    }
}