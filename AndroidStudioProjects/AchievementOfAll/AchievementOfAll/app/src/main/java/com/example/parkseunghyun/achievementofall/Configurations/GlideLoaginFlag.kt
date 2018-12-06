package com.example.parkseunghyun.achievementofall.Configurations

import android.graphics.drawable.Drawable

object GlideLoadinFlag {


    private var FLAG_IS_THUMBNAIL_UPDATED = false
    private var FLAG_IS_CONTENT_LIST_UPDATED = false
    private var FLAG_IS_USER_STORY_UPDATED = false
    private var FLAG_IS_PROFILE_UPDATED = false
    private var FLAG_IS_JOINED_CONTENTS_UPDATED = false

    var FLAG_UPDATED = true
    var FLAG_NOT_UPDATED = false

    var profileBitmap: Drawable? = null

    fun setThumbnailFlag(flag:Boolean){
        this.FLAG_IS_THUMBNAIL_UPDATED = flag
    }
    fun setContentListFlag(flag:Boolean){
        this.FLAG_IS_CONTENT_LIST_UPDATED = flag
    }
    fun setUserStoryFlag(flag:Boolean){
        this.FLAG_IS_USER_STORY_UPDATED = flag
    }
    fun setProfileFlag(flag:Boolean){
        this.FLAG_IS_PROFILE_UPDATED = flag
    }
    fun setJoinedContentFlag(flag:Boolean){
        this.FLAG_IS_PROFILE_UPDATED = flag
    }

    fun getThumbnailFlag(): Boolean{
        return this.FLAG_IS_THUMBNAIL_UPDATED
    }
    fun getContentListFlag(): Boolean{
        return this.FLAG_IS_CONTENT_LIST_UPDATED
    }
    fun getUserStoryFlag(): Boolean{
        return this.FLAG_IS_USER_STORY_UPDATED
    }
    fun getProfileFlag(): Boolean{
        return this.FLAG_IS_PROFILE_UPDATED
    }
    fun getJoinedContentFlag(): Boolean{
        return this.FLAG_IS_JOINED_CONTENTS_UPDATED
    }

//    fun isHomeDataChanged(): Boolean{
//        val isHomeDataChangedFlag = FLAG_IS_THUMBNAIL_UPDATED || FLAG_IS_CONTENT_LIST_UPDATED || FLAG_IS_PROFILE_UPDATED
//        return isHomeDataChangedFlag
//    }
//
//    fun isContentsDataChanged(): Boolean{
//        val isHomeDataChangedFlag = FLAG_IS_USER_STORY_UPDATED
//        return isHomeDataChangedFlag
//    }


}

