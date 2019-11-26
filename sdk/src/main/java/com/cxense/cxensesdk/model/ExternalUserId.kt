package com.cxense.cxensesdk.model

class ExternalUserId(
    userId: String,
    userType: String
) {
    val userType: String = userType.also {
        require(it.length <= USER_TYPE_MAX_LENGTH) {
            "User type can't be longer than $USER_TYPE_MAX_LENGTH symbols"
        }
    }

    val userId: String = userId.also {
        if (userType == CX_USER_TYPE)
            require(it.matches(CX_USER_ID_REGEX.toRegex())) {
                "The valid characters for internal ids are digits, letters, space and the special characters \"=@+-_.\". Max length is 64 symbols"
            }
        else
            require(it.matches(CUSTOMER_USER_ID_REGEX.toRegex())) {
                "The valid characters for external ids are digits, letters, space and the special characters !#$%%&()*+,-./;<=>?@[]^_{}~|. Max length is 100 symbols"
            }
    }

    companion object {
        private const val CX_USER_TYPE = "cx"
        private const val USER_TYPE_MAX_LENGTH = 10
        private const val CX_USER_ID_REGEX = "^[\\w =@+\\-\\.]{1,64}$"
        private const val CUSTOMER_USER_ID_REGEX = "^[\\w !#$%&()*+,\\-./;<=>?@\\[\\]^{}~|]{1,100}$"
    }
}