const mongoose = require('mongoose');

const { Schema } = mongoose;
const reportSchema = new Schema({
  contentId: String,
  contentName: String,
  userEmail: String, // 신고하는 유저
  reportReason: String,
  authenDay: String,
  reportUser:Array, // 신고할 유저
  complete : Number, // 0: 신고접수  1: 처리 완료

});

module.exports = mongoose.model('Report', reportSchema);
