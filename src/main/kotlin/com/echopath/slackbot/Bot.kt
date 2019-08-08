package com.echopath.slackbot

import com.echopath.slackbot.config.SettingsSpec
import com.github.seratch.jslack.Slack
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import mu.KLogging
import java.io.File
import java.util.*


/**
 * @author Kyle Staschke
 */

class Bot {

    var config: Config = Config { addSpec(SettingsSpec) }

    lateinit var service: BotService

    fun init() {
        logger.info { "Starting Echopath Slackbot..." }
        initDirs()
        initConfigs()

        WEBHOOK = config[SettingsSpec.webhook_url]
        TOKEN = config[SettingsSpec.bot_token]
        USER_TOKEN = config[SettingsSpec.bot_user_token]

        testBotAuth()

        start()
    }

    /**
     * Start the bot process.
     */
    private fun start() {
        logger.info { "Starting Slackbot thread..." }
        service = BotService()


        /**
         * Just tests for now
         */
        val user = Api.methods().usersList { it.token(TOKEN) }
            .members
            .filter { it.name == "kslrtips" }
            .first()!!

        service.sendRunningLatePrompt(user)
    }

    private fun initDirs() {
        if(!File(DATA_PATH).exists()) {
            File(DATA_PATH).mkdirs()
            logger.info { "Created required directories..." }
        }
    }

    private fun initConfigs() {
        if(!File(CONFIG_PATH).exists()) {
            setup()
        } else {
            config = Config { addSpec(SettingsSpec) }.from.yaml.file(CONFIG_PATH)
            logger.info { "Loaded slackbot.settings.yml configuration." }
        }
    }

    /**
     * Handles the questions for the setup.
     */
    private fun setup() {
        var webhook_url = ""
        var bot_token = ""
        var bot_user_token = ""

        val scanner = Scanner(System.`in`)
        println("It appears the Echopath SlackBot has not been setup yet.")
        println("Would you like to configure it? (y/n)")

        if(scanner.hasNext() && scanner.nextLine() in arrayOf("y","yes","true")) {

            println("Enter Slack Webhook URL:")
            if(scanner.hasNext()) {
                webhook_url = scanner.nextLine()
            }

            println("Enter Slack Bot Token:")
            if(scanner.hasNext()) {
                bot_token = scanner.nextLine()
            }

            println("Enter Slack Bot User Token:")
            if(scanner.hasNext()) {
                bot_user_token = scanner.nextLine()
            }

            println("--------------------------------------------------------")
            println("Webhook URL: ${webhook_url}")
            println("Bot Token: ${bot_token}")
            println("Bot User Token: ${bot_user_token}")
            println(" ")
            println("Is the information above correct? (y/n)")

            if(scanner.hasNext() && scanner.nextLine() in arrayOf("y","yes","true")) {
                config[SettingsSpec.webhook_url] = webhook_url
                config[SettingsSpec.bot_token] = bot_token
                config[SettingsSpec.bot_user_token] = bot_user_token

                config.toYaml.toFile(CONFIG_PATH)

                logger.info { "Created settings.properties in directory ${DATA_PATH}..." }
            } else {
                setup()
            }

        } else {
            logger.info { "Ending Slackbot process..." }
        }
    }

    private fun testBotAuth() {
        val response = Api.methods().botsInfo {
            it.token(TOKEN)
        }

        if(response.isOk) {
            logger.info { "Slackbot has successfully hooked into Slack API..." }
        } else {
            logger.error { "Slackbot was unable to hook into Slack API. Verify the webhook and bot tokens are correct." }
            println(response)
            System.exit(1)
        }
    }

    companion object : KLogging() {
        const val DATA_PATH: String = "echopath"
        const val CONFIG_PATH: String = "echopath/slackbot.settings.yml"
        lateinit var WEBHOOK: String
        lateinit var TOKEN: String
        lateinit var USER_TOKEN: String
        val Api = Slack.getInstance()
    }
}