const functions = require("firebase-functions");
const admin = require("firebase-admin");

exports.addRating = functions.https.onCall((data, context) => {

    // checks if user is authenticated
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 
        'not authenticated');
    }

    // gathering the data from the header
    const courseCode = data.courseCode;
    const courseRating = data.courseRating;
    const lecturerId = data.lecturerId;
    const lecturerRating = data.lecturerRating;
    const examinerId = data.examinerId;
    const examinerRating = data.examinerRating;

    // making sure all the values are in the correct type and value
    if (!typeof(courseCode === 'string') || courseCode.length === 0) {
        throw new functions.https.HttpsError('invalid-argument', 
        '"courseCode" was not present or is empty');
    }
    else if (!typeof(courseRating === 'number')) {
        throw new functions.https.HttpsError('invalid-argument',
        '"courseRating" was not present');
    }
    else if (!typeof(lecturerId === 'number') || lecturerId === -1) {
        throw new functions.https.HttpsError('invalid-argument',
        '"lecturerId" was not present or is empty');
    }
    else if (!typeof(lecturerRating === 'number')) {
        throw new functions.https.HttpsError('invalid-argument',
        '"lecturerRating" was not present');
    }
    else if (!typeof(examinerId === 'number') || examinerId === -1) {
        throw new functions.https.HttpsError('invalid-argument',
        '"examinerId" was not present or is empty');
    }
    else if (!typeof(examinerRating === 'number')) {
        throw new functions.https.HttpsError('invalid-argument',
        '"examinerRating" was not present');
    }

    // rating begins
    console.log(`Rating has started with the following parameters -> uid: ${context.auth.uid}` +
    `courseCode: ${courseCode}, courseRating: ${courseRating}, lecturerId: ${lecturerId}, ` + 
    `lecturerRating: ${lecturerRating}, examinerId: ${examinerId}, examinerRating: ${examinerRating}`);

    // reading user data from db and processing it
    const db = admin.database();
    db.ref(`/users/${context.auth.uid}`).once("value", function (snapshot) {

        // making sure there are some data
        if (snapshot == null) {
            return;
        }

        // checking whether the previous rating was at least a 4 months old on the same course,
        // if so, the rating process will stop
        let recentlyRated = false;
        snapshot.child("ratings").forEach(function (rating) {
            if (rating.child("course_code").val() === courseCode && 
                (rating.child("time_in_millis").val() + 10519200000) > Date.now()) {
                recentlyRated = true;
                }
        });

        if (!recentlyRated) {
            let userWeight = snapshot.child("rating_weight").val();

            // creating a log of the current rating
            let ratingRef = db.ref("/ratings/").push({
                uid: context.auth.uid,
                course_code: courseCode,
                course_rating: courseRating,
                examiner_id: examinerId,
                examiner_rating: examinerRating,
                lecturer_id: lecturerId,
                lecturer_rating: lecturerRating,
                current_user_weight: userWeight,
                time_in_millis: Date.now()
            });
            ratingRef.then( _ => {
                // fields without rating stars on the front-end wont count
                if (courseRating !== 0.0) {
                    rateCourse(db, context.auth, courseCode, courseRating);
                }
                if (lecturerRating !== 0.0) {
                    rateLecturer(db, context.auth, lecturerId, lecturerRating);
                }
                if (examinerRating !== 0.0) {
                    rateExaminer(db, context.auth, examinerId, examinerRating);
                }
            });
        
            // saving course code and current time to easily check if a day passed
            db.ref(`/users/${context.auth.uid}/ratings/`).push({
                rating_id: ratingRef.key,
                course_code: courseCode,
                time_in_millis: Date.now()
            }).then( _ => {
                console.log(`Rating for course ${courseCode} was saved`);
                calculateUserWeight(db, context.auth, snapshot);
            });
        }
    });

    return "Rating successfully processed";
});

// rating the course itself
function rateCourse(db, auth, courseCode, courseRating) {
    db.ref(`/ratings/`).orderByChild("course_code").equalTo(courseCode).once("value", function (ratings) {
        let totalWeight = 0, totalWeightedRating = 0;

        // iterating thru the courses previous ratings, and calculating the weighted sum rating
        ratings.forEach( (rating) => {
            if (rating.child("course_rating").val() !== 0) {
                totalWeight += rating.child("current_user_weight").val();
                totalWeightedRating += (rating.child("course_rating").val() * rating.child("current_user_weight").val());
            }
        });
        let newRating = totalWeightedRating / totalWeight;

        // saving the new rating as a transaction
        db.ref(`/courses/${courseCode}`).transaction( function (course) {
            if (course == null) {
                return course;
            }
            if (0 < newRating && newRating < 1) {
                newRating = 1;
            }
            course.avg_course_score = newRating;
            course.rating_count += 1;
            return course;
        });

        changeUserCourseData(db, auth, courseRating);
    });
}

// rating the lecturer itself
function rateLecturer(db, auth, lecturerId, lecturerRating) {
    db.ref(`/ratings/`).orderByChild("lecturer_id").equalTo(lecturerId).once("value", function (ratings) {
        let totalWeight = 0, totalWeightedRating = 0;

        // iterating thru the lecturers previous ratings, and calculating the weighted sum rating
        ratings.forEach( (rating) => {
            if (rating.child("lecturer_rating").val() !== 0) {
                totalWeight += rating.child("current_user_weight").val();
                totalWeightedRating += (rating.child("lecturer_rating").val() * rating.child("current_user_weight").val());
            }
        });
        let newRating = totalWeightedRating / totalWeight;

        // saving the new rating as a transaction
        db.ref(`/teachers/${lecturerId}`).transaction( function (lecturer) {
            if (lecturer == null) {
                return lecturer;
            }
            if (0 < newRating && newRating < 1) {
                newRating = 1;
            }
            lecturer.avg_lecturer_score = newRating;
            lecturer.lecturer_rating_count += 1;
            return lecturer;
        });

        changeUserLecturerData(db, auth, lecturerRating);
    });
}

// rating the examiner itself
function rateExaminer(db, auth, examinerId, examinerRating) {
    db.ref(`/ratings/`).orderByChild("examiner_id").equalTo(examinerId).once("value", function (ratings) {
        let totalWeight = 0, totalWeightedRating = 0;

        // iterating thru the examiners previous ratings, and calculating the weighted sum rating
        ratings.forEach( (rating) => {
            if (rating.child("examiner_rating").val() !== 0) {
                totalWeight += rating.child("current_user_weight").val();
                totalWeightedRating += (rating.child("examiner_rating").val() * rating.child("current_user_weight").val());
            }
        });
        let newRating = totalWeightedRating / totalWeight;

        // saving the new rating as a transaction
        db.ref(`/teachers/${examinerId}`).transaction( function (examiner) {
            if (examiner == null) {
                return examiner;
            }
            if (0 < newRating && newRating < 1) {
                newRating = 1;
            }
            examiner.avg_examiner_score = newRating;
            examiner.examiner_rating_count += 1;
            return examiner;
        });

        changeUserExaminerData(db, auth, examinerRating);
    });
}

// updating the users local course ratings data
function changeUserCourseData(db, auth, courseRating) {
    db.ref(`/users/${auth.uid}/`).once("value", function (snapshot) {
        let avgRatCourse = snapshot.child("avg_rat_cour").val();
        let noRatCour = snapshot.child("no_rat_cour").val();

        if (noRatCour || noRatCour !== 0) {
            let sum = avgRatCourse * noRatCour;
            sum += courseRating;
            noRatCour++;
            avgRatCourse = sum / noRatCour;
        } else {
            avgRatCourse = courseRating;
            noRatCour = 1;
        }

        const ref = db.ref(`/users/${auth.uid}/`);
        ref.child("avg_rat_cour").set(avgRatCourse);
        ref.child("no_rat_cour").set(noRatCour);
    });
}

// updating the users local lecturer ratings data
function changeUserLecturerData(db, auth, lecturerRating) {
    db.ref(`/users/${auth.uid}/`).once("value", function (snapshot) {
        let avgRatLect = snapshot.child("avg_rat_lect").val();
        let noRatLect = snapshot.child("no_rat_lect").val();

        if (noRatLect || noRatLect !== 0) {
            let sum = avgRatLect * noRatLect;
            sum += lecturerRating;
            noRatLect ++;
            avgRatLect = sum / noRatLect;
        } else {
            avgRatLect = lecturerRating;
            noRatLect = 1;
        }

        const ref = db.ref(`/users/${auth.uid}/`);
        ref.child("avg_rat_lect").set(avgRatLect);
        ref.child("no_rat_lect").set(noRatLect);
    });
}

