const functions = require("firebase-functions");
const https = require("https");

const onRegister = require("./register");
const addRating = require("./addRating");

exports.initUser = onRegister.initUser;
exports.addRating = addRating.addRating;