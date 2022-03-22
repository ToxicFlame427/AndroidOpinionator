package com.toxicflame427.opinionator

class AccountAdder {
    var username : String = ""
    var password : String = ""
    var disagreedCount : Int = 0
    var agreedCount : Int = 0
    constructor(username : String, password : String, agreedCount : Int, disagreedCount : Int){
        this.username = username
        this.password = password
        this.agreedCount = agreedCount
        this.disagreedCount = disagreedCount
    }
}