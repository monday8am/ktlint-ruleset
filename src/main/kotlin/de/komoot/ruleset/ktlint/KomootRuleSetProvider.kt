package de.komoot.ruleset.ktlint

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import de.komoot.ruleset.ktlint.android.FunctionParameterName

internal val KOMOOT_RULE_SET_ID = "komoot-kotlin-ruleset"

class KomootRuleSetProvider : RuleSetProviderV3(
    id = RuleSetId(KOMOOT_RULE_SET_ID)
) {
    override fun getRuleProviders(): Set<RuleProvider> =
        setOf(
            RuleProvider { FunctionParameterName() },
        )
}
