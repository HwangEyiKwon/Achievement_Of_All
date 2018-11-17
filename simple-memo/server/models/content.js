const mongoose = require('mongoose');

const { Schema } = mongoose;
const contentSchema = new Schema({
  id: String,
  name: String,
  roomNum : Number,
  startDate: Date,
  endDate: Date,
  achievementRate: Number,
  userList: [{email: String, newVideo: {path: String, authen: Number, authorizePeople: [{name: String, authenInfo: Number}] } }], //authenInfo: 1: O 2: X
  isDone: Number, // 0: 진행중 1: 종료 2: 시작 전
  description: String,
});

module.exports = mongoose.model('Content', contentSchema);
