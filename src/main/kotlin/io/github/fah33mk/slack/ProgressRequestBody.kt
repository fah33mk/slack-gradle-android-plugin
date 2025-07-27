package io.github.fah33mk.slack

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File

class ProgressRequestBody(
    private val file: File,
    private val contentType: MediaType?,
    private val progressCallback: (percent: Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInput = file.inputStream().buffered()
        var uploaded = 0L
        val total = contentLength()
        var lastProgress = -1

        fileInput.use {
            var read: Int
            while (fileInput.read(buffer).also { read = it } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)

                val progress = (100 * uploaded / total).toInt()
                if (progress != lastProgress) {
                    progressCallback(progress)
                    lastProgress = progress
                }
            }
        }
    }
}
