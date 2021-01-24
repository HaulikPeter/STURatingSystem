package sk.stuba.fei.uim.sturating.ui.course

import com.google.firebase.database.*

class CourseReader {

    companion object {

    }





//    companion object {
//        private lateinit var adapter: CourseViewAdapter
//        private val courseList = mutableListOf<Course>()
//        private val courseNames = mutableListOf<String>()
//        private val auth = FirebaseAuth.getInstance()
//        private val db = FirebaseDatabase.getInstance().reference
//
//        private var shortName = ""
//        private var longName = ""
//        private var descShort = ""
//        private var descLong = ""
//        private var avg_course_score = 0
//        private var avg_lect_score = 0
//        private var avg_examiner_score = 0
//        private var lecturerName = ""
//        private var examinerName = ""
//
//        fun read(adapter: CourseViewAdapter): MutableList<Course> {
//            this.adapter = adapter
//            readCourses()
//            loadCourses()
//            return courseList
//        }
//
//        private fun readCourses() {
//            db.child("users").child(auth.uid.toString()).child("courses")
//                .addValueEventListener(userListener)
//        }
//
//        private val userListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (child in snapshot.children) {
//                    courseNames.add(child.value.toString())
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) { Log.w("Error reading course list from user", error.toException()) }
//        }
//
//        private fun loadCourses() {
//            for (courseName in courseNames) {
//                db.child("courses").child(courseName).addValueEventListener(courseListener)
//                courseList.clear()
//                courseList.add(Course(
//                    shortName, longName, lecturerName, examinerName, descShort, descLong,
//                    avg_course_score, avg_lect_score, avg_examiner_score
//                ))
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//        private val courseListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                shortName = snapshot.key.toString()
//                longName = snapshot.child("name").value.toString()
//                descShort = snapshot.child("description_short").value.toString()
//                descLong = snapshot.child("description_long").value.toString()
//                avg_course_score = snapshot.child("avg_course_score").value.toString().toInt()
//
//                // need to get lecturer data
//                db.child("teachers").child(snapshot.child("lecturer_id").value.toString())
//                    .addValueEventListener(lecturerListener)
//                db.child("teachers").child(snapshot.child("examiner_id").value.toString())
//                    .addValueEventListener(examinerListener)
//            }
//
//            override fun onCancelled(error: DatabaseError) { Log.w("Error reading from course list", error.toException()) }
//        }
//
//        private val lecturerListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                lecturerName = snapshot.child("name").value.toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) { Log.w("Error reading from teacher list", error.toException()) }
//        }
//        private val examinerListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                examinerName = snapshot.child("name").value.toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) { Log.w("Error reading from teacher list", error.toException()) }
//        }
//    }
}