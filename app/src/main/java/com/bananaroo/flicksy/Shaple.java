package com.bananaroo.flicksy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by kkampfen on 11/26/2015.
 */
public class Shaple {

    int health;
    Bitmap bitmap;
    float xSpeed;
    float ySpeed;
    public int frameWidth = 128;
    public int frameHeight = 256;
    float yPosition = -256;
    float xPosition;
    float flickSpeed = -10;
    int currentFrame = 1;
    int frameCount = 6;
    public long hitTime, hitDelay = 1000;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMilliseconds = 100;

    public Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
    public RectF whereToDraw = new RectF(xPosition, yPosition, xPosition + frameWidth, frameHeight);

    public class ShapleOval extends Shaple {
        ShapleOval(Context context, Canvas canvas) {
            health = 1;
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.shaple_oval_shaded);
            xPosition = setStartingXPosition(canvas);
            ySpeed = setYspeed();
            xSpeed = setXspeed();
        }
    }

    public class ShapleTri extends Shaple {
        ShapleTri(Context context, Canvas canvas) {
            health = 2;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shaple_tri_shaded);
            xPosition = setStartingXPosition(canvas);
            ySpeed = setYspeed();
            xSpeed = setXspeed();
        }
    }

    public class ShapleRect extends Shaple {
        ShapleRect(Context context, Canvas canvas) {
            health = 3;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shaple_rect_shaded);
            xPosition = setStartingXPosition(canvas);
            ySpeed = setYspeed();
            xSpeed = setXspeed();
        }
    }

    // Set current animation frame
    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            }
        if (currentFrame >= frameCount) {
            currentFrame = 1;
        }

        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    // Generate random starting position on X axis
    private float setStartingXPosition(Canvas canvas) {
        int tempMaxX = canvas.getWidth();
        float maxX = (float) tempMaxX - frameWidth;
        Random random = new Random();
        float x = Math.abs(random.nextFloat() * maxX);
        return x;
    }

    // Generate random vertical speed
    private float setYspeed() {
        float minY = 1, maxY = 5;
        Random random = new Random();
        float speed = (random.nextFloat() * maxY) + minY;
        return speed;
    }

    // Set horizontal speed and direction
    private float setXspeed() {
        Random random = new Random();
        float minSpeed = -2, maxSpeed = 2, speed;
        speed = (random.nextFloat() * maxSpeed) + minSpeed;
        return speed;
    }

    // Movement operations
    public void move(Canvas canvas) {
        xPosition += xSpeed;
        if (xPosition < 0 ||
                xPosition > (canvas.getWidth() - frameWidth)) {
            xSpeed = -xSpeed;
        }

        yPosition += ySpeed;

        whereToDraw.set(
                (int) xPosition,
                (int) yPosition,
                (int) xPosition + (int) frameWidth,
                (int) yPosition + (int) frameHeight);

        getCurrentFrame();
    }
}
