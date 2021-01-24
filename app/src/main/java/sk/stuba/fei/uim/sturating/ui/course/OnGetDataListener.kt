package sk.stuba.fei.uim.sturating.ui.course

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface OnGetDataListener {
    fun onSuccess(dataSnapshot: DataSnapshot)
    fun onStart()
    fun onFailure(error: DatabaseError)
}