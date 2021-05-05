package sk.stuba.fei.uim.sturating.ui.mycourses

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import sk.stuba.fei.uim.sturating.ui.course.Course
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import sk.stuba.fei.uim.sturating.R

class AddRatingDialogFragment(
    private val course: Course
) : DialogFragment() {

    private lateinit var rbCourse: RatingBar
    private lateinit var rbLecturer: RatingBar
    private lateinit var rbExaminer: RatingBar
    private lateinit var btnSubmitRating: Button

    private val functions = FirebaseFunctions.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rate_course, container, false)
        dialog?.setTitle(tag)

        root.findViewById<TextView>(R.id.tvAddRatingCourseName).apply { text = course.longName }
        root.findViewById<TextView>(R.id.tvAddRatingCourseLecturer).apply { text = course.courseLecturer }
        root.findViewById<TextView>(R.id.tvAddRatingCourseExaminer).apply { text = course.courseExaminer }
        rbCourse = root.findViewById(R.id.rbCourse)
        rbLecturer = root.findViewById(R.id.rbLecturer)
        rbExaminer = root.findViewById(R.id.rbExaminer)
        btnSubmitRating = root.findViewById(R.id.btnSubmitRating)

        btnSubmitRating.setOnClickListener { onSubmitClick() }

        return root
    }

    private fun onSubmitClick() {
        btnSubmitRating.isEnabled = false
        if (rbCourse.rating == 0.0f && rbLecturer.rating == 0.0f && rbExaminer.rating == 0.0f)
            showDialog("You need to rate at least one field.",false)
        else if (rbCourse.rating == 0.0f || rbLecturer.rating == 0.0f || rbExaminer.rating == 0.0f)
            showDialog("Fields with no stars wont be counted. \n" +
                    "Do you wish to continue?", true)
        else
            submitRating()
    }

    private fun showDialog(message: String, enablePositiveButton: Boolean) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Notice")
        dialogBuilder.setMessage(message)
        if (enablePositiveButton) {
            dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                submitRating()
                dialog.dismiss()
            }
            dialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                btnSubmitRating.isEnabled = true
            }
        } else {
            dialogBuilder.setNeutralButton("Ok") {dialog, _ ->
                dialog.dismiss()
                btnSubmitRating.isEnabled = true
            }
        }
        dialogBuilder.setOnCancelListener { btnSubmitRating.isEnabled = true }
        dialogBuilder.create().show()
    }

    private fun submitRating() {
        addRating().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val e = task.exception

                Log.w(TAG, "addRating:onFailure", e)
                Toast.makeText(context, e?.message ?: "unknown error", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            val parent = parentFragmentManager
                .findFragmentByTag("Course Fragment") as DialogFragment
            dismiss()
            parent.dismiss()
            Toast.makeText(context, task.result, Toast.LENGTH_LONG).show()
        }
    }

    private fun addRating(): Task<String> {
        val data = hashMapOf(
            "courseCode" to course.shortName,
            "courseRating" to rbCourse.rating,

            "lecturerId" to course.courseLecturerId,
            "lecturerRating" to rbLecturer.rating.toDouble(),

            "examinerId" to course.courseExaminerId,
            "examinerRating" to rbExaminer.rating.toDouble(),

            "push" to true
        )

        return functions.getHttpsCallable("addRating")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    companion object {
        const val TAG = "AddRatingDialogFragment"
    }
}