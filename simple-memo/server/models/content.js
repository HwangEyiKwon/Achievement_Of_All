const mongoose = require('mongoose');

const { Schema } = mongoose;
const contentSchema = new Schema({
  id: String,
  name: String,
  roomNum : Number,
  startDate: Date,
  endDate: Date,
  achievementRate: Number,
  userList: [String],
  isDone: Number, // 0: 진행중 1: 종료 2: 시작 전
});

module.exports = mongoose.model('Content', contentSchema);
