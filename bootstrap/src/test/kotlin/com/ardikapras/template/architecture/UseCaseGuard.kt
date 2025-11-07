package com.ardikapras.template.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Guard tests for Use Cases to ensure consistent patterns.
 * Use cases should follow the operator invoke pattern for cleaner API.
 */
class UseCaseGuard {
    @Test
    fun `classes with UseCase suffix should reside in application usecase package`() {
        Konsist
            .scopeFromModule("application")
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                it.resideInPackage("${Packages.APPLICATION}.usecase")
            }
    }

    @Test
    fun `use cases should have single constructor parameter injection`() {
        Konsist
            .scopeFromPackage("${Packages.APPLICATION}.usecase")
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                it.hasConstructor { true }
            }
    }

    @Test
    fun `use cases should not have mutable state`() {
        Konsist
            .scopeFromPackage("${Packages.APPLICATION}.usecase")
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                val mutableProps =
                    it.properties().filter { prop ->
                        prop.hasValModifier == false && prop.hasVarModifier == true
                    }
                mutableProps.isEmpty()
            }
    }

    @Test
    fun `use cases should be in application module`() {
        Konsist
            .scopeFromProduction("application")
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                it.resideInModule("application")
            }
    }

    @Test
    fun `use cases should have operator invoke method`() {
        Konsist
            .scopeFromModule("application")
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                it.hasFunction { function ->
                    function.name == "invoke" &&
                        function.hasPublicOrDefaultModifier &&
                        function.hasOperatorModifier
                }
            }
    }
}
