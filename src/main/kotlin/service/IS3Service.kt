package com.example.service

interface IS3Service {
    fun uploadImage(bucketName: String, fileName: String, contentType: String, image: ByteArray): Boolean
}
