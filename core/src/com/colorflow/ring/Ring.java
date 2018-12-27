package com.colorflow.ring;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.colorflow.entity.dot.Color;
import com.colorflow.utility.Position;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.colorflow.utility.Position.Radial.regulateAngle;

public class Ring extends Actor implements Disposable {

    private String ID;
    private String name;
    private int cost;
    private Texture texture;
    private Circle circle;
    private float radius;
    private float sensibility;
    private float rotation = 0;
    private RingListener listener;

    public Ring(String ringId) {
        FileHandle ring = Gdx.files.local("rings/" + ringId);
        this.ID = ring.name();
        loadFromXML(ring);
        setBounds(Position.getWidthScreen() / 2 - texture.getWidth() / 2, Position.getHeightScreen() / 2 - texture.getHeight() / 2,
                texture.getWidth(), texture.getHeight());
        this.circle = new Circle(Position.getCenter().getX(), Position.getCenter().getY(), radius);
        this.listener = new SideTapListener(this);
    }

    private void loadFromXML(FileHandle file) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc;
        if (!file.exists()) {
            throw new IllegalArgumentException();
        }
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file.file());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        doc.normalizeDocument();
        Node look = null, collision = null;
        NodeList nodeList = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("look")) {
                look = nodeList.item(i);
            }
            if (nodeList.item(i).getNodeName().equals("collision")) {
                collision = nodeList.item(i);
            }
        }
        this.name = doc.getFirstChild().getAttributes().getNamedItem("name").getNodeValue();
        this.cost = Integer.parseInt(doc.getFirstChild().getAttributes().getNamedItem("cost").getNodeValue());
        this.texture = new Texture(Gdx.files.local("rings/" + look.getAttributes().getNamedItem("img").getNodeValue()));
        this.radius = Float.parseFloat(look.getAttributes().getNamedItem("radius").getNodeValue());
        this.sensibility = Float.parseFloat(look.getAttributes().getNamedItem("sensibility").getNodeValue());
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(texture, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation(), 0, 0, (int) getWidth(), (int) getHeight(), false, true);
    }

    @Override
    public void act(float delta) {
        listener.onRingAct();
        super.act(delta);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    public Circle getCircle() {
        return circle;
    }

    public Color getColorFor(float angle) {
        angle = regulateAngle(angle + getRotation());
        if (angle <= 60 && angle >= 0) {
            return Color.CYAN;
        } else if (angle <= 120 && angle > 60) {
            return Color.RED;
        } else if (angle <= 180 && angle > 120) {
            return Color.YELLOW;
        } else if (angle <= 240 && angle > 180) {
            return Color.GREEN;
        } else if (angle <= 300 && angle > 240) {
            return Color.MAGENTA;
        } else if (angle < 360 && angle > 300) {
            return Color.BLUE;
        }
        throw new IllegalStateException();
    }

    public float getSensibility() {
        return sensibility;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = regulateAngle(rotation);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        setRotation(getRotation() + amountInDegrees);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        radius *= scaleXY;
    }

    public int getCost() {
        return cost;
    }

    public String getID() {
        return ID;
    }

    public InputProcessor getListener() {
        return listener;
    }
}