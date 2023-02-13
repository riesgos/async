package org.n.riesgos.asyncwrapper.filestorage

import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset

@RestController
@RequestMapping("uploads")
class TestUploadController(val filestorageConfig: FilestorageConfig) {
    @GetMapping("/test")
    fun uploadExample(): String {

        val name = "examplefile.txt"

        val content = """
            Some content
            some other line
        """.trimIndent().toByteArray()

        val mimeType = "plain/text"


        val fileStorage = FileStorage(filestorageConfig.endpoint, filestorageConfig.user, filestorageConfig.password)
        fileStorage.upload(filestorageConfig.bucketName, name, content, mimeType)
        return filestorageConfig.access + name
    }

    @GetMapping("/test-content")
    private fun fetchContent(): String {
        val name = "examplefile.txt"
        val link = filestorageConfig.access + name
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(link)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        val body = response.body()
        return String(body, Charset.defaultCharset())
    }
}