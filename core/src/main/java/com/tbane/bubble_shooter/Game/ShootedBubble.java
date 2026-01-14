package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.math.Vector2;
import com.tbane.bubble_shooter.Renderer;

public class ShootedBubble {

    Bubble _bubble;
    float _angleInDegrees;


    public ShootedBubble(Bubble bubble, Vector2 startPosition, float angleInDegrees){
        _bubble = bubble;
        _angleInDegrees = angleInDegrees;
        _bubble.setPosition(startPosition);
    }

    public void calcPosition(){
        float speed = 8.0f;
        Vector2 position = _bubble.getPosition();
        position.x -= speed * Math.sin(Math.toRadians(_angleInDegrees));
        position.y += speed * Math.cos(Math.toRadians(_angleInDegrees));

        if(position.x - Bubble._radius - 10 < 0){
            _angleInDegrees = 360 - _angleInDegrees;
            position.x = Bubble._radius + 10;
        }

        if(position.x + Bubble._radius +10 > Renderer.VIRTUAL_WIDTH-1){
            _angleInDegrees = 360 - _angleInDegrees;
            position.x = Renderer.VIRTUAL_WIDTH-1 - Bubble._radius - 10;
        }


        _bubble.setPosition(position);
    }


}
