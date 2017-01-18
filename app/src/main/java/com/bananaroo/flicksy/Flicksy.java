package com.bananaroo.flicksy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by kkampfen on 12/1/2015.
 */
public class Flicksy {
    Bitmap bitmap;
    float xSpeed = 5;
    float ySpeed = 0;
    float flickSpeed = -25;
    boolean flicked = false;
    boolean spawn = false;
    public int frameWidth = 120;
    public int frameHeight = 120;
    float yPosition;
    float xPosition;
    int currentFrame = 1;
    int frameCount = 6;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMilliseconds = 100;

    public Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
    public RectF whereToDraw = new RectF(xPosition, yPosition, xPosition + frameWidth, frameHeight);

    public class FlicksyLeft extends Flicksy {
        public FlicksyLeft(Context context, Canvas canvas) {
            xPosition = 0;
            yPosition = canvas.getHeight() - 200;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.flicksy);
        }
    }

    public class FlicksyRight extends Flicksy {
        public FlicksyRight(Context context, Canvas canvas) {
            xPosition = canvas.getWidth() - frameWidth;
            yPosition = canvas.getHeight() - 200;
            xSpeed = -xSpeed;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.flicksy);
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
        if (xSpeed > 0) {
            frameToDraw.top = 0;
            frameToDraw.bottom = 60;
        }
        else
        {
            frameToDraw.top = 60;
            frameToDraw.bottom = 120;
        }
    }

    // Movement operations
    public void move(Canvas canvas) {
        xPosition += xSpeed;
        yPosition += ySpeed;
        if (xPosition < 0 || xPosition > (canvas.getWidth() - frameWidth)) {
            xSpeed = -xSpeed;
        }

        whereToDraw.set(
                (int) xPosition,
                (int) yPosition,
                (int) xPosition + (int) frameWidth,
                (int) yPosition + (int) frameHeight);

        getCurrentFrame();
    }
}
