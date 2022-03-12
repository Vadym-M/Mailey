package com.devx.mailey.data.firebase

import com.devx.mailey.data.model.User
import com.devx.mailey.presentation.auth.AuthState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseSource: FirebaseService {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val database: DatabaseReference by lazy { Firebase.database.reference }

    override suspend fun register(fullName: String, email: String, password: String) : Flow<AuthState<AuthResult>> = flow {

            emit(AuthState.Loading(null))
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
                result.user?.uid.let { id ->
                    val user = User(fullName = fullName, email = email, id = id.toString(), null)
                    database.child("users").child(id.toString()).setValue(user)
                }.await()
                emit(AuthState.Success(result))
                }catch (e :Exception){
                emit(AuthState.Error(e.message))
                }


        }
    }

