const mongoose = require('mongoose');

const { Schema } = mongoose;
const appInfoSchema = new Schema({
  appInfo: String,
  noticeInfo: String
});

module.exports = mongoose.model('App', appInfoSchema);
