package io.viamo.flow.runner

import io.viamo.flow.runner.model.User

class Greeting {

  fun hello() = "Hello, ${Platform().platform}!"

  fun helloUser(user: User) = "Hello ${user.name}"

  fun createBob() = User(id = 0, name = "Bob")

  fun renameUserToJim(user: User) = user.copy(name = "Jim")
}
