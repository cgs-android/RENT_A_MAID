package com.task.ui.component.home.fragment.projectlist.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.task.R
import com.task.data.dto.projectlist.ProjectDetailsResponse
import com.task.data.dto.projectlist.TeamMembers
import com.task.databinding.ItemProjectlistBinding
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener
import com.task.utils.DateUtils.formatDate
import com.task.utils.DateUtils.isTodayOrTomorrowProject
import com.task.utils.DateUtils.returnCurrentDate
import com.task.utils.RegexUtils.removeLastChar


class ProjectListViewHolder(private val itemBinding: ItemProjectlistBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    fun bind(
        projectDetailsResponse: ProjectDetailsResponse,
        teamMembers: List<TeamMembers>,
        iProjectListListener: IProjectListListener,
        position: Int,
        context: Context
    ) {

        val serverStartDate = formatDate(projectDetailsResponse.start_date)

        itemBinding.pliProjectIdTextView.text =
            String.format(
                context.resources.getString(
                    R.string.project
                ) + " 000" + projectDetailsResponse.id
            )

        itemBinding.pliWholeConstraintLayout.setOnClickListener {
            iProjectListListener.onProjectItemSelected(
                position
            )
        }


        for (team in teamMembers) {
            if (team.Roles.rolename.contains(
                    context.resources.getString(
                        R.string.team_leader
                    )
                )
            ) {
                mTeamLead =
                    String.format(
                        team.Users.first_name + " " + team.Users.last_name + context.getString(
                            R.string.team_lead
                        )
                    )
            } else {
                mTeamMember +=
                    String.format(team.Users.first_name + " " + team.Users.last_name + ", ")
            }
        }


        if (!mTeamMember.isNullOrEmpty()) {
            mTeamMember = removeLastChar(mTeamMember)
        }
        if (mTeamLead.isNullOrEmpty()) {
            itemBinding.pliProjectDescriptionTextView.text = mTeamMember
        } else {
            if (mTeamMember.isNullOrBlank()) {
                itemBinding.pliProjectDescriptionTextView.text = mTeamLead
            } else {
                itemBinding.pliProjectDescriptionTextView.text =
                    String.format(mTeamLead + "\n" + mTeamMember)
            }
        }


        when (isTodayOrTomorrowProject(returnCurrentDate(), serverStartDate)) {
            1 -> {
                itemBinding.pliProjectDetailsConstraintLayout.visibility = View.VISIBLE
                itemBinding.pliProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorGreen
                    )
                )
                itemBinding.pliProjectDateTextView.text = context.resources.getString(
                    R.string.today
                )
                projectDetailsResponse.projectStatusColor = 1
            }
            -1 -> {
                itemBinding.pliProjectDetailsConstraintLayout.visibility = View.GONE
                itemBinding.pliProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlue
                    )
                )
                itemBinding.pliProjectDateTextView.text = serverStartDate
                projectDetailsResponse.projectStatusColor = -1
            }
            0 -> {
                itemBinding.pliProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorOrange
                    )
                )
                itemBinding.pliProjectDateTextView.text = context.resources.getString(
                    R.string.tomorrow
                )
                projectDetailsResponse.projectStatusColor = 0
            }
        }

    }


}
