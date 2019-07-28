package app.sinanyilmaz.firebasearccomp.data

import app.sinanyilmaz.firebasearccomp.model.Message

class Entity : Message{
    override var text: String
        get() = text
        set(value) {}
    override var userName: String
        get() = userName
        set(value) {}
    override var photoUrl: String
        get() = photoUrl
        set(value) {}


    constructor(){}

    constructor(text:String? , userName:String? , photoUrl:String?){
        this.text = text!!
        this.userName = userName!!
        this.photoUrl = photoUrl!!
    }

    fun getTxt():String{
        return text
    }

    fun getUserNam():String{
        return userName
    }

    fun getPhotoUr():String{
        return photoUrl
    }




}
