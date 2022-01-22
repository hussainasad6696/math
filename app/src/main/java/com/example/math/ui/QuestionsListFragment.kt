package com.example.math.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.math.MainActivity
import com.example.math.R
import com.example.math.adapter.QuestionsAdapter
import com.example.math.databinding.FragmentQuestionsListBinding
import com.example.math.interfaces.OnQuestionClickListener
import com.example.math.models.QuestionTypes
import com.example.math.models.Questions
import com.example.math.models.QuestionsItem
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


class QuestionsListFragment(
    private val functions: FirebaseFunctions,
    private val questionTypes: QuestionTypes
) : Fragment(), OnQuestionClickListener {

    private lateinit var binding: FragmentQuestionsListBinding
    private lateinit var adapter: QuestionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_questions_list, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        adapter = QuestionsAdapter(requireContext(), this)
        binding.fragmentQuestionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentQuestionsList.adapter = this.adapter

        lifecycleScope.async(onPreExecute = {
            binding.progressCircular.visibility = View.VISIBLE
        }, doInBackground = {
            val gson = Gson()
            gson.fromJson(parseJson(), Questions::class.java)
        }, onPostExecute = {
            binding.progressCircular.visibility = View.GONE
            it.forEach { question ->
                if (question.questionType.contains(questionTypes.name.lowercase()))
                    adapter.addDataToList(question)
            }
        })

    }

    private fun CoroutineScope.async(
        onPreExecute: () -> Unit,
        doInBackground: () -> Questions, onPostExecute: (Questions) -> Unit
    ) = launch(Dispatchers.Main) {
        onPreExecute()
        val result = withContext(Dispatchers.IO) {
            doInBackground()
        }
        onPostExecute(result)
    }

    private fun parseJson(): String {
        val inputStream = resources.openRawResource(R.raw.questions)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use { input ->
            val reader: Reader = BufferedReader(InputStreamReader(input, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }
        return writer.toString()
    }

    override fun onItemClickListener(question: QuestionsItem) {
        (activity as MainActivity).changeFragment(QuestionsFragment(functions,question,questionTypes))
    }
}