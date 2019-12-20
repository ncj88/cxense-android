package com.cxense.cxensesdk.model

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserExternalDataBuilderTest {
    private lateinit var builder: UserExternalData.Builder

    private val identity = UserIdentity("cx", "id")
    private val externalItem = ExternalItem("group", "item")

    @BeforeTest
    fun setUp() {
        builder = UserExternalData.Builder(identity)
    }

    @Test
    fun buildValid() {
        with(builder.addExternalItems(externalItem).build()) {
            assertEquals(identity.id, id)
            assertEquals(identity.type, type)
            assertEquals(1, items.size)
            assertTrue {
                with(items.first()) {
                    group == "$type-${externalItem.group}" && item == externalItem.item
                }
            }
        }
    }
}
