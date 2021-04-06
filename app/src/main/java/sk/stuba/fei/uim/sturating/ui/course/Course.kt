package sk.stuba.fei.uim.sturating.ui.course

data class Course(
    val id: Int,
    var shortName: String = "-1",
    var longName: String = "-1",
    var courseLecturerId: Int = -1,
    var courseLecturer: String = "-1",
    var courseExaminerId: Int = -1,
    var courseExaminer: String = "-1",
    var shortDescription: String = "-1",
    var longDescription: String = "-1",
    var avgCourseScore: Double = -1.0,
    var avgLecturerScore: Double = -1.0,
    var avgExaminerScore: Double = -1.0,
    var teacherIds: ArrayList<Int> = arrayListOf()
)