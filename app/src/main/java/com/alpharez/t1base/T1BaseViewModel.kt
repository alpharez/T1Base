package com.alpharez.t1base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alpharez.t1base.data.Medicine
import com.alpharez.t1base.data.MedicineDatabase
import com.alpharez.t1base.data.MedicineRepository

class T1BaseViewModel(application: Application): ViewModel() {

    val allMedicines: LiveData<List<Medicine>>
    private val repository: MedicineRepository
    val searchResults: MutableLiveData<List<Medicine>>

    init {
        val medicineDb = MedicineDatabase.getInstance(application)
        val medicineDao = medicineDb.medicineDao()
        repository = MedicineRepository(medicineDao)
        searchResults = repository.searchResults
        allMedicines = repository.allMedicines
    }

    fun insertMedicine(medicine: Medicine) {
        repository.insertMedicine(medicine)
    }

    fun deleteMedicine(medicine: Medicine) {
        repository.deleteMedicine(medicine)
    }

    fun findMedicine(name: String) {
        repository.findMedicine(name)
    }
}