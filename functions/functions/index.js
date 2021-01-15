const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

exports.initUser = functions.auth.user().onCreate(user => {
    const db = admin.database();
    db.ref(`/users/${user.uid}/name`).set(user.displayName);
    db.ref(`/users/${user.uid}/email`).set(user.email);
    db.ref(`/users/${user.uid}/reg_date`).set(getDate());

    /**
     * TODO: transfer files from AIS database to given user who logs in
     * TODO: calculate the weight for the given user
     */

    return db;
});

function getDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day  = date.getDate();y;
    return year + "-" + month + "-" + day;
}