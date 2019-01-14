package com.colorflow.play.ring

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.colorflow.play.entity.dot.Color
import com.colorflow.utility.Position

import org.w3c.dom.Document
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class Ring(ringId: String) : Actor(), Disposable {

    val id: String
    private var name: String? = null
    var cost: Int = 0
        private set
    private var texture: Texture? = null
    val circle: Circle
    private var radius: Float = 0.toFloat()
    var sensibility: Float = 0.toFloat()
        private set
    private var rotation = 0f
    private val listener: RingListener

    init {
        val ring = Gdx.files.local("rings/$ringId")
        this.id = ring.name()
        loadFromXML(ring)
        setBounds(Position.widthScreen / 2 - texture!!.width / 2, Position.heightScreen / 2 - texture!!.height / 2,
                texture!!.width.toFloat(), texture!!.height.toFloat())
        this.circle = Circle(Position.center.x, Position.center.y, radius)
        this.listener = SideTapListener(this)
    }

    private fun loadFromXML(file: FileHandle) {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val doc: Document
        if (!file.exists()) {
            throw IllegalArgumentException()
        }
        try {
            val dBuilder = dbFactory.newDocumentBuilder()
            doc = dBuilder.parse(file.file())
        } catch (e: Exception) {
            throw RuntimeException()
        }

        doc.normalizeDocument()
        var look: Node? = null
        var collision: Node? = null
        val nodeList = doc.firstChild.childNodes
        for (i in 0 until nodeList.length) {
            if (nodeList.item(i).nodeName == "look") {
                look = nodeList.item(i)
            }
            if (nodeList.item(i).nodeName == "collision") {
                collision = nodeList.item(i)
            }
        }
        this.name = doc.firstChild.attributes.getNamedItem("name").nodeValue
        this.cost = Integer.parseInt(doc.firstChild.attributes.getNamedItem("cost").nodeValue)
        this.texture = Texture(Gdx.files.local("rings/" + look!!.attributes.getNamedItem("img").nodeValue))
        this.radius = java.lang.Float.parseFloat(look.attributes.getNamedItem("radius").nodeValue)
        this.sensibility = java.lang.Float.parseFloat(look.attributes.getNamedItem("sensibility").nodeValue)
    }

    override fun draw(batch: Batch?, alpha: Float) {
        batch!!.draw(texture, x, y, width / 2, height / 2, width, height,
                scaleX, scaleY, getRotation(), 0, 0, width.toInt(), height.toInt(), false, true)
    }

    override fun act(delta: Float) {
        listener.onRingAct()
        super.act(delta)
    }

    override fun dispose() {
        texture!!.dispose()
    }

    fun getColorFor(angle: Float): Color {
        val range_angle = Position.Radial.regulateAngle(angle + getRotation())
        if (range_angle in 0.0..60.0) {
            return Color.CYAN
        } else if (range_angle in 60.0..120.0) {
            return Color.RED
        } else if (range_angle in 120.0..180.0) {
            return Color.YELLOW
        } else if (range_angle in 180.0..240.0) {
            return Color.GREEN
        } else if (range_angle in 240.0..300.0) {
            return Color.MAGENTA
        } else if (range_angle in 300.0..360.0) {
            return Color.BLUE
        }
        return Color.BLUE
    }

    override fun getRotation(): Float {
        return rotation
    }

    override fun setRotation(rotation: Float) {
        this.rotation = Position.Radial.regulateAngle(rotation)
    }

    override fun rotateBy(amountInDegrees: Float) {
        setRotation(getRotation() + amountInDegrees)
    }

    override fun getName(): String? {
        return name
    }

    override fun setName(name: String) {
        this.name = name
    }

    override fun setScale(scaleXY: Float) {
        super.setScale(scaleXY)
        radius *= scaleXY
    }

    fun getListener(): InputProcessor {
        return listener
    }
}