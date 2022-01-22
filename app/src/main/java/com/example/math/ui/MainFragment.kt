package com.example.math.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.math.MainActivity
import com.example.math.R
import com.example.math.databinding.FragmentMainBinding
import com.example.math.models.QuestionTypes
import com.google.firebase.functions.FirebaseFunctions


class MainFragment(private val functions: FirebaseFunctions) : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main, container, false)

        iniViews()

        return binding.root
    }

    private fun iniViews() {
        mainActivity = activity as MainActivity
        binding.addition.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.ADDITION)) }
        binding.subtraction.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.SUBTRACTION)) }
        binding.division.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.DIVISION)) }
        binding.multiplication.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.MULTIPLICATION)) }
        binding.lcm.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.LCM)) }
        binding.hcf.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.HCF)) }
        binding.equations.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.QUADRATIC)) }
        binding.mix.setOnClickListener { mainActivity.changeFragment(QuestionsListFragment(functions,QuestionTypes.MIX)) }
    }

}