package de.komoot.ruleset.ktlint.android

import com.pinterest.ktlint.rule.engine.core.api.*
import com.pinterest.ktlint.rule.engine.core.api.ElementType.FUN
import com.pinterest.ktlint.rule.engine.core.api.ElementType.IDENTIFIER
import de.komoot.ruleset.ktlint.KOMOOT_RULE_SET_ID
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/*
"Remove _p_ prefix from (private) function parameter names"
 */
class FunctionParameterName : Rule(
    RuleId("${KOMOOT_RULE_SET_ID}:function-parameter-name"),
    About(
        maintainer = "Komoot",
        repositoryUrl = "https://github.com/your/project/",
        issueTrackerUrl = "https://github.com/your/project/issues",
    ),
) {
    private var parameters: List<String> = listOf()
    private var fixedParameters: List<String> = listOf()

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        //  || node.elementType == CATCH
        if (node.elementType == FUN) {
            node
                .takeIf {
                    val isNotInterface = it.findChildByType(ElementType.BLOCK) != null
                    val isNotOverride = it.findChildByType(ElementType.MODIFIER_LIST)
                        ?.findChildByType(ElementType.OVERRIDE_KEYWORD) == null
                    isNotInterface && isNotOverride
                }
                ?.findChildByType(ElementType.VALUE_PARAMETER_LIST)
                ?.let { visitInsideTypeParameterList(it, emit, autoCorrect) }
        }

        if (node.elementType == IDENTIFIER && autoCorrect) {
            // Replace cases like property = pProperty -> this.property = property
            val alreadyExistingIndex = fixedParameters.indexOf(node.text)
            if (alreadyExistingIndex != -1) {
                val psiNode = (node.psi as LeafPsiElement)
                println("Already existing parameter name: ${psiNode.text}")
                when {
                    // anotherObject.property case. Do nothing
                    psiNode.prevLeaf(false)?.text == "." -> {
                        println("Skipping modification for: .${psiNode.text}")
                    }
                    // internal var property case. Add with internal prefix to it.
                    psiNode.prevLeaf(false)?.prevLeaf(false)?.text == "var" ||
                        psiNode.prevLeaf(false)?.prevLeaf(false)?.text == "val" -> {
                        println(
                            "Adding internal parameter to list: ${psiNode.text} -> ${
                                getInternalParameter(
                                    psiNode.text,
                                )
                            }",
                        )
                        psiNode.rawReplaceWithText(getInternalParameter(psiNode.text))
                    }
                    else -> {
                        // external property case. Add this. prefix to it
                        println(
                            "Replacing external variable: ${psiNode.text} -> ${
                                getExternalParameter(
                                    psiNode.text,
                                )
                            }",
                        )
                        psiNode.rawReplaceWithText(getExternalParameter(psiNode.text))
                    }
                }
            } else {
                val fixingIndex = parameters.indexOf(node.text)
                if (fixingIndex != -1) {
                    println("Replaced parameter: ${node.text} -> ${fixedParameters[fixingIndex]}")
                    (node.psi as LeafPsiElement).rawReplaceWithText(fixedParameters[fixingIndex])
                }
            }
        }
    }

    private fun visitInsideTypeParameterList(
        parametersNode: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
        autoCorrect: Boolean,
    ) {
        val parameters = getParametersWithOldDefinition(parametersNode)
        if (parameters.isNotEmpty()) {
            if (autoCorrect.not()) {
                emit(
                    parametersNode.startOffset,
                    "Detected invalid parameter names: $parameters",
                    true,
                )
            } else {
                this.parameters = parameters
                fixedParameters = parameters.map { fixParameter(it) }
                println("Cleared list. Saved: $parameters")
            }
        }
    }

    private fun getParametersWithOldDefinition(node: ASTNode?): List<String> {
        node ?: return listOf()
        val parameters = mutableListOf<String>()
        node.children().iterator().forEach { childNode ->
            if (childNode.elementType == ElementType.VALUE_PARAMETER &&
                childNode.children().firstOrNull()?.text?.matches(INVALID_PARAMETER_NAME) == true
            ) {
                parameters.add(childNode.children().first().text)
            }
        }
        return parameters
    }

    private fun fixParameter(parameter: String): String {
        return parameter.substring(1, 2).lowercase() + parameter.substring(2)
    }

    private fun getInternalParameter(parameter: String): String {
        return "internal" + parameter.substring(0, 1).uppercase() + parameter.substring(1)
    }

    private fun getExternalParameter(parameter: String): String {
        return "this.$parameter"
    }

    private companion object {
        val INVALID_PARAMETER_NAME = Regex("[pmscf][A-Z]\\w+")
    }
}
