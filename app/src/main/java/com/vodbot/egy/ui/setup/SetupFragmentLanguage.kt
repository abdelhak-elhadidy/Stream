package com.vodbot.egy.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.vodbot.egy.BuildConfig
import com.vodbot.egy.CommonActivity
import com.vodbot.egy.R
import com.vodbot.egy.mvvm.normalSafeApiCall
import com.vodbot.egy.plugins.PluginManager
import com.vodbot.egy.ui.settings.appLanguages
import com.vodbot.egy.ui.settings.getCurrentLocale
import com.vodbot.egy.utils.SubtitleHelper
import com.vodbot.egy.utils.UIHelper.fixPaddingStatusbar
import kotlinx.android.synthetic.main.fragment_setup_language.*
import kotlinx.android.synthetic.main.fragment_setup_media.listview1
import kotlinx.android.synthetic.main.fragment_setup_media.next_btt

const val HAS_DONE_SETUP_KEY = "HAS_DONE_SETUP"

class SetupFragmentLanguage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.fixPaddingStatusbar(setup_root)

        // We don't want a crash for all users
        normalSafeApiCall {
            with(context) {
                if (this == null) return@normalSafeApiCall
                val settingsManager = PreferenceManager.getDefaultSharedPreferences(this)

                val arrayAdapter =
                    ArrayAdapter<String>(this, R.layout.sort_bottom_single_choice)

                // Icons may crash on some weird android versions?
                normalSafeApiCall {
                    val drawable = when {
                        BuildConfig.DEBUG -> R.drawable.cloud_2_gradient_debug
                        BuildConfig.BUILD_TYPE == "prerelease" -> R.drawable.cloud_2_gradient_beta
                        else -> R.drawable.cloud_2_gradient
                    }
                    app_icon_image?.setImageDrawable(ContextCompat.getDrawable(this, drawable))
                }

                val current = getCurrentLocale(this)
                val languageCodes = appLanguages.map { it.third }
                val languageNames = appLanguages.map { (emoji, name, iso) ->
                    val flag = emoji.ifBlank { SubtitleHelper.getFlagFromIso(iso) ?: "ERROR" }
                    "$flag $name"
                }
                val index = languageCodes.indexOf(current)

                arrayAdapter.addAll(languageNames)
                listview1?.adapter = arrayAdapter
                listview1?.choiceMode = AbsListView.CHOICE_MODE_SINGLE
                listview1?.setItemChecked(index, true)

                listview1?.setOnItemClickListener { _, _, position, _ ->
                    val code = languageCodes[position]
                    CommonActivity.setLocale(activity, code)
                    settingsManager.edit().putString(getString(R.string.locale_key), code).apply()
                    activity?.recreate()
                }

                next_btt?.setOnClickListener {
                    // If no plugins go to plugins page
                    val nextDestination = if (
                        PluginManager.getPluginsOnline().isEmpty()
                        && PluginManager.getPluginsLocal().isEmpty()
                    //&& PREBUILT_REPOSITORIES.isNotEmpty()
                    ) R.id.action_navigation_global_to_navigation_setup_extensions
                    else R.id.action_navigation_setup_language_to_navigation_setup_provider_languages

                    findNavController().navigate(
                        nextDestination,
                        SetupFragmentExtensions.newInstance(true)
                    )
                }

                skip_btt?.setOnClickListener {
                    findNavController().navigate(R.id.navigation_home)
                }
            }
        }
    }


}