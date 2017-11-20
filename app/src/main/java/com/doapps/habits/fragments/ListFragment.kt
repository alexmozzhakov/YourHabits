package com.doapps.habits.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.adapter.HabitRecycleAdapter
import com.doapps.habits.adapter.HabitSearchAdapter
import com.doapps.habits.adapter.IMovableListAdapter
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.helper.SimpleItemTouchHelperCallback
import com.doapps.habits.listeners.EmptyListListener
import com.doapps.habits.models.Habit
import com.doapps.habits.view.holders.HabitViewHolder
import java.util.*
import java.util.concurrent.ExecutionException

class ListFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

  private var searchView: SearchView? = null
  private var recycleAdapter: RecyclerView.Adapter<HabitViewHolder>? = null
  private var recyclerView: RecyclerView? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_list, container, false)
    recyclerView = result.findViewById(R.id.habits_list)
    recyclerView!!.setHasFixedSize(true)

    val emptyView = result.findViewById<View>(R.id.empty_view)
    val toolbar = (activity as MainActivity).toolbar
    toolbar.setTitle(R.string.list)
    EmptyListListener.listener.addObserver { _, _ ->
      recyclerView!!.visibility = View.GONE
      emptyView.visibility = View.VISIBLE
      searchView?.visibility = View.GONE
    }

    val habitListManager = HabitListManager.getInstance(context)
    try {
      if (habitListManager.list.isEmpty()) {
        recyclerView!!.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
      } else {
        try {
          Log.i("List", Arrays.toString(habitListManager.list.toTypedArray()).toString())
        } catch (e: InterruptedException) {
          e.printStackTrace()
        } catch (e: ExecutionException) {
          e.printStackTrace()
        }

        recyclerView!!.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        val llm = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = llm
        recycleAdapter = HabitRecycleAdapter(habitListManager)
        recyclerView!!.adapter = recycleAdapter
        val callback = SimpleItemTouchHelperCallback(recycleAdapter as IMovableListAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        searchView = toolbar.rootView.findViewById(R.id.toolbar_search)
        searchView!!.visibility = View.VISIBLE
//        Log.i(TAG, "height = ${toolbar.height}")
//        searchView!!.layoutParams = CoordinatorLayout.LayoutParams(toolbar.width - 72f.dpToPixel(context!!), toolbar.height)
        searchView!!.setOnQueryTextListener(this)
        searchView!!.setOnCloseListener(this)
        val searchEditText = searchView!!.findViewById<EditText>(R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)
      }
    } catch (e: InterruptedException) {
      e.printStackTrace()
    } catch (e: ExecutionException) {
      e.printStackTrace()
    }

    return result
  }

  override fun onQueryTextSubmit(s: String): Boolean = false

  override fun onQueryTextChange(s: String): Boolean {
    performSearch(s)
    return false
  }

  private fun performSearch(str: String) {
    val lowerCaseString = str.toLowerCase()
    val habitList: List<Habit>
    try {
      habitList = HabitListManager.getInstance(context).list
      val searchResult = ArrayList<Habit>(habitList.size)
      habitList.indices
          .filter { habitList[it].title.toLowerCase().contains(lowerCaseString) }
          .mapTo(searchResult) { habitList[it] }
      recycleAdapter = HabitSearchAdapter(searchResult, HabitListManager.getInstance(context))
      recyclerView!!.adapter = recycleAdapter
    } catch (e: InterruptedException) {
      e.printStackTrace()
    } catch (e: ExecutionException) {
      e.printStackTrace()
    }
  }

  override fun onDestroy() {
    searchView?.visibility = View.GONE
    super.onDestroy()
  }

  override fun onClose(): Boolean {
    recycleAdapter = HabitRecycleAdapter(HabitListManager.getInstance(context))
    recyclerView!!.adapter = recycleAdapter
    return false
  }

  companion object {
    private val TAG: String = ListFragment::class.java.simpleName
  }
}
