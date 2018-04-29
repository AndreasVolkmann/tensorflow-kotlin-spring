package me.avo.springkotlintest

import org.junit.Assert
import org.junit.Test
import kotlin.reflect.KProperty0

class Tet {

    @Test fun test() {
        data class Data(val id: Int)
        infix fun <T> KProperty0<T>.shouldEqual(value: T) = Assert.assertEquals(this.name, value, this.get())

        val data = Data(1)

        data::id shouldEqual 1

        data::id shouldEqual 2


        with(data) {
            ::id shouldEqual 1
        }

    }

}