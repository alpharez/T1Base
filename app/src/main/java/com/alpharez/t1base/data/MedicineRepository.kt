package com.alpharez.t1base.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class MedicineRepository(private val medicineDao: MedicineDao) {
    val allMedicines: LiveData<List<Medicine>> = medicineDao.getMedicines()
    val searchResults = MutableLiveData<List<Medicine>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertMedicine(newMedicine: Medicine) {
        coroutineScope.launch(Dispatchers.IO) {
            medicineDao.insert(newMedicine)
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        coroutineScope.launch(Dispatchers.IO) {
            medicineDao.delete(medicine)
        }
    }

    fun takeMedicine(medicine: Medicine) {
        coroutineScope.launch(Dispatchers.IO) {
            medicineDao.update(medicine)
        }
    }

    fun findMedicine(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    private fun asyncFind(name: String): Deferred<List<Medicine>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async medicineDao.findMedicine(name)
        }
}