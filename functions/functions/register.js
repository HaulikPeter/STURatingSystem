const functions = require("firebase-functions");
const admin = require("firebase-admin");
const https = require("https");
const { reverse } = require("dns");

admin.initializeApp(functions.config().firebase);

exports.initUser = functions.auth.user().onCreate(user => {
    const db = admin.database();
    let userNameReg = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+/igm;
    let userName = user.email.match(userNameReg)[0];
    console.log("writing initial data to db of user");
    setDbVal(db, user, "user_name", userName);
    setDbVal(db, user, "name", user.displayName);
    setDbVal(db, user, "email", user.email);
    setDbVal(db, user, "reg_date", Date.now());
    setDbVal(db, user, "no_rat_cour", 0);
    setDbVal(db, user, "no_rat_lect", 0);
    setDbVal(db, user, "no_rat_exam", 0);
    setDbVal(db, user, "avg_rat_cour", 0);
    setDbVal(db, user, "avg_rat_lect", 0);
    setDbVal(db, user, "avg_rat_exam", 0);

    /*
     *  After consultation whether to use this method 
     *  to gather information about the logged in user,
     *  we are not going to use, since we did not get permission for it.

    var options = {
        host: 'is.stuba.sk',
        port: 443,
        path: '/uissuggest.pl?_suggestHandler=lide&_suggestKey=',
        method: 'GET'
    };
    options.path += userName;

    var output = "";
    https.get(options, response => {
        response.on("data", chunk => {
            output += chunk;
        });
        response.on("end", () => {
            console.log("GET method was successfull");
            let json = JSON.parse(output);
            if (Object.keys(json["data"]).length !== 0) {
                console.log("data contains values, read/write to db");

                let studyType = json["data"][0][3].match(/.-/g)[0][0];
                setDbVal(db, user, "study_type", studyType);

                let rev = reverseString(json["data"][0][3]);
                setDbVal(db, user, "current_year", rev[1]);
                //TODO: PHD studs has no semesters
                setDbVal(db, user, "current_semester", rev[9]);
            }
        });
    });

    function reverseString(str) {
    if (str === "")
        return "";
    else
        return reverseString(str.substr(1)) + str.charAt(0);
    }
    

    // Test data, since there were no permission granted...
    const studyType = "B";
    const currentYear = 3;
    const currentSemester = 8;
    const creditsOwned = 150;
    const wholeStudyAvg = 2.48;
    

    console.log("writing test data to db");
    setDbVal(db, user, "study_type", studyType);
    setDbVal(db, user, "current_year", currentYear);
    setDbVal(db, user, "current_semester", currentSemester);
    setDbVal(db, user, "credits_owned", creditsOwned);
    setDbVal(db, user, "whole_study_avg", wholeStudyAvg);
    console.log("test data written to db");
    
    // weighing the data by the model chart
    console.log("calculating users initial weight");
    let studyRatio = wholeStudyAvg / (4 / 0.2);
    let creditRate = creditsOwned / (180 / 0.05);
    let acYearRate = currentYear / (3 / 0.05);
    let semestRate = currentSemester / (6 / 0.05);

    // values could be out of range, due to extended study length, ...
    // we need to make sure it has an upper limit
    if (creditRate > 0.05) {
        creditRate = 0.05;
    }
    if (acYearRate > 3) {
        acYearRate = 3;
    }
    if (semestRate > 6) {
        acYearRate = 6;
    }

    // maximum sum amount is 3.5
    let sum = studyRatio + creditRate + acYearRate + semestRate;
    setDbVal(db, user, "rating_weight", sum);
    */

    setDbVal(db, user, "rating_weight", 0.2);

    return db;
});

function setDbVal(db, user, ref, val) {
    db.ref(`/users/${user.uid}/${ref}`).set(val)
}