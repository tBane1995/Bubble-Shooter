package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.tbane.bubble_shooter.AssetsManager;
import com.tbane.bubble_shooter.MyInput.MyInput;
import com.tbane.bubble_shooter.Renderer;

public class Gun {

    private Vector2 _position;
    private float _angleInDegrees;

    public Gun(){
        _position = new Vector2(Renderer.VIRTUAL_WIDTH/2.f, 320);
        _angleInDegrees = 0;
    }

    private Vector2 getGunTipPosition() {
        float offset = 96-24; // od origin do końca lufy
        float rad = (float)Math.toRadians(_angleInDegrees);
        return new Vector2(
            _position.x - offset * (float)Math.sin(rad),  // cos dla X
            _position.y + offset * (float)Math.cos(rad)   // sin dla Y
        );
    }

    public void handleEvents() {

        if(MyInput.processor.isTouchDown() || MyInput.processor.isTouchMoved()){

            Vector2 curPos = MyInput.processor.getTouchPosition();

            float dy = _position.y-curPos.y;
            float dx = _position.x-curPos.x;

            _angleInDegrees = (float)Math.atan2(dy,dx);
            _angleInDegrees = (float)Math.toDegrees(_angleInDegrees);
            _angleInDegrees += 90;

            //System.out.println(_angleInDegrees);

            while (_angleInDegrees > 180) _angleInDegrees -= 360;
            while (_angleInDegrees < -180) _angleInDegrees += 360;

            // limiting to [-angle, angle]
            float angle = 75;
            _angleInDegrees = Math.max(-angle, Math.min(angle, _angleInDegrees));


        }

        if(MyInput.processor.isTouchUp()){

            Bubble bubble = new Bubble(-1,-1, -1);
            //Game._bubbles.add(bubble);
            ShootedBubble mbubble = new ShootedBubble(bubble, getGunTipPosition(), _angleInDegrees);
            Game._shootedBubbles.add(mbubble);
        }
    }

    public void update() {

    }

    public void draw(){
        Texture gunTexture = AssetsManager.getTexture("tex/gun.png");
        if(gunTexture != null){
            Sprite gun = new Sprite(gunTexture);
            gun.setOrigin(24,24);
            gun.setRotation(_angleInDegrees);
            gun.setPosition(_position.x - gun.getOriginX(), _position.y - gun.getOriginY());
            gun.draw(Renderer.spriteBatch);
        }
    }
}
