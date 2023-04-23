package de.komoot.ruleset.ktlint.android

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class FunctionParameterNameTest {

    private val wrappingRuleAssertThat = assertThatRule { FunctionParameterName() }

    @Test
    fun `simple wrong parameter detection`() {
        val code =
            """
            class AClass {
                fun fn(pNumber: Int) {
                    var pNew = pNumber
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(2, 11, "Detected invalid parameter names: [pNumber]")
    }

    @Test
    fun `if no pParameter is included, any error SHOULD be detected`() {
        val code =
            """
            fun fn(number: Int) {
                var new = number
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasNoLintViolations()
    }

    @Test
    fun `no pParameter is detected inside interfaces`() {
        val code =
            """
            interface TestInterface {
                fun fn(pNumber: Int)
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasNoLintViolations()
    }

    @Test
    fun `no pParameter is detected inside overwritten methods`() {
        val code =
            """
            class TestInterface {
                override fun fn(pNumber: Int) {
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasNoLintViolations()
    }

    @Test
    fun `global parameter is equal to pParameter`() {
        val code =
            """
            class TestInterface {
                private var number = 0
                fun fn(pNumber: Int) {
                    number = pNumber
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(3, 11, "Detected invalid parameter names: [pNumber]")
    }
}
