package com.echopath.slackbot

import com.github.seratch.jslack.api.model.Action
import com.github.seratch.jslack.api.model.Attachment
import com.github.seratch.jslack.api.model.User

/**
 * @author Kyle Staschke
 */

class BotService {

    fun sendRunningLatePrompt(user: User) {
        val directMsgChannel = Bot.Api.methods().imOpen {
            it.token(Bot.USER_TOKEN)
                .returnIm(true)
                .user(user.id)
        }

        val msg = Bot.Api.methods().chatPostMessage {
            it.token(Bot.USER_TOKEN)
                .channel(directMsgChannel.channel.id)
                .text("You have not checked in this morning.")
                .attachments(mutableListOf(
                    Attachment.builder()
                        .text("Are you running late?")
                        .fallback("Sorry, I couldn't process your response.")
                        .callbackId("running_late")
                        .color("#3AA3E3")
                        .actions(mutableListOf(
                            Action.builder()
                                .name("no")
                                .text("Already In")
                                .type(Action.Type.BUTTON)
                                .style("primary")
                                .value("no")
                                .build(),
                            Action.builder()
                                .name("yes")
                                .text("Running Late")
                                .type(Action.Type.BUTTON)
                                .style("danger")
                                .value("yes")
                                .build(),
                            Action.builder()
                                .name("minutes")
                                .text("Minutes?")
                                .type(Action.Type.SELECT)
                                .options(mutableListOf(
                                    actionOption("5 Minutes", "5min"),
                                    actionOption("10 Minutes", "10min"),
                                    actionOption("15 Minutes", "15min"),
                                    actionOption("30 Minutes", "30min"),
                                    actionOption("Undetermined", "unknown")
                                ))
                                .build()
                        ))
                        .build()
                ))
        }

        println(msg)
    }

    private fun actionOption(text: String, value: String): Action.Option {
        val opt = Action.Option()
        opt.text = text
        opt.value = value
        return opt
    }
}