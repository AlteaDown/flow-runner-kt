package io.viamo.flow.runner


class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}
