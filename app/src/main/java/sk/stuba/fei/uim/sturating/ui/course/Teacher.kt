package sk.stuba.fei.uim.sturating.ui.course

// data class for the teacher
data class Teacher(
    var name: String = "-1",
    var avgExaminerScore: Double = -1.0,
    var avgLecturerScore: Double = -1.0,
    var examinerRatingCount: Int = 0,
    var lecturerRatingCount: Int = 0
)