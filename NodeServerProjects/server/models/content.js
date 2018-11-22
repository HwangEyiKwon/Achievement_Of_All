const mongoose = require('mongoose');

const { Schema } = mongoose;
const contentSchema = new Schema({
  id: String,
  name: String,
  roomNum : Number,
  startDate: Date,
  endDate: Date,
  achievementRate: Number,
  totalUser: Number,
  userList: [{name: String, email: String, newVideo: {path: String, authen: Number, authorizePeople: [{email: String, authenInfo: Number}] } }], //authenInfo: 0: 실패 1: 성공 / authon: 0: 인증 실패 1: 인증 성공 2: 인증 Not yet
  isDone: Number, // 0: 진행중 1: 종료 2: 시작 전
  description: String,
});

module.exports = mongoose.model('Content', contentSchema);
