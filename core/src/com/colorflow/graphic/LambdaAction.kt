package com.colorflow.graphic

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction

fun laction(cb: () -> Unit): Action {
    val action = RunnableAction()
    action.runnable = Runnable(cb)
    return action
}