package tech.jorgecastro.jsonapi.data

class ArticleMockData {

    /*val ARTICLE_DATA_LIST = """
        {"data":[{"type":"articles","id":"1","attributes":{"title":"JSON:API paints my bikeshed!","body":"The shortest article. Ever.","created":"2015-05-22T14:56:29.000Z","updated":"2015-05-22T14:56:28.000Z"},"relationships":{"author":{"data":[{"id":"42","type":"people"},{"id":"45","type":"people"}]}}}],"included":[{"type":"people","id":"42","attributes":{"name":"John","age":80,"gender":"male"}},{"type":"people","id":"45","attributes":{"name":"Martin C. Robert","age":65,"gender":"male"}}]}
    """.trimIndent()*/

    val ARTICLE_DATA_ITEM_WITH_MULTIPLE_RELATIONSHIP = """
        {
          "data": {
            "type": "articles",
            "id": "1",
            "attributes": {
                "title": "JSON:API paints my bikeshed!",
                "body": "The shortest article. Ever.",
                "created": "2015-05-22T14:56:29.000Z",
                "updated": "2015-05-22T14:56:28.000Z"
            },
            "relationships": {
                "author": {
                    "data": [
                        {
                            "id": "42",
                            "type": "people"
                        },
                        {
                            "id": "45",
                            "type": "people"
                        }
                    ]
                }
            }
        },
        "included": [
            {
                "type": "people",
                "id": "42",
                "attributes": {
                    "name": "John",
                    "age": 80,
                    "gender": "male"
                }
            },
            {
                "type": "people",
                "id": "45",
                "attributes": {
                    "name": "Martin C. Robert",
                    "age": 65,
                    "gender": "male"
                }
            }
        ]
    }
    """.trimIndent()


    val ARTICLE_DATA_LIST = """
        {
          "data": [
            {
              "type": "articles",
              "id": "1",
              "attributes": {
                "title": "JSON:API paints my bikeshed!",
                "body": "The shortest article. Ever.",
                "created": "2015-05-22T14:56:29.000Z",
                "updated": "2015-05-22T14:56:28.000Z"
              },
              "relationships": {
                "author": {
                  "data": [
                    {
                        "id": "42",
                        "type": "people"
                    },
                    {
                        "id": "45",
                        "type": "people"
                    }
                  ]
                }
              }
            }
          ],
          "included": [
            {
              "type": "people",
              "id": "42",
              "attributes": {
                "name": "John",
                "age": 80,
                "gender": "male"
              }
            },
            {
              "type": "people",
              "id": "45",
              "attributes": {
                "name": "Martin C. Robert",
                "age": 65,
                "gender": "male"
              }
            }
          ]
        }
    """.trimIndent()
}