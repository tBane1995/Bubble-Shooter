package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.math.Vector2;
import com.tbane.bubble_shooter.Time;

public class AnimatedPositioningBubble {
    public Bubble _bubble;
    public static float _animationTime = 0.125f;
    public float _startAnimationTime;
    private Vector2 _startPosition;
    private Vector2 _endPosition;

    public AnimatedPositioningBubble(Bubble bubble, Vector2 startPosition, Vector2 endPosition){
        _bubble = bubble;
        _startPosition = startPosition;
        _endPosition = endPosition;
        _startAnimationTime = Time.currentTime;
    }
    public void calcPosition(){
        float dt = Time.currentTime - _startAnimationTime;
        float t = dt / _animationTime;
        if (t > 1f) t = 1f;

        Vector2 newPosition = new Vector2(
            _startPosition.x + (_endPosition.x - _startPosition.x) * t,
            _startPosition.y + (_endPosition.y - _startPosition.y) * t
        );

        _bubble.setPosition(newPosition);
    }
}
