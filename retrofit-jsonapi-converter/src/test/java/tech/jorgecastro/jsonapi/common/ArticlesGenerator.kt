package tech.jorgecastro.jsonapi.common

import java.lang.StringBuilder

class ArticlesGenerator {
    fun generateArticlesJson(limit: Int = 1): String {

        val staticJson = "{\n" +
                "  \"data\": [\n" +
                "{{jsonGenerate}}" +
                "  ],\n" +
                "  \"included\": [\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"41\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 30,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"42\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 40,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"43\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"Martin C. Robert\",\n" +
                "        \"age\": 50,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"44\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 60,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"45\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 70,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}"


        val sample = "{\n" +
                "      \"type\": \"articles\",\n" +
                "      \"id\": \"{{tempId}}\",\n" +
                "      \"attributes\": {\n" +
                "        \"title\": \"JSON:API paints my bikeshed! - ID={{tempId}}\",\n" +
                "        \"body\": \"The shortest article. Ever.\",\n" +
                "        \"created\": \"2015-05-22T14:56:29.000Z\",\n" +
                "        \"updated\": \"2015-05-22T14:56:28.000Z\"\n" +
                "      },\n" +
                "      \"relationships\": {\n" +
                "        \"author\": {\n" +
                "          \"data\": [\n" +
                "            {\n" +
                "              \"id\": \"41\",\n" +
                "              \"type\": \"people\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": \"42\",\n" +
                "              \"type\": \"people\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": \"43\",\n" +
                "              \"type\": \"people\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": \"44\",\n" +
                "              \"type\": \"people\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": \"45\",\n" +
                "              \"type\": \"people\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }"

        val stringBuilder = StringBuilder()
        for (i in 1..limit) {
            stringBuilder.append(sample.replace("{{tempId}}", "$i"))

            if (i < limit) stringBuilder.append(",")
        }

        return staticJson.replace("{{jsonGenerate}}", stringBuilder.toString())
    }
}