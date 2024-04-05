package com.alpharez.t1base.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
class Medicine {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "medicineName")
    var medicineName: String = ""
    @ColumnInfo(name = "dosageForm")
    var dosageForm: String = ""          // 1 Tablet, 1 Tbsp, 1 Pod
    @ColumnInfo(name = "frequency")
    var frequency: Int = 0              // 1=Daily, 2=Every 3 Days, 3=Weekly
    var lastTaken: Date = Date(0)       // Could be a Long and do Converting elsewhere
//    val route: String
//    val unit: String
//    val amount: Int
//    val quantity: Int
//    val takeNext: Int
//    val inventory: Int

    constructor()

    constructor(medicineName: String, dosageForm: String, frequency: Int, lastTaken: Date) {
        this.medicineName = medicineName
        this.dosageForm = dosageForm
        this.frequency = frequency
        this.lastTaken = lastTaken
    }
}