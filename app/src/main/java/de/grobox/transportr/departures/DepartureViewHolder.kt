/*
 *    Transportr
 *
 *    Copyright (c) 2013 - 2018 Torsten Grote
 *
 *    This program is Free Software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.grobox.transportr.departures

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.google.common.base.Strings

import java.util.Date

import de.grobox.transportr.R
import de.grobox.transportr.ui.LineView
import de.schildbach.pte.dto.Departure

import android.view.View.GONE
import android.view.View.VISIBLE
import de.grobox.transportr.utils.DateUtils
import de.grobox.transportr.utils.DateUtils.*
import de.grobox.transportr.utils.TransportrUtils.getLocationName
import kotlinx.android.synthetic.main.list_item_departure.view.*

internal class DepartureViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val card: CardView = v as CardView
    private val line: LineView = v.line
    private val lineName: TextView = v.lineNameView
    private val timeRel: TextView = v.departureTimeRel
    private val timeAbs: TextView = v.departureTimeAbs
    private val delay: TextView = v.delay
    private val destination: TextView = v.destinationView
    private val position: TextView = v.positionView
    private val message: TextView = v.messageView

    fun bind(dep: Departure) {
        // times and delay
        var plannedTime : Date? = dep.plannedTime
        var predictedTime : Date? = dep.predictedTime
        if (plannedTime == null) {
            if (predictedTime != null) {
                plannedTime = Date(predictedTime.time)
                predictedTime = null
            }
            else throw RuntimeException()
        }
        setRelativeDepartureTime(timeRel, predictedTime ?: plannedTime)
        timeAbs.text = getTime(timeAbs.context, plannedTime)
        predictedTime?.let {
            val delayTime = it.time - plannedTime.time
            delay.text = getDelayText(delayTime)
            if (delayTime <= 0)
                delay.setTextColor(ContextCompat.getColor(delay.context, R.color.md_green_500))
            else
                delay.setTextColor(ContextCompat.getColor(delay.context, R.color.md_red_500))
            delay.visibility = VISIBLE
        } ?: run { delay.visibility = GONE }

        // line icon and name
        line.setLine(dep.line)
        lineName.text = dep.line.name

        // line destination
        dep.destination?.let {
            destination.text = getLocationName(it)
        } ?: run { destination.text = null }

        // platform/position
        dep.position?.let {
            position.text = it.name
            position.visibility = VISIBLE
        } ?: run { position.visibility = GONE }

        // show message if available
        if (dep.message.isNullOrEmpty()) {
            message.visibility = GONE
        } else {
            message.text = dep.message
            message.visibility = VISIBLE
        }

        // TODO show line from here on
        card.isClickable = false
    }

}
