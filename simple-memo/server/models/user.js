const mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs'); // 암호화를 위한 모듈

const { Schema } = mongoose;

const userSchema = new Schema({
  name: {
    type: String,
  },
  email: {
    type: String,
  },
  password: {
    type: String,
  },
  authority: {
    type: String,
  },
  phoneNumber: {
    type: String,
  },
  nickname: {
    type: String,
  },
  //video path들
  //videoPath: [String],
  //사용자 프로필 이미지?
  imagePath: {
    type: String,
  },
  reportCount: {
    type: Number,
  },
  contentList: [{
    contentId: Number,
    contentName: String,
    videoPath: [{path: String, authen: Number}], //authen = 0:아직 완료 전, 1: 성공 2: 실패
    joinState: Number, //0: 시작 전 컨텐츠 1:컨텐츠 진행 중 2: 컨텐츠 is done
    calendar: [{year: String, month: String, day: String, authen: Number}], // 0: 인증 실패 1: 인증 성공
    authenticationDate: String, //최근 날짜 비디오 기준
    isAuthenticated: Number, // fcm 용도. 최근 날짜 비디오 기준
  }],
  pushToken: {
    type: String,
  },
});

//password를 암호화
userSchema.methods.generateHash = function(password) {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};
userSchema.methods.validPassword = function(password) {
  return bcrypt.compareSync(password, this.password)
};

module.exports = mongoose.model('User', userSchema);
// module.exports = mongoose.model('ContentInfo', contentInfoSchema);
//
// const mongoose = require('mongoose');
// var bcrypt = require('bcrypt-nodejs'); // 암호화를 위한 모듈
//
// const { Schema } = mongoose;
// const userSchema = new Schema({
//     name: {
//         type: String,
//     },
//     email: {
//         type: String,
//     },
//     password: {
//         type: String,
//     },
//     authority: {
//         type: String,
//     },
//     phoneNumber: {
//         type: String,
//     },
//     nickname: {
//         type: String,
//     },
//     imagePath: {
//         type: String,
//     },
//     reportCount: {
//         type: Number,
//     },
//     contentList: [{
//       contentId: {
//         type: Number,
//       },
//       joinState: {
//         type: Number,
//       },
//       calendar: {
//         type: Array,
//       },
//       authenticationDate: {
//         type: String,
//       },
//       isAuthenticated: {
//         type: Number,
//       },
//     }],
//     pushToken: {
//         type: String,
//     },
//     // authenticationDate: {
//     //     type: String,
//     // },
//     // isAuthenticated: {
//     //     type: Number,
//     // },
// });
//
//
//
// //password를 암호화
// userSchema.methods.generateHash = function(password) {
//   return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
// };
//
// userSchema.methods.validPassword = function(password) {
//   return bcrypt.compareSync(password, this.password)
// };
//
// module.exports = mongoose.model('User', userSchema);
