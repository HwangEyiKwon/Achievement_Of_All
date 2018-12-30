package com.example.parkseunghyun.achievementofall.Configurations

import android.graphics.drawable.Drawable
import android.net.Uri

/**
    REFACTORED
    TODO: 마지막에 필요없는 플래그는 제거
 */

// GlideLoadingFlag
// 이미지/영상에 사용된 Glide Flag를 모아놨습니다.
object GlideLoadingFlag {

    private var FLAG_IS_THUMBNAIL_UPDATED = false
    private var FLAG_IS_CONTENT_LIST_UPDATED = false
    private var FLAG_IS_USER_STORY_UPDATED = false
    private var FLAG_IS_PROFILE_UPDATED_WITH_IMAGE = false
    private var FLAG_IS_PROFILE_UPDATED_WITHOUT_IMAGE = false
    private var FLAG_IS_JOINED_CONTENTS_UPDATED = false

    var FLAG_UPDATED = true
    var FLAG_NOT_UPDATED = false

    var profileBitmap: Drawable? = null
    var profileURI: Uri? = null

    fun setThumbnailFlag(flag:Boolean){
        this.FLAG_IS_THUMBNAIL_UPDATED = flag
    }

    fun setContentListFlag(flag:Boolean){
        this.FLAG_IS_CONTENT_LIST_UPDATED = flag
    }

    fun setUserStoryFlag(flag:Boolean){
        this.FLAG_IS_USER_STORY_UPDATED = flag
    }

    fun setProfileWithImageFlag(flag:Boolean){
        this.FLAG_IS_PROFILE_UPDATED_WITH_IMAGE = flag
    }

    fun setProfileWithOutImageFlag(flag:Boolean){
        this.FLAG_IS_PROFILE_UPDATED_WITHOUT_IMAGE = flag
    }

    fun setJoinedContentFlag(flag:Boolean){
        this.FLAG_IS_JOINED_CONTENTS_UPDATED = flag
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
        return (this.FLAG_IS_PROFILE_UPDATED_WITH_IMAGE || this.FLAG_IS_PROFILE_UPDATED_WITHOUT_IMAGE)
    }

    fun getProfileWithImageFlag(): Boolean{
        return this.FLAG_IS_PROFILE_UPDATED_WITH_IMAGE
    }

    fun getProfileWithOutImageFlag(): Boolean{
        return this.FLAG_IS_PROFILE_UPDATED_WITHOUT_IMAGE
    }

    fun getJoinedContentFlag(): Boolean{
        return this.FLAG_IS_JOINED_CONTENTS_UPDATED
    }

}

