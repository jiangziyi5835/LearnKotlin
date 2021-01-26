package com.example.learnkotlin.model

data class Login(
    var md5: String,
    var password: String,
    var salt: String,
    var sha1: String,
    var sha256: String,
    var username: String
)