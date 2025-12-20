package com.example.service

import net.coobird.thumbnailator.Thumbnails
import org.koin.java.KoinJavaComponent.inject
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.getValue

class S3Service : IS3Service {
    val s3Client: S3Client by inject(S3Client::class.java)
    data class CompressedImage(
        val bytes: ByteArray,
        val contentType: String,
        val ext: String
    )

    override fun uploadImage(
        bucketName: String,
        fileName: String,
        contentType: String,
        image: ByteArray
    ): Boolean {
        if (image.size > 10 * 1024 * 1024) { // 例：10MB超は拒否
            return false
        }

        // 2) 200KB超なら圧縮
        val compressed = if (image.size > 200 * 1024) {
            compressImageToUnder(image, maxBytes = 200 * 1024)
        } else {
            // すでに200KB以下でも、Content-Typeを整えるならここで統一変換してもOK
            CompressedImage(image, contentType, "bin")
        }

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(compressed.contentType)
                    .build(),
                RequestBody.fromBytes(compressed.bytes)
            )

            return true
        } catch (e: Exception) {
            print(e.message)
            return false
        }
    }

    private fun compressImageToUnder(
        input: ByteArray,
        maxBytes: Int = 200 * 1024,
        maxWidth: Int = 1280,
        minQuality: Float = 0.40f
    ): CompressedImage {
        // 画像として読めるかチェック（読めないなら例外）
        val buffered = ImageIO.read(ByteArrayInputStream(input))
            ?: throw IllegalArgumentException("Unsupported image format")

        // まず長辺を抑えた状態を作る
        var width = buffered.width
        var height = buffered.height
        val longSide = maxOf(width, height)
        if (longSide > maxWidth) {
            val ratio = maxWidth.toDouble() / longSide.toDouble()
            width = (width * ratio).toInt().coerceAtLeast(1)
            height = (height * ratio).toInt().coerceAtLeast(1)
        }

        fun encodeJpeg(w: Int, h: Int, quality: Float): ByteArray {
            val out = ByteArrayOutputStream()
            Thumbnails.of(buffered)
                .size(w, h)
                .outputFormat("jpg")
                .outputQuality(quality.toDouble())
                .toOutputStream(out)
            return out.toByteArray()
        }

        // quality を下げていく
        var q = 0.92f
        while (q >= minQuality) {
            val bytes = encodeJpeg(width, height, q)
            if (bytes.size <= maxBytes) {
                return CompressedImage(bytes, "image/jpeg", "jpg")
            }
            q -= 0.07f
        }

        // まだ大きければ解像度も下げて再トライ
        var w = width
        var h = height
        repeat(5) {
            w = (w * 0.85).toInt().coerceAtLeast(320)
            h = (h * 0.85).toInt().coerceAtLeast(320)
            var qq = 0.90f
            while (qq >= minQuality) {
                val bytes = encodeJpeg(w, h, qq)
                if (bytes.size <= maxBytes) {
                    return CompressedImage(bytes, "image/jpeg", "jpg")
                }
                qq -= 0.07f
            }
        }

        // 最後の手段（ギリギリまで落としたやつ）
        val last = encodeJpeg(w, h, minQuality)
        return CompressedImage(last, "image/jpeg", "jpg")
    }

    override fun deleteImage(bucketName: String, fileName: String): Boolean {
        if (bucketName.isEmpty() || fileName.isEmpty()) {
            return false
        }

        s3Client.deleteObject(
            software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build()
        )

        return true
    }
}