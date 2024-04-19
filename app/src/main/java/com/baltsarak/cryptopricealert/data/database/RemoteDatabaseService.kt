package com.baltsarak.cryptopricealert.data.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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

        remoteDatabase.collection("users").document(userId).collection("watchList")
            .add(newItem)
            .addOnSuccessListener { documentReference ->
                Log.i("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    fun getWatchListFromRemoteDatabase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User not authenticated")
            return
        }

        remoteDatabase.collection("users").document(userId).collection("watchList")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i("Firestore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
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
}