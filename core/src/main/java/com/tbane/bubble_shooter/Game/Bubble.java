package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tbane.bubble_shooter.AssetsManager;
import com.tbane.bubble_shooter.Renderer;
import com.tbane.bubble_shooter.Time;

public class Bubble {

    int _coordX;
    int _coordY;
    static float _radius;
    static float _marginLeft;
    Vector2 _position;

    int _color;
    float _timer;

    Vector2 positionWithVariation;

    public Bubble(int x, int y, int color){
        _coordX = x;
        _coordY = y;
        _color = color;

        setPositionFromCoords(_coordX, _coordY);

        _timer = (float)(Math.random() * 6);
    }

    public Vector3 getColor(){
        Vector3 color;

        float c = 0.65f;    // first color value (main)
        float a = 0.15f;    // second color value (background)

        switch (_color){
            case 0: color = new Vector3(c,a,a); break;
            case 1: color = new Vector3(a,c,a); break;
            case 2: color = new Vector3(a,a,c); break;
            case 3: color = new Vector3(c,c,a); break;
            case 4: color = new Vector3(a,c,c); break;
            case 5: color = new Vector3(c,a,c); break;
            default: color = new Vector3(0,0,0); break;
        }

        return color;
    }
    public void setCoords(int coordX, int coordY){
        _coordX = coordX;
        _coordY = coordY;
    }

    public void setCoords(Vector2 coords){
        _coordX = (int)coords.x;
        _coordY = (int)coords.y;
    }
    public void setColor(int color){
        _color = color;
    }

    public boolean hasCoords(int x, int y){
        return x == _coordX && y == _coordY;
    }

    public static Vector2 calcPositionFromCoords(int coordX, int coordY){
        Vector2 position = new Vector2();
        position.x = _marginLeft + (coordX + coordY * 0.5f - coordY / 2) * 2 * _radius + _radius;
        position.y = Renderer.VIRTUAL_HEIGHT - 160 - 2*_radius - coordY * 2 * _radius + _radius;
        return position;
    }

    public static Vector2 calculateCoordsFromPosition(Vector2 position) {

        int coordY = Math.round(
            (Renderer.VIRTUAL_HEIGHT - 160 - _radius - position.y)
                / (2f * _radius)
        );

        float rowOffset = (coordY % 2) * 0.5f;

        int coordX = (int)Math.round(
            (position.x - _marginLeft - _radius)
                / (2f * _radius)
                - rowOffset
        );

        return new Vector2(coordX, coordY);
    }
    public void setPositionFromCoords(int coordX, int coordY){
        _position = calcPositionFromCoords(coordX, coordY);
    }

    public void setPosition(Vector2 newPosition){
        _position = newPosition;
        positionWithVariation = _position;
    }

    public Vector2 getPosition() {
        return new Vector2(_position);
    }

    public static Vector2 getPosition(int coordX, int coordY){
        Vector2 position = calcPositionFromCoords(coordX, coordY);
        return position;
    }
    public Vector2 getPositionWithVariation() {
        Vector2 positionWithVariation = new Vector2(_position);
        float speed = 3.0f;
        positionWithVariation.x += (float)Math.sin(speed*Time.currentTime-_timer) * 3.0f;
        positionWithVariation.y += (float)Math.cos(speed*Time.currentTime-_timer) * 3.0f;
        return positionWithVariation;
    }

    public void drawColor() {
        Vector3 color = getColor();
        float r = color.x;
        float g = color.y;
        float b = color.z;

        float radius = _radius - 5;
        Vector2 positionWithVariation = getPositionWithVariation();

        Renderer.shapeRenderer.setColor(r, g, b, 1.f);
        Renderer.shapeRenderer.circle(positionWithVariation.x, positionWithVariation.y, radius);

        Renderer.shapeRenderer.setColor(r+0.25f, g+0.25f, b+0.25f, 1.f);
        Renderer.shapeRenderer.circle(positionWithVariation.x - radius*4.0f/11.0f, positionWithVariation.y+radius*4.0f/11.0f, radius/4);

    }

    public void drawBubble() {

        Texture bubbleTexture = AssetsManager.getTexture("tex/bubble.png");
        if(bubbleTexture != null){

            Vector2 positionWithVariation = getPositionWithVariation();

            Sprite bubble = new Sprite(bubbleTexture);
            bubble.setSize(_radius*2, _radius*2);
            bubble.setOriginCenter();
            bubble.setCenter(positionWithVariation.x, positionWithVariation.y);
            bubble.draw(Renderer.spriteBatch);
        }
    }

}
