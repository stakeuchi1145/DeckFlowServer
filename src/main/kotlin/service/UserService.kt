package com.example.service

import com.example.db.Users
import com.example.repository.IUserRepository
import com.example.util.PasswordHasher
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class UserService: IUserService {
    private val userRepository: IUserRepository by inject(IUserRepository::class.java)
    private val firebaseService: FirebaseService by inject(FirebaseService::class.java)

    override fun login(email: String, password: String): String? {
        userRepository.getUserByEmail(email)?.let { user ->
            if (!PasswordHasher.verify(password, user.passwordHash)) {
                return null
            }

            return firebaseService.createCustomToken(user.authId)
        }

        return null
    }

    override fun getUser(uid: String): Users? {
        return userRepository.getUser(uid)
    }
}
