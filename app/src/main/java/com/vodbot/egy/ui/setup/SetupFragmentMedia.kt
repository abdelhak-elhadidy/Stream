package com.vodbot.egy.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.vodbot.egy.R
import com.vodbot.egy.TvType
import com.vodbot.egy.utils.DataStore.removeKey
import com.vodbot.egy.utils.USER_SELECTED_HOMEPAGE_API
import com.vodbot.egy.utils.UIHelper.fixPaddingStatusbar
import kotlinx.android.synthetic.main.fragment_setup_media.*


class SetupFragmentMedia : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.fixPaddingStatusbar(setup_root)

        with(context) {
            if (this == null) return
            val settingsManager = PreferenceManager.getDefaultSharedPreferences(this)

            val arrayAdapter =
                ArrayAdapter<String>(this, R.layout.sort_bottom_single_choice)

            val names = enumValues<TvType>().sorted().map { it.name }
            val selected = mutableListOf<Int>()

            arrayAdapter.addAll(names)
            listview1?.let {
                it.adapter = arrayAdapter
                it.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE

                it.setOnItemClickListener { _, _, _, _ ->
                    it.checkedItemPositions?.forEach { key, value ->
                        if (value) {
                            selected.add(key)
                        } else {
                            selected.remove(key)
                        }
                    }
                    val prefValues = selected.mapNotNull { pos ->
                        val item = it.getItemAtPosition(pos)?.toString() ?: return@mapNotNull null
                        val itemVal = TvType.valueOf(item)
                        itemVal.ordinal.toString()
                    }.toSet()
                    settingsManager.edit()
                        .putStringSet(getString(R.string.prefer_media_type_key), prefValues)
                        .apply()

                    // Regenerate set homepage
                    removeKey(USER_SELECTED_HOMEPAGE_API)
                }
            }

            next_btt?.setOnClickListener {
                findNavController().navigate(R.id.navigation_setup_media_to_navigation_setup_layout)
            }

            prev_btt?.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }


}