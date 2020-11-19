package sk.stuba.fei.uim.sturating.ui.course

data class Course(
    val shortName: String,
    val longName: String,
    val courseLecturer: String,
    val courseExaminer: String,
    val shortDescription: String,
    val longDescription: String,
    val avgCourseScore: Int,
    val avgLecturerScore: Int,
    val avgExaminerScore: Int
)