package com.example.learnkotlin.interfaces

interface TextcallBack {
    var ok:(()->Unit)?
    var fail:((e:String?)->Unit)?
}