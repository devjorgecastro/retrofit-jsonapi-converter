package tech.jorgecastro.jsonapi.common

import java.io.File

class ArticlesMockJson {

    private val pathJson = "/src/main/java/tech/jorgecastro/jsonapi/examples/json/articles-{{number}}.json"

    fun getMockJson(number: Int = 100): String {
        val path = System.getProperty("user.dir")
        val fileJson = File(path + pathJson.replace("{{number}}", "$number"))

        return fileJson.readText()
    }
}
