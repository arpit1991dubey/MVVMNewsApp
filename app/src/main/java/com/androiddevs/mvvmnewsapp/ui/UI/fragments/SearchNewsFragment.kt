package com.androiddevs.mvvmnewsapp.ui.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment:Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter:NewsAdapter
    val TAG ="SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as NewsActivity).viewModel
        setupRecyclerView()

        var job: Job?=null
        etSearch.addTextChangedListener {editabe->
            job?.cancel()
            job= MainScope().launch {
                delay(500L)
                editabe?.let{
                    if(editabe.toString().isNotEmpty()){
                        viewModel.searchNews(editabe.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer{ response ->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    response.data?.let{
                            newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let{message ->
                        Log.e(TAG,"An error occured: $message")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }

        })
    }
    private fun hideProgressBar(){
        paginationProgressBar.visibility =View.INVISIBLE
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility=View.VISIBLE
    }
    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}