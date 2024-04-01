package com.alpharez.t1base.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class Medicine {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "medicineName")
    var medicineName: String = ""
//    val dosageForm: String
//    val route: String
//    val unit: String
//    val amount: Int
//    val quantity: Int
//    val frequency: Int
//    val takeNext: Int
//    val inventory: Int

    constructor()

    constructor(medicineName: String) {
        this.medicineName = medicineName
    }
}