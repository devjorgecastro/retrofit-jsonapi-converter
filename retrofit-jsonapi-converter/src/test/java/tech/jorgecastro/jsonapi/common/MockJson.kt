package tech.jorgecastro.jsonapi.common

import java.io.File

class MockJson {

    private val pathJson = "/src/main/java/tech/jorgecastro/jsonapi/examples/json/{{fileName}}.json"

    /**
     * @param fileName name of json without extension
     * @return String with the json representation
     */
    fun getMockJson(fileName: String): String {
        val path = System.getProperty("user.dir")
        val fileJson = File(path + pathJson.replace("{{fileName}}", fileName))

        return fileJson.readText()
    }
}
