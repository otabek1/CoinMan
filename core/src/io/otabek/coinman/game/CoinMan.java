package io.otabek.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Rectangle manRectangle;
    Texture dizzyMan;

    int score = 0;
    BitmapFont font;
    int gameState = 0;

    int manState = 0;
    int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int yPosition;
    Texture coin;
    ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    int coinCount = 0;
    Random mRandom;

    ArrayList<Integer> bombXs = new ArrayList<Integer>();
    ArrayList<Integer> bombYs = new ArrayList<Integer>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
    int bombCount = 0;
    Texture bomb;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        yPosition = Gdx.graphics.getHeight() / 2 - man[manState].getHeight() / 2;
        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        dizzyMan = new Texture("dizzy-1.png");
        mRandom = new Random();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
    }

    public void setBomb() {
        float height = mRandom.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    public void makeCoin() {

        float height = mRandom.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //BOMBS
            if (bombCount < 200) {
                bombCount++;
            } else {
                bombCount = 0;
                setBomb();
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 5);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //COINS
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 6);
                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));

            }

            if (Gdx.input.justTouched()) {
                velocity = -10;
            }

            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            velocity += gravity;
            yPosition -= velocity;
            if (yPosition <= 0) {
                yPosition = 0;
            }
        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }

        } else if (gameState == 2) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
            yPosition = Gdx.graphics.getHeight() / 2 - man[manState].getHeight() / 2;
            score = 0;
            coinYs.clear();
            coinXs.clear();
            coinRectangles.clear();
            bombRectangles.clear();
            bombXs.clear();
            bombYs.clear();

        }

        if (gameState == 2) {
            batch.draw(dizzyMan, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, yPosition);
        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, yPosition);
        }

        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2, yPosition, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                score++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);

            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                Gdx.app.log("Bomba", "Urildim");
                gameState = 2;

            }
        }

        font.draw(batch, Integer.toString(score), 100, 200);

        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
