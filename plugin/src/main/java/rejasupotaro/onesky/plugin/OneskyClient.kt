package rejasupotaro.onesky.plugin

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import org.apache.commons.codec.binary.Hex
import java.io.File
import java.security.MessageDigest

class OneskyClient(val apiKey: String, val apiSecret: String, val projectId: Int) {
    private val endpoint = "https://platform.api.onesky.io"
    private val version = 1
    private val urlPrefix
        get() = "$endpoint/$version"

    fun upload(translationFile: File, handler: (Result<String, FuelError>) -> Unit) {
        val params = authParams()
        params.add("file_format" to "ANDROID_XML")

        val (request, response, result) = Fuel.upload("$urlPrefix/projects/$projectId/files", parameters = params)
                .source { _, _ -> translationFile }
                .name { "file" }
                .progress { _, _ -> }
                .responseString()
        handler.invoke(result)
    }

    fun download(locale: String, handler: (Result<String, FuelError>) -> Unit) {
        val params = authParams()
        params.add("source_file_name" to "strings.xml")
        params.add("locale" to locale)

        println("$urlPrefix/1/projects/$projectId/translations")
        val (request, response, result) = Fuel.get("$urlPrefix/projects/$projectId/translations", parameters = params).responseString()
        handler.invoke(result)
    }

    fun authParams(): MutableList<Pair<String, String>> {
        val md = MessageDigest.getInstance("MD5")
        val timestamp = (System.currentTimeMillis() / 1000L).toString()
        val devHash = Hex.encodeHexString(md.digest((timestamp + apiSecret).toByteArray()))

        return mutableListOf("api_key" to apiKey,
                "dev_hash" to devHash,
                "timestamp" to timestamp)
    }
}

