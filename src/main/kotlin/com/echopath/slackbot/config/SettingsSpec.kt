package com.echopath.slackbot.config

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Staschke
 */

object SettingsSpec : ConfigSpec(null) {
    val webhook_url by required<String>("webhook_url")
    val bot_token by required<String>("bot_token")
    val bot_user_token by required<String>("bot_user_token")
}