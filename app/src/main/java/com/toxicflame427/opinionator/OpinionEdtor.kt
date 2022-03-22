package com.toxicflame427.opinionator

class OpinionEditor {
    var opinion : String = ""
    var agreedVotes : Int = 0
    var disagreedVotes : Int = 0
    constructor(opinion : String, agreedVotes : Int, disagreedVotes : Int){
        this.opinion = opinion
        this.agreedVotes = agreedVotes
        this.disagreedVotes = disagreedVotes
    }
}