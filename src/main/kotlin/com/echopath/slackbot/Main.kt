package com.echopath.slackbot

/**
 * @author Kyle Staschke
 */

@Suppress("UnusedMainParameter")
object Main {
    lateinit var bot: Bot
    @JvmStatic
    fun main(args: Array<String>) {
        bot = Bot()
        bot.init()
    }
}