package com.baltsarak.cryptopricealert.data.database

import android.util.Log
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class RemoteDatabaseService {
    private val remoteDatabase = FirebaseFirestore.getInstance()

    fun addToRemoteDatabase(
        fromSymbol: String,
        targetPrice: Double?,
        higherThenCurrent: Boolean,
        position: Int
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User not authenticated")
            return
        }

        val newItem = hashMapOf(
            "fromSymbol" to fromSymbol,
            "targetPrice" to targetPrice,
            "higherThenCurrent" to higherThenCurrent,
            "position" to position
        )

        remoteDatabase.collection("users").document(userId)
            .collection("watchList")
            .add(newItem)
            .addOnSuccessListener { documentReference ->
                Log.i("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    fun sendWatchListToRemoteDatabase(list: List<TargetPrice>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User not authenticated")
            return
        }

        val batch = remoteDatabase.batch()
        val userWatchListRef = remoteDatabase
            .collection("users")
            .document(userId)
            .collection("watchList")
        list.forEach {
            val newItemRef = userWatchListRef.document()
            batch.set(
                newItemRef, hashMapOf(
                    "fromSymbol" to it.fromSymbol,
                    "targetPrice" to it.targetPrice,
                    "higherThenCurrent" to it.higherThenCurrent,
                    "position" to it.position
                )
            )
        }

        batch.commit()
            .addOnSuccessListener {
                Log.i("Firestore", "Batch write succeeded, all items added.")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error performing batch write", e)
            }
    }

    suspend fun getWatchListFromRemoteDatabase(): List<WatchListCoinDbModel> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User not authenticated")
            return emptyList()
        }

        return try {
            remoteDatabase.collection("users").document(userId)
                .collection("watchList")
                .get()
                .await()
                .documents
                .map { document ->
                    WatchListCoinDbModel(
                        id = 0,
                        fromSymbol = document.getString("fromSymbol") ?: "",
                        targetPrice = document.getDouble("targetPrice"),
                        higherThenCurrent = document.getBoolean("higherThenCurrent"),
                        position = document.getLong("position")?.toInt() ?: 0
                    )
                }
        } catch (e: Exception) {
            Log.w("Firestore", "Error getting documents: ", e)
            emptyList()
        }
    }

    fun deleteTargetPriceFromRemoteDatabase(fromSymbol: String, price: Double) {
        remoteDatabase.collection("watchList")
            .whereEqualTo("fromSymbol", fromSymbol)
            .whereEqualTo("targetPrice", price)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    remoteDatabase.collection("watchList").document(document.id)
                        .update(
                            mapOf(
                                "targetPrice" to null,
                                "higherThenCurrent" to null
                            )
                        )
                        .addOnSuccessListener {
                            Log.i("Firestore", "Document successfully updated")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error updating document", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

    fun deleteCoinFromRemoteDatabase(fSym: String) {
        remoteDatabase.collection("watch_list")
            .whereEqualTo("fromSymbol", fSym)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.i("Firestore", "No documents found with fromSymbol = $fSym")
                } else {
                    deleteDocuments(documents)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun deleteDocuments(documents: QuerySnapshot) {
        for (document in documents) {
            remoteDatabase.collection("watch_list").document(document.id)
                .delete()
                .addOnSuccessListener {
                    Log.i("Firestore", "Document successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting document", e)
                }
        }
    }

    suspend fun deleteAllFromRemoteDatabase() {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Log.e("Firestore", "User not authenticated")
                return
            }
            val watchListRef = remoteDatabase
                .collection("users")
                .document(userId)
                .collection("watchList")
            val snapshot = watchListRef.get().await()

            val batch = remoteDatabase.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()
            Log.i("Firestore", "All watchList items deleted successfully")
        } catch (e: Exception) {
            Log.w("Firestore", "Error deleting watchList", e)
        }
    }
}