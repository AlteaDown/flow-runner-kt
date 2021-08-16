package io.viamo.flow.runner.model

data class User(val id: Int, val name: String, val phoneNumber: Number? = null) {

  fun withPhoneNumber(phoneNumber: Number?) = copy(phoneNumber = phoneNumber)
}
