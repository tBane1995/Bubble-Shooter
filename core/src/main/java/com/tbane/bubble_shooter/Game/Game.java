package com.tbane.bubble_shooter.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tbane.bubble_shooter.AssetsManager;
import com.tbane.bubble_shooter.GUI.Button;
import com.tbane.bubble_shooter.GUI.Font;
import com.tbane.bubble_shooter.Renderer;
import com.tbane.bubble_shooter.Time;
import com.tbane.bubble_shooter.Views.Layout;
import com.tbane.bubble_shooter.Views.LayoutsManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Game extends Layout {

    private final Button _backBtn;
    private int _bubblesLinesAtStart;
    private int _bubblesInLine;
    public static ArrayList<Bubble> _bubbles;
    public static ArrayList<ShootedBubble> _shootedBubbles;
    public static ArrayList<AnimatedPositioningBubble> _animatedPositioningBubbles;
    // public static ArrayList<> _destroyingBubbles;
    // public static ArrayList <> _fallingBubbles;
    private float _lastMoveDownBubblesTime;
    private float _moveDownBubblesDuration;
    private Gun _gun;
    private float _gameStartTime;
    private float _gameTime;
    private int _points;


    enum Direction {NE, E, SE, SW, W, NW}

    public Game() {
        super();

        _backBtn = new Button(
            AssetsManager.getTexture("tex/backButtonNormal.png"),
            AssetsManager.getTexture("tex/backButtonHover.png"),
            AssetsManager.getTexture("tex/backButtonPressed.png"),
            32, Renderer.VIRTUAL_HEIGHT - 96 - 32, 96, 96
        );

        _backBtn.onclick_func = LayoutsManager::pop_back;


        createBubbles();
        coloringBubbles();

        _shootedBubbles = new ArrayList<>();
        _animatedPositioningBubbles = new ArrayList<>();

        _lastMoveDownBubblesTime = Time.currentTime;
        _moveDownBubblesDuration = 20;

        _gun = new Gun();

        _gameStartTime = Time.currentTime;
        _gameTime = 0;
        _points = 0;
    }

    public void createBubbles() {


        Bubble._radius = 32;

        _bubblesLinesAtStart = 8;
        _bubblesInLine = Renderer.VIRTUAL_WIDTH / ((int) Bubble._radius * 2);
        _bubblesInLine -= 1;
        int marginLeft = (int) ((float) (Renderer.VIRTUAL_WIDTH - _bubblesInLine * Bubble._radius * 2)/4);

        Bubble._marginLeft = marginLeft;

        _bubbles = new ArrayList<>();
        for (int y = 0; y < _bubblesLinesAtStart; y++) {
            for (int x = 0; x < _bubblesInLine; x++) {
                int color = (int) (Math.random() * 6);
                _bubbles.add(new Bubble(x, y, color));
            }
        }
    }

    private Bubble getBubble(int x, int y) {
        for (Bubble bubble : _bubbles) {
            if (bubble.hasCoords(x, y))
                return bubble;
        }
        return null;
    }

    private Bubble getNeighbour(Bubble bubble, Direction direction) {

        if (bubble == null)
            return null;

        Bubble b = null;
        switch (direction) {
            case NE:
                b = getBubble(bubble._coordX + bubble._coordY % 2, bubble._coordY - 1);
                break;
            case E:
                b = getBubble(bubble._coordX + 1, bubble._coordY);
                break;
            case SE:
                b = getBubble(bubble._coordX + bubble._coordY % 2, bubble._coordY + 1);
                break;
            case SW:
                b = getBubble(bubble._coordX - 1 + bubble._coordY % 2, bubble._coordY + 1);
                break;
            case W:
                b = getBubble(bubble._coordX - 1, bubble._coordY);
                break;
            case NW:
                b = getBubble(bubble._coordX - 1 + bubble._coordY % 2, bubble._coordY - 1);
                break;
            // default is NW
            default:
                b = getBubble(bubble._coordX + bubble._coordY % 2, bubble._coordY - 1);
                break;
        }

        return b;
    }

    private void coloringBubbles(Bubble bubble, int color, int iter) {

        if (bubble == null)
            return;

        if (iter <= 0)
            return;

        if (bubble._color == color)
            return;

        bubble.setColor(color);

        for (Direction dir : Direction.values()) {
            if (Math.random() < 0.6f) {
                coloringBubbles(
                    getNeighbour(bubble, dir),
                    color,
                    iter - 1
                );
            }
        }
    }

    private int generateColor(Bubble bubble) {
        ArrayList<Integer> neighborColors = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            Bubble ngbr = getNeighbour(bubble, dir);
            if (ngbr != null) {
                neighborColors.add(ngbr._color);
            }
        }

        ArrayList<Integer> possibleColors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (!neighborColors.contains(i)) {
                possibleColors.add(i);
            }
        }

        if (possibleColors.isEmpty()) {
            return (int) (Math.random() * 6);
        }

        int index = (int) (Math.random() * possibleColors.size());
        return possibleColors.get(index);
    }

    private void coloringBubbles() {
        for (int i = 0; i < 64; i++) {
            int y = (int) (Math.random() * _bubblesLinesAtStart);
            int x = (int) (Math.random() * _bubblesInLine);
            Bubble bubble = getBubble(x, y);
            int color = generateColor(bubble);
            int iter = (int) (Math.random() * 3) + 1;
            coloringBubbles(bubble, color, iter);
        }
    }

    public int countBubbleGroup(Bubble start) {

        ArrayList<Bubble> visited = new ArrayList<>();
        ArrayList<Bubble> toCheck = new ArrayList<>();

        toCheck.add(start);
        visited.add(start);

        int count = 0;

        while (!toCheck.isEmpty()) {

            Bubble b = toCheck.remove(0);
            count++;

            for (Direction dir : Direction.values()) {
                Bubble n = getNeighbour(b, dir);

                if (n == null) continue;
                if (n._color != start._color) continue;
                if (visited.contains(n)) continue;

                visited.add(n);
                toCheck.add(n);
            }
        }

        return count;
    }

    public void removeBubbleGroup(Bubble start) {

        ArrayList<Bubble> visited = new ArrayList<>();
        ArrayList<Bubble> toCheck = new ArrayList<>();

        toCheck.add(start);
        visited.add(start);

        while (!toCheck.isEmpty()) {

            Bubble b = toCheck.remove(0);

            for (Direction dir : Direction.values()) {
                Bubble n = getNeighbour(b, dir);

                if (n == null) continue;
                if (n._color != start._color) continue;
                if (visited.contains(n)) continue;

                visited.add(n);
                toCheck.add(n);
            }
        }

        for (Bubble bubble : visited) {
            _bubbles.remove(bubble);
        }

    }

    private void removeFloatingBubbles() {

        ArrayList<Bubble> connectedToTop = new ArrayList<>();
        ArrayList<Bubble> bubblesToCheck = new ArrayList<>();

        for (Bubble bubble : _bubbles) {
            if (bubble._coordY == 0) {
                connectedToTop.add(bubble);
                bubblesToCheck.add(bubble);
            }
        }

        int index = 0;
        while (index < bubblesToCheck.size()) {
            Bubble current = bubblesToCheck.get(index++);

            for (Direction dir : Direction.values()) {
                Bubble neighbor = getNeighbour(current, dir);

                if (neighbor != null && !connectedToTop.contains(neighbor)) {
                    connectedToTop.add(neighbor);
                    bubblesToCheck.add(neighbor);
                }
            }
        }

        _bubbles.removeIf(bubble -> !connectedToTop.contains(bubble));
    }

    private void generateNewBubblesOnFirstLine() {
        for (int x = 0; x < _bubblesInLine; x++) {

            int color = (int) (Math.random() * 6);
            Bubble bubble = new Bubble(x, 0, color);
            _bubbles.add(bubble);

            if(Math.random() < 0.3f) {
                ArrayList<Integer> ngbrsColor = new ArrayList<>();
                Bubble ngbrSW = getNeighbour(bubble, Direction.SW);
                Bubble ngbrSE = getNeighbour(bubble, Direction.SE);

                if(ngbrSW != null)
                    ngbrsColor.add(ngbrSW._color);
                if(ngbrSE != null)
                    ngbrsColor.add(ngbrSE._color);

                if(ngbrsColor.size() > 0)
                    bubble.setColor(ngbrsColor.get((int)(Math.random()*(ngbrsColor.size()))));
            }

        }
    }
    private void moveBubblesDown() {
        for(int i=_bubbles.size()-1;i>=0; i--){
            Vector2 coords = new Vector2(_bubbles.get(i)._coordX, _bubbles.get(i)._coordY+1);
            _bubbles.get(i).setCoords(coords);
            _bubbles.get(i).setPositionFromCoords(coords);
        }
    }

    private void shootedBubblesUpdate() {
        for (int i = _shootedBubbles.size() - 1; i >= 0; i--) {
            ShootedBubble bubble = _shootedBubbles.get(i);
            Vector2 position = bubble._bubble.getPositionWithVariation();
            boolean bubbleCollided = false;

            if (position.y > Renderer.VIRTUAL_HEIGHT - 160 - Bubble._radius)
                bubbleCollided = true;

            if (!bubbleCollided) {
                for (Bubble staticBubble : _bubbles) {
                    float xx = position.x - staticBubble.getPositionWithVariation().x;
                    float yy = position.y - staticBubble.getPositionWithVariation().y;

                    if (xx * xx + yy * yy <= 4.0f * Bubble._radius * Bubble._radius) {
                        bubbleCollided = true;
                        break;
                    }
                }
            }

            if (!bubbleCollided) {
                for (AnimatedPositioningBubble animBubble : _animatedPositioningBubbles) {
                    float xx = position.x - animBubble._bubble.getPosition().x;
                    float yy = position.y - animBubble._bubble.getPosition().y;

                    if (xx * xx + yy * yy <= 4.0f * Bubble._radius * Bubble._radius) {
                        bubbleCollided = true;
                        break;
                    }
                }
            }

            if (bubbleCollided) {
                Bubble b = bubble._bubble;
                Vector2 coords = Bubble.calculateCoordsFromPosition(b.getPosition());
                if(coords.x < 0) coords.x = 0;
                if(coords.x >= _bubblesInLine) coords.x = _bubblesInLine-1;
                b.setCoords(coords);
                _animatedPositioningBubbles.add(
                    new AnimatedPositioningBubble(b,
                        b.getPosition(),
                        Bubble.calcPositionFromCoords((int) coords.x, (int) coords.y)
                    ));
                _shootedBubbles.remove(i);
            } else {
                bubble.calcPosition();
            }
        }
    }

    public void animatedPositioningBubblesUpdate() {
        for (int i = _animatedPositioningBubbles.size() - 1; i >= 0; i--) {
            AnimatedPositioningBubble bubble = _animatedPositioningBubbles.get(i);
            bubble.calcPosition();

            Vector2 curPos = bubble._bubble.getPosition();
            Vector2 endPos = Bubble.calcPositionFromCoords(bubble._bubble._coordX, bubble._bubble._coordY);

            float x = curPos.x - endPos.x;
            float y = curPos.y - endPos.y;

            if (x * x + y * y <= 1.0f) {
                Bubble b = bubble._bubble;
                Game._bubbles.add(b);
                _animatedPositioningBubbles.remove(i);

                int countSameBubbles = countBubbleGroup(bubble._bubble);

                if (countSameBubbles > 2)
                    removeBubbleGroup(bubble._bubble);

                removeFloatingBubbles();
            }
        }
    }

    @Override
    public void handleEvents() {

        _backBtn.handleEvents();
        _gun.handleEvents();

    }

    @Override
    public void update() {

        _backBtn.update();

        _gun.update();

        shootedBubblesUpdate();
        animatedPositioningBubblesUpdate();

        if(_animatedPositioningBubbles.isEmpty() && _shootedBubbles.isEmpty()){
            if(Time.currentTime - _lastMoveDownBubblesTime > _moveDownBubblesDuration){
                _lastMoveDownBubblesTime = Time.currentTime;
                _moveDownBubblesDuration -= 0.5f;

                if(_moveDownBubblesDuration < 5)
                    _moveDownBubblesDuration = 5;

                moveBubblesDown();
                generateNewBubblesOnFirstLine();
            }
        }

        for(Bubble buble : _bubbles){
            buble.calcPositionWithVariation();
        }

        _gameTime = Time.currentTime - _gameStartTime;

    }

    @Override
    public void draw() {

        Texture backgroundTexture = AssetsManager.getTexture("tex/gameBoard.png");
        if (backgroundTexture != null) {
            Sprite background = new Sprite(backgroundTexture);
            background.setPosition(0, 0);
            background.draw(Renderer.spriteBatch);
        }

        Texture topPanelTexture = AssetsManager.getTexture("tex/topPanel.png");
        if (topPanelTexture != null) {
            Sprite topPanel = new Sprite(topPanelTexture);
            topPanel.setPosition(0, Renderer.VIRTUAL_HEIGHT - topPanel.getHeight());
            topPanel.draw(Renderer.spriteBatch);
        }

        _backBtn.draw();

        // Top Texts
        GlyphLayout layout = new GlyphLayout();
        float x = 256;
        float y = Renderer.VIRTUAL_HEIGHT - 36;
        float dx = 256;

        layout.setText(Font.gameTopTextBigFont, "Time");
        float timeTopTextWidth1 = layout.width;
        Font.gameTopTextBigFont.draw(Renderer.spriteBatch, "Time", x-timeTopTextWidth1/2, y);

        String timeText = Time.generateTimeString(_gameTime);
        layout.setText(Font.gameTopTextSmallFont, timeText);
        float timeBottomTextWidth1 = layout.width;
        Font.gameTopTextSmallFont.draw(Renderer.spriteBatch, timeText, x-timeBottomTextWidth1/2, y-64);

        layout.setText(Font.gameTopTextBigFont, "Points");
        float timeTopTextWidth2 = layout.width;
        Font.gameTopTextBigFont.draw(Renderer.spriteBatch, "Points", x+dx-timeTopTextWidth2/2, y);

        layout.setText(Font.gameTopTextSmallFont, Integer.toString(_points));
        float timeBottomTextWidth2 = layout.width;
        Font.gameTopTextSmallFont.draw(Renderer.spriteBatch, Integer.toString(_points), x+dx-timeBottomTextWidth2/2, y-64);

        _gun.drawBackward();

        Renderer.end2D();
        Renderer.beginShapeRenderer();

        for (Bubble bubble : _bubbles)
            bubble.drawColor();
        for (ShootedBubble bubble : _shootedBubbles)
            bubble._bubble.drawColor();
        for (AnimatedPositioningBubble bubble : _animatedPositioningBubbles)
            bubble._bubble.drawColor();
        _gun.drawColorOfAmmo();

        Renderer.endShapeRenderer();
        Renderer.begin2D();

        for (Bubble bubble : _bubbles)
            bubble.drawBubble();
        for (ShootedBubble bubble : _shootedBubbles)
            bubble._bubble.drawBubble();
        for (AnimatedPositioningBubble bubble : _animatedPositioningBubbles)
            bubble._bubble.drawBubble();
        _gun.drawBubbleOfAmmo();
        _gun.drawForward();

        Renderer.end2D();
        Renderer.beginShapeRenderer();

        _gun.drawColorOfBubbleOnGun();

        Renderer.endShapeRenderer();
        Renderer.begin2D();

        _gun.drawBubbleOfBubbleOnGun();

        // ads panel
        Texture bottomPanelTexture = AssetsManager.getTexture("tex/bottomPanel.png");
        if (bottomPanelTexture != null) {
            Sprite adsPanel = new Sprite(bottomPanelTexture);
            adsPanel.setPosition(0, 0);
            adsPanel.draw(Renderer.spriteBatch);
        }


    }
}
