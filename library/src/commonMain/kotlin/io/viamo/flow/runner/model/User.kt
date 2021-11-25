package io.viamo.flow.runner.model


@Serializable
data class User(val id: Int, val name: String, val phoneNumber: String? = null) {

  fun withPhoneNumber(phoneNumber: String?) = copy(phoneNumber = phoneNumber)
}