// updating the users local examiner ratings data
function changeUserExaminerData(db, auth, examinerRating) {
    db.ref(`/users/${auth.uid}/`).once("value", function (snapshot) {
        let avgRatExam = snapshot.child("avg_rat_exam").val();
        let noRatExam = snapshot.child("no_rat_exam").val();

        if (noRatExam || noRatExam !== 0) {
            let sum = avgRatExam * noRatExam;
            sum += examinerRating;
            noRatExam++;
            avgRatExam = sum / noRatExam;
        } else {
            avgRatExam = examinerRating;
            noRatExam = 1;
        }

        const ref = db.ref(`/users/${auth.uid}/`);
        ref.child("avg_rat_exam").set(avgRatExam);
        ref.child("no_rat_exam").set(noRatExam);
    });
}

// calculating the new user weight
function calculateUserWeight(db, auth, userSnapshot) {
    db.ref(`/users/${auth.uid}/rating_weight`).transaction(function (_) {
    console.log("Starting to calculate userWeight:");

    // 0 or 0.2 depending on the local average rating
    // when under 20% and over 95% it returns 0, else 0.2
    let avgRatingUnderOverValue = avgRatingUnderOver(userSnapshot);

    // from 0 to 0.4, depending on the age of the user in the system
    let daysSinceRegistrationValue = daysSinceRegistration(userSnapshot);
    
    // from 0 to 0.2, depending on the number of rated courses
    let numRatingsValue = numRatings(userSnapshot);

    console.log("Ending calculation of userWeight:");

    let userWeight =  avgRatingUnderOverValue + daysSinceRegistrationValue + numRatingsValue + 0.2;
    console.log(`returning userWeight: ${userWeight}`);
    return userWeight;
    });
}

function avgRatingUnderOver(userSnapshot) {
    console.log("---avgRatingUnderOver: start");
    let avgRatCourse = userSnapshot.child("avg_rat_cour").val();
    let avgRatExam = userSnapshot.child("avg_rat_exam").val();
    let avgRatLect = userSnapshot.child("avg_rat_lect").val();

    let percent = 0;

    if (avgRatCourse === 0 && avgRatExam === 0 && avgRatLect === 0) {
        return 0.2;
    }
    else if (avgRatCourse === 0 && avgRatExam === 0) {
        percent = (avgRatLect / 5);
    }
    else if (avgRatExam === 0 && avgRatLect === 0) {
        percent = (avgRatCourse / 5);
    }
    else if (avgRatCourse === 0 && avgRatLect === 0) {
        percent = (avgRatExam / 5);
    }
    else if (avgRatCourse === 0) {
        percent = ((avgRatExam / 10) + (avgRatLect / 10));
    }
    else if (avgRatExam === 0) {
        percent = ((avgRatCourse / 10) + (avgRatLect / 10));
    }
    else if (avgRatLect === 0) {
        percent = ((avgRatCourse / 10) + (avgRatExam / 10));
    }
    else {
        percent = ((avgRatCourse / 15) + (avgRatExam / 15) + (avgRatLect / 15));
    }

    console.log(`avgRatingUnderOver: percent = ${percent}`);
    console.log("---avgRatingUnderOver: end");

    if ((0.2 <= percent) && (percent <= 0.95)) {
        return 0.2;
    }
    return 0;
}

function daysSinceRegistration(userSnapshot) {
    console.log("---daysSinceRegistration: start");
    let date1 = new Date(userSnapshot.child("reg_date").val());
    let date2 = new Date();
    let diffTime = Math.abs(date2 - date1);
    let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    let weight = (diffDays / 1764) * 0.4;
    if (weight > 0.4) {
        weight = 0.4;
    }
    console.log(`---daysSinceRegistration: end return: ${weight}`);
    return weight;
}

function numRatings(userSnapshot) {
    let numRatings = userSnapshot.child("ratings").numChildren();
    if (numRatings > 56) {
        numRatings = 56;
    }
    return (numRatings / 56) * 0.2;
}