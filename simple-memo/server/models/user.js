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
    imagePath: {
        type: String,
    },
    reportCount: {
        type: Number,
    },
    contentList: {
        type: Array,
    },
    pushToken: {
        type: String,
    },
    authenticationDate: {
        type: String,
    },
    isAuthenticated: {
        type: Number,
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
