package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.tbane.bubble_shooter.AssetsManager;
import com.tbane.bubble_shooter.MyInput.MyInput;
import com.tbane.bubble_shooter.Renderer;

import java.util.ArrayList;
import java.util.Vector;

public class Gun {

    private Vector2 _position;
    private float _angleInDegrees;
    private ArrayList<Vector2> _laserPoints;
    private ArrayList<Bubble> _ammo;


    public Gun(){
        _position = new Vector2(Renderer.VIRTUAL_WIDTH/2.f, 320);
        _angleInDegrees = 0;

        _laserPoints = new ArrayList<>();

        _ammo = new ArrayList<>();
        loadAmmo();
        loadAmmo();
        loadAmmo();
    }

    private void loadAmmo(){

        int color = (int)(Math.random()*6);
        _ammo.add(new Bubble(-1,-1, color));


        for(int i=0;i<_ammo.size();i++){
            float x = Bubble._radius*2.0f*i;
            _ammo.get(i).setPosition(new Vector2(_position.x + 128 + x,_position.y));

        }
    }
    private Vector2 getGunTipPosition() {
        float offset = 96-24; // od origin do końca lufy
        float rad = (float)Math.toRadians(_angleInDegrees);
        return new Vector2(
            _position.x - offset * (float)Math.sin(rad),  // cos dla X
            _position.y + offset * (float)Math.cos(rad)   // sin dla Y
        );
    }

    private boolean pointInBubble(Vector2 point){
        for(Bubble bubble : Game._bubbles){
            float x = bubble.getPosition().x - point.x;
            float y = bubble.getPosition().y - point.y;

            if( x*x + y*y < 4*Bubble._radius*Bubble._radius){
                return true;
            }
        }

        return false;
    }
    private void generateLaserPoints() {

        _laserPoints.clear();

        Vector2 point = getGunTipPosition();
        float angle = _angleInDegrees;

        float distBetweenPoints = 48f;
        float traveled = 0f;

        float leftWall  = 10 + Bubble._radius;
        float rightWall = Renderer.VIRTUAL_WIDTH - 10 - Bubble._radius;

        while (true) {

            float dirX = -(float)Math.sin(Math.toRadians(angle));
            float dirY =  (float)Math.cos(Math.toRadians(angle));

            float tX = Float.MAX_VALUE;
            boolean reflectX = false;
            if (dirX < 0) { tX = (leftWall - point.x) / dirX; reflectX = true; }
            else if (dirX > 0) { tX = (rightWall - point.x) / dirX; reflectX = true; }

            float tY = Float.MAX_VALUE;
            if (dirY > 0) tY = (Renderer.VIRTUAL_HEIGHT - 160 - point.y) / dirY;

            float tMax = Math.min(tX, tY);
            boolean reflect = (tMax == tX) && reflectX;

            Vector2 next = new Vector2(point.x + dirX * tMax, point.y + dirY * tMax);

            float segmentLength = point.dst(next);
            float remaining = distBetweenPoints - traveled;

            while (segmentLength > 0f) {

                if (segmentLength >= remaining) {

                    float ratio = remaining / segmentLength;
                    float px = point.x + (next.x - point.x) * ratio;
                    float py = point.y + (next.y - point.y) * ratio;

                    if (pointInBubble(new Vector2(px, py))) {
                        return;
                    }

                    _laserPoints.add(new Vector2(px, py));

                    point = new Vector2(px, py);
                    segmentLength = point.dst(next);
                    remaining = distBetweenPoints;

                } else {
                    traveled = remaining - segmentLength;
                    point = next;
                    break;
                }
            }

            if (reflect) {
                angle = 360f - angle;
            }

            if (point.y >= Renderer.VIRTUAL_HEIGHT - 160)
                break;
        }
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

            generateLaserPoints();

        }

        if(MyInput.processor.isTouchUp()){

            //Game._bubbles.add(bubble);
            ShootedBubble mbubble = new ShootedBubble(_ammo.get(0), getGunTipPosition(), _angleInDegrees);
            _ammo.remove(0);
            Game._shootedBubbles.add(mbubble);

            loadAmmo();

            _laserPoints.clear();
        }
    }

    public void update() {

    }

    public void drawColorOfAmmo(){
        for(Bubble ammo : _ammo)
            ammo.drawColor();
    }

    public void drawBubbleOfAmmo() {
        for(Bubble ammo : _ammo)
            ammo.drawBubble();
    }

    public void draw(){

        Texture laserPointTexture = AssetsManager.getTexture("tex/laser.png");
        if(laserPointTexture != null){
            for(Vector2 point : _laserPoints){
                Sprite laser = new Sprite(laserPointTexture);
                laser.setOriginCenter();
                laser.setScale(0.25f);
                laser.setCenter(point.x, point.y);
                laser.draw(Renderer.spriteBatch);
            }
        }

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
