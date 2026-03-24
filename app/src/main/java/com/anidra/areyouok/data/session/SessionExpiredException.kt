package com.anidra.areyouok.data.session

class SessionExpiredException(
    message: String = "Session expired. Please log in again."
) : Exception(message)