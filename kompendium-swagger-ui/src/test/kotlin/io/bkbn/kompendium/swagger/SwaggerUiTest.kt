package io.bkbn.kompendium.swagger

import io.bkbn.kompendium.swagger.TestHelpers.TEST_SWAGGER_UI_INDEX
import io.bkbn.kompendium.swagger.TestHelpers.TEST_SWAGGER_UI_INIT_JS
import io.bkbn.kompendium.swagger.TestHelpers.TEST_SWAGGER_UI_ROOT
import io.bkbn.kompendium.swagger.TestHelpers.compareRedirect
import io.bkbn.kompendium.swagger.TestHelpers.compareResource
import io.bkbn.kompendium.swagger.TestHelpers.withSwaggerApplication
import io.kotest.core.spec.style.DescribeSpec

class SwaggerUiTest: DescribeSpec ({

    describe("Swagger UI resources") {

        it ("Redirects /swagger-ui -> index.html") {
            withSwaggerApplication {
                compareRedirect(TEST_SWAGGER_UI_ROOT, TEST_SWAGGER_UI_INDEX)
            }
        }

        it ("Can return original: index.html") {
            withSwaggerApplication {
                compareResource(TEST_SWAGGER_UI_INDEX, listOf(
                    "<title>Swagger UI</title>",
                    "<div id=\"swagger-ui\"></div>",
                    "src=\"./swagger-initializer.js\""
                ))
            }
        }

        it("Can return generated: swagger-initializer.js") {
            withSwaggerApplication {
                compareResource(TEST_SWAGGER_UI_INIT_JS, listOf(
                    "url: '/openapi.json', name: 'My API v1'",
                    "url: '/openapi.json', name: 'My API v2'",
                    "defaultModelExpandDepth: 4",
                    "defaultModelsExpandDepth: 4",
                    "displayOperationId: true",
                    "displayRequestDuration: true",
                    "operationsSorter: 'alpha'",
                    "persistAuthorization: true",
                    "tagsSorter: 'alpha'",
                    "window.ui.initOAuth",
                    "clientId: 'CLIENT_ID'",
                    "clientSecret: 'CLIENT_SECRET'",
                    "realm: 'MY REALM'",
                    "appName: 'TEST APP'",
                    "useBasicAuthenticationWithAccessCodeGrant: true"
                ))
            }
        }
    }

})
