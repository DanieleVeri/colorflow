package com.colorflow.engine.entity

interface IMotionCoordinator {
    var dot_velocity: Float
    var bonus_velocity: Float
    var path_type: PathType

    enum class PathType {RADIAL, SPIRAL}
}