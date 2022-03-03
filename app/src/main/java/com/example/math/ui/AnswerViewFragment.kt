package com.example.math.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.math.R
import com.example.math.databinding.FragmentAnswerViewBinding
import com.example.math.wolfram.Fetchers.SimpleAPIFetcher


class AnswerViewFragment(private val query: String) : Fragment() {

    private lateinit var binding: FragmentAnswerViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer_view, container, false)

        SimpleAPIFetcher().fetchRequestOnIOThread(onPreExecute = {
            binding.progressBar.visibility = View.VISIBLE
            query
        },
            onPostExecute = {
                binding.progressBar.visibility = View.GONE
                if (it != null)
                    binding.detail.setImageBitmap(it)
                else binding.noData.visibility = View.VISIBLE
            })

        return binding.root
    }


}