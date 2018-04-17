package com.doapps.habits.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.adapter.ProgramRecycleAdapter
import com.doapps.habits.helper.ClickableDataSnapshotList
import com.doapps.habits.models.FirebaseProgramView
import com.doapps.habits.models.IProgramViewProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*

class ProgramsFragment : Fragment(), ClickableDataSnapshotList {
  private var mAdapter: ProgramRecycleAdapter? = null
  private var mProgramData: MutableList<IProgramViewProvider>? = null
  private var mTitleTop: TextView? = null
  private var mSuccessTop: TextView? = null
  private var mImageTop: ImageView? = null
  private var isEmpty: Boolean = false
  private var topDataSnapshot: DataSnapshot? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_programs, container, false)
    val toolbar = (activity as MainActivity).toolbar
    toolbar.setTitle(R.string.programs)

    if (!persistenceEnabled) {
      FirebaseDatabase.getInstance().setPersistenceEnabled(true)
      persistenceEnabled = true
    }

    val rootRef = FirebaseDatabase.getInstance().reference.child("programs")
    val emptyView = result.findViewById<View>(R.id.empty_view)
    val recyclerView = result.findViewById<RecyclerView>(R.id.recyclerView)
    mImageTop = result.findViewById(R.id.imageViewTop)

    val conMan = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val sharedPreferences = activity!!
        .getSharedPreferences("pref", Context.MODE_PRIVATE)

    val activeNetwork = conMan.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected) {
      sharedPreferences.edit().putBoolean("downloaded", true).apply()
      Log.i("FD", "User downloaded programDatabase")
    }
    if (sharedPreferences.getBoolean("downloaded", false)) {
      emptyView.visibility = View.INVISIBLE
      recyclerView.visibility = View.VISIBLE
      mImageTop!!.visibility = View.VISIBLE
      Log.i("FD", "Database is already downloaded")
    } else {
      isEmpty = true
    }

    mProgramData = ArrayList(5)
    mAdapter = ProgramRecycleAdapter(mProgramData!!, activity!!, this)

    recyclerView.setHasFixedSize(true)

    val llm = LinearLayoutManager(activity)
    recyclerView.layoutManager = llm

    recyclerView.adapter = mAdapter

    mSuccessTop = result.findViewById(R.id.percentTop)
    mImageTop!!.maxHeight = result.height / 3
    mTitleTop = result.findViewById(R.id.titleTop)

    rootRef.addChildEventListener(object : ChildEventListener {
      override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
        var position = Integer.parseInt(dataSnapshot.key)

        Log.i(TAG, "onChildAdded:()")
        if (position == 0) {
          if (isEmpty) {
            emptyView.visibility = View.INVISIBLE
            recyclerView.visibility = View.VISIBLE
            mImageTop!!.visibility = View.VISIBLE
          }
          topDataSnapshot = dataSnapshot
          generateTopProgram(topDataSnapshot)
        } else {
          mProgramData!!.add(--position, FirebaseProgramView(dataSnapshot))
          mAdapter!!.notifyItemInserted(position)
        }
      }

      override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
        Log.i(TAG, "onChildChanged: ()")
        val position = Integer.parseInt(dataSnapshot.key) - 1

        if (position >= 0) {
          mProgramData!!.removeAt(position)
          mProgramData!!.add(position, FirebaseProgramView(dataSnapshot))
          mAdapter!!.notifyItemChanged(position)
        } else {
          topDataSnapshot = dataSnapshot
          generateTopProgram(topDataSnapshot)
        }
      }

      override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        Log.i(TAG, "onChildRemoved()")
        val position = Integer.parseInt(dataSnapshot.key) - 1
        if (position >= 0) {
          mProgramData!!.removeAt(position)
          mAdapter!!.notifyItemRemoved(position)
        } else {
          val programViewProvider = mProgramData!![0]
          mTitleTop!!.text = programViewProvider.title
          val imageLink = programViewProvider.imageLink
          Log.v("IMG_URL", imageLink)

          if (activity != null) {
            Picasso.with(activity!!.applicationContext)
                .load(imageLink)
                .into(mImageTop)
          }

          //Style percent view
          mSuccessTop!!.text = programViewProvider.percent
          mProgramData!!.removeAt(0)
          mAdapter!!.notifyItemRemoved(0)
        }
      }

      override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
        Log.i(TAG, "onChildMoved: ()")
      }

      override fun onCancelled(databaseError: DatabaseError) {
        Log.e("DatabaseError", databaseError.toString())
      }
    })

    return result
  }


  /**
   * The function sets up view for the first program in the list with given {@link #DataSnapshot}
   * by downloading photo, setting onClick and success rate.
   * @param dataSnapshot DataSnapshot of the program to be on top
   */
  private fun generateTopProgram(dataSnapshot: DataSnapshot?) {
    mTitleTop!!.text = dataSnapshot!!.child("name").getValue(String::class.java)
    val imageLink = String.format("http://habit.esy.es/img_progs/%s.jpg",
        dataSnapshot.child("image").getValue(String::class.java))
    Log.v("IMG_URL", imageLink)

    if (activity != null) {
      Picasso.with(activity!!.applicationContext)
          .load(imageLink)
          .into(mImageTop)
    }

    mImageTop!!.setOnClickListener {
      onClick(0, dataSnapshot)
      isTop = true
    }
    mTitleTop!!.setOnClickListener {
      onClick(0, dataSnapshot)
      isTop = true
    }

    //Style percent view
    mSuccessTop!!.visibility = View.VISIBLE
    mSuccessTop!!.text = dataSnapshot.child("success").getValue(String::class.java)
  }

  /**
   * Handles click events on list items
   * @param position the position of an item
   * @param dataSnapshot DataSnapshot the position of an item
   */
  override fun onClick(position: Int, dataSnapshot: DataSnapshot) {
    val fragmentManager = childFragmentManager
    if (isShowing) {
      if (!isTop) {
        generateTopProgram(topDataSnapshot)
      }
      fragmentManager.beginTransaction()
          .remove(fragmentManager.findFragmentById(R.id.recyclerLayout))
          .commit()
      isShowing = false
    } else {
      createProgramApplyFragment(position, fragmentManager)
      isTop = false
    }
  }

  companion object {
    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = ProgramsFragment::class.java.simpleName
    var isShowing: Boolean = false
    private var persistenceEnabled: Boolean = false
    private var isTop: Boolean = false

    private fun createProgramApplyFragment(position: Int, fragmentManager: FragmentManager) {

      // delete previous fragment if showing
      if (isShowing) {
        fragmentManager.beginTransaction()
            .remove(fragmentManager.findFragmentById(R.id.recyclerLayout))
            .commit()
      }

      val programFragment = ProgramFragment()
      val bundle = Bundle()
      bundle.putInt("pos", position)
      programFragment.arguments = bundle
      fragmentManager.beginTransaction().replace(R.id.recyclerLayout, programFragment).commit()

      isShowing = true
    }
  }

}
