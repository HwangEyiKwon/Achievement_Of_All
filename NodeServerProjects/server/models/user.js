const mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs'); // 암호화를 위한 모듈

const { Schema } = mongoose;
const userSchema = new Schema({
  name: String,
  email: String,
  password: String,
  authority: String,
  phoneNumber: String,
  nickname: String,
  //사용자 프로필 이미지?
  imagePath: String,
  reportCount: Number,
  contentList: [{
    contentId: Number,
    contentName: String,
    videoPath: [{path: String, authen: Number}], //authen = 0: 인증 실패 1: 인증 성공 2: 인증 Not yet
    joinState: Number, //0: 시작 전 컨텐츠 1:컨텐츠 진행 중 2: 컨텐츠 is done 4: 컨텐츠 참가 중이지만, 최종 실패
    calendar: [{year: String, month: String, day: String, authen: Number}], // 0: 인증 실패 1: 인증 성공 2: 인증 Not yet
    authenticationDate: String, //최근 날짜 비디오 기준
    isUploaded: Number, // fcm 용도. 최근 날짜 비디오 기준
    money: Number, //컨텐츠 별 개인 금액
  }],
  pushToken: String,
});

//password를 암호화
userSchema.methods.generateHash = function(password) {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};
userSchema.methods.validPassword = function(password) {
  return bcrypt.compareSync(password, this.password)
};

module.exports = mongoose.model('User', userSchema);
