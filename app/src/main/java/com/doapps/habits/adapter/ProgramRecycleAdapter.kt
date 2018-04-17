package com.doapps.habits.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.doapps.habits.R
import com.doapps.habits.helper.ClickableDataSnapshotList
import com.doapps.habits.models.IProgramViewProvider
import com.doapps.habits.view.holders.ProgramViewHolder
import com.squareup.picasso.Picasso

class ProgramRecycleAdapter(private val mProgramList: List<IProgramViewProvider>,
                            private val mContext: Context,
                            private val mFragment: ClickableDataSnapshotList)
  : RecyclerView.Adapter<ProgramViewHolder>() {
  /**
   * Top program image ImageView
   */
  private lateinit var mImageTop: ImageView
  /**
   * Top program title TextView
   */
  private lateinit var mTitleTop: TextView
  /**
   * Top program success rate TextView
   */
  private lateinit var mSuccessTop: TextView

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.program_list_item, parent, false)

    mTitleTop = parent.rootView.findViewById(R.id.titleTop)
    mImageTop = parent.rootView.findViewById(R.id.imageViewTop)
    mSuccessTop = parent.rootView.findViewById(R.id.percentTop)

    return ProgramViewHolder(view)
  }

  override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
    val program = mProgramList[position]
    holder.titleTextView.text = program.title
    holder.percentTextView.text = program.percent

    holder.titleTextView.setOnClickListener {
      Picasso.with(mContext.applicationContext)
          .load(program.imageLink)
          .into(mImageTop)
      mTitleTop.text = program.title
      mSuccessTop.visibility = View.VISIBLE
      mSuccessTop.text = program.snapshot.child("success").getValue(String::class.java)
      mFragment.onClick(position + 1, program.snapshot)
    }
  }

  /**
   * A function to return list size
   * @return size of the list
   */
  override fun getItemCount() = mProgramList.size
}