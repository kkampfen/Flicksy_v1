package com.bananaroo.flicksy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by kkampfen on 11/29/2015.
 */
public class GameView extends SurfaceView implements Runnable {
    Thread viewthread = null;
    Paint drawPaint = new Paint();
    SurfaceHolder holder;
    boolean running = true;
    int blueBackgroundColor = Color.argb(255, 30, 130, 180);
    int redBackgroundColor = Color.argb(255, 255, 20, 20);
    LinkedList<Shaple> shaple = new LinkedList<Shaple>();
    LinkedList<Flicksy> flicksy = new LinkedList<Flicksy>();
    Shaple tempShaple;
    Flicksy tempFlicksy;
    private long shapleTimeStart = System.currentTimeMillis();
    int shapleDelay = 2000;
    int shapleCount = 0;
    int randomMax = 2;
    int randomShaple = 1;
    int score = 0;
    int playerHealth = 5;
    private int frameCount = 6;
    Bitmap hitBitmap;

    private SoundPool sounds;
    private int sndHit;
    private int sndFlick;
    private int sndWilhelm;
    private int sndScream;

    public GameView(Context context) {
        super(context);
        holder = this.getHolder();
        hitBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.hit);
        hitBitmap = Bitmap.createScaledBitmap(hitBitmap, 120, 120, false);
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            sndHit = sounds.load(context, R.raw.hit, 0);
            sndFlick = sounds.load(context, R.raw.flick, 0);
            sndScream = sounds.load(context, R.raw.scream, 0);
            sndWilhelm = sounds.load(context, R.raw.wilhelm, 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            if (!holder.getSurface().isValid()) {
                continue;
            }

            if (playerHealth <= 0) {
                gameEnd();
            } else {
                Canvas canvas = holder.lockCanvas();
                gamePlay(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // Game playing screen
    protected void gamePlay(Canvas canvas) {

        canvas.drawColor(blueBackgroundColor);

        // Check that a Shaple exists in the LinkedList, then iterate through
        if (shaple != null) {
            for (int i = 0; i < shaple.size(); i++) {

                // Check Shaple health and remove from LinkedList if dead
                if (shaple.get(i).health < 1) {
                    removeShaple(shaple.get(i));
                    score++;
                }

                // Check Shaple position; if bottom of screen player loses health
                else if (shaple.get(i).yPosition > (canvas.getHeight() - shaple.get(i).frameHeight)) {
                    playerHealth--;
                    canvas.drawColor(redBackgroundColor);
                    removeShaple(shaple.get(i));
                    if (playerHealth > 0) {
                        sounds.play(sndWilhelm, 1, 1, 0, 0, 1);
                    }
                }

                // If Shaple is alive and not at bottom of screen, process movement operations
                else {
                    // Movement operations
                    shaple.get(i).move(canvas);

                    // Set up current frame and position to draw to screen
                    canvas.drawBitmap(shaple.get(i).bitmap,
                            shaple.get(i).frameToDraw,
                            shaple.get(i).whereToDraw,
                            drawPaint);
                }
            }
        }

        // Spawn initial Flicksies
        if (flicksy.size() < 1) {
            spawnNewFlicksyLeft(canvas);
            spawnNewFlicksyRight(canvas);
        }

        // Check that a Flicksy exists in the LinkedList, then iterate through
        if (flicksy != null) {
            for (int i = 0; i < flicksy.size(); i++) {

                // Movement operations
                flicksy.get(i).move(canvas);

                // Set up current frame and position to draw to screen
                canvas.drawBitmap(flicksy.get(i).bitmap,
                        flicksy.get(i).frameToDraw,
                        flicksy.get(i).whereToDraw,
                        drawPaint);

               // Check if bitmap has been flicked
                if (flicksy.get(i).flicked) {

                    // Spawn Flicksy to replace flicked
                    if (flicksy.get(i).spawn) {
                        if (flicksy.get(i) instanceof Flicksy.FlicksyLeft) {
                            spawnNewFlicksyLeft(canvas);
                            flicksy.get(i).spawn = false;
                        } else {
                            spawnNewFlicksyRight(canvas);
                            flicksy.get(i).spawn = false;
                        }
                    }

                    // Check if Flicksy has hit a Shaple
                    for (int j = 0; j < shaple.size(); j++) {
                        if (shaple.get(j).yPosition > -shaple.get(j).frameHeight / 2) {
                            if (RectF.intersects(flicksy.get(i).whereToDraw, shaple.get(j).whereToDraw)) {
                                sounds.play(sndHit, 1, 1, 0, 0, 1);
                                shaple.get(j).health--;
                                canvas.drawBitmap(hitBitmap, flicksy.get(i).whereToDraw.left, flicksy.get(i).whereToDraw.top, drawPaint);
                                removeFlicksy(flicksy.get(i));
                                j = shaple.size();
                            }
                        }
                    }

                    // Remove Flicksy if off screen
                    if (flicksy.get(i).yPosition < -120) {
                        removeFlicksy(flicksy.get(i));
                    }
                }
            }
        }

        // Display player data text on screen
        drawPaint.setColor(Color.argb(255, 250, 130, 0));
        drawPaint.setTextSize(75);
        canvas.drawText("Score: " + score, 20, 75, drawPaint);
        canvas.drawText("Health: " + playerHealth, canvas.getWidth() - 325, 75, drawPaint);

        if ((System.currentTimeMillis() - shapleTimeStart) > shapleDelay) {

            // Set max random value for determining bitmap type
            if (shapleCount > 10) {
                randomMax = 4;
                Random random = new Random();
                randomShaple = random.nextInt(randomMax - 1) + 1;
            } else if (shapleCount > 5) {
                randomMax = 3;
                Random random = new Random();
                randomShaple = random.nextInt(randomMax - 1) + 1;
            }

            if (randomShaple == 1) {
                tempShaple = new Shaple().new ShapleOval(getContext(), canvas);
                tempShaple.bitmap = Bitmap.createScaledBitmap(
                        tempShaple.bitmap,
                        tempShaple.frameWidth * frameCount,
                        tempShaple.frameHeight,
                        false);
            } else if (randomShaple == 2) {
                tempShaple = new Shaple().new ShapleTri(getContext(), canvas);
                tempShaple.bitmap = Bitmap.createScaledBitmap(
                        tempShaple.bitmap,
                        tempShaple.frameWidth * frameCount,
                        tempShaple.frameHeight,
                        false);
            } else if (randomShaple == 3) {
                tempShaple = new Shaple().new ShapleRect(getContext(), canvas);
                tempShaple.bitmap = Bitmap.createScaledBitmap(
                        tempShaple.bitmap,
                        tempShaple.frameWidth * frameCount,
                        tempShaple.frameHeight,
                        false);
            }

            addShaple(tempShaple);

            shapleTimeStart = System.currentTimeMillis();

            if (shapleDelay > 250) {
                shapleDelay -= 10;
            } else {
                shapleDelay = 250;
            }
            shapleCount++;
        }
    }

    // Check if Flicksy is touched, and if so, set new movement variables
    public void checkTouch(RectF touchRect, float flickedXSpeed, float flickedYSpeed) {
        if (flicksy != null) {
            for (int i = 0; i < flicksy.size(); i++) {
                if (!flicksy.get(i).flicked) {
                    if (RectF.intersects(flicksy.get(i).whereToDraw, touchRect)) {
                        flicksy.get(i).flicked = true;
                        flicksy.get(i).spawn = true;
                        flicksy.get(i).xSpeed = flickedXSpeed;
                        flicksy.get(i).ySpeed = flickedYSpeed;
                        sounds.play(sndFlick, 1, 1, 0, 0, 1);
                    }
                }
            }
        }
    }

    // Fade screen to red and play end sound when playerHealth < 0
    protected void gameEnd() {

        int alpha = 0;
        running = false;
        sounds.play(sndScream, 1, 1, 0, 0, 1);

        // Screen fade;
        for (int i = 0; i < 51; i ++) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(alpha, 0, 0, 0));
            alpha += 5;
            holder.unlockCanvasAndPost(canvas);

            try {
                viewthread.sleep(29);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        endGame(getContext());
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                viewthread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        viewthread = null;
    }

    public void resume() {
        running = true;
        viewthread = new Thread(this);
        viewthread.start();
    }

    public void spawnNewFlicksyLeft(Canvas canvas) {
        tempFlicksy = new Flicksy().new FlicksyLeft(getContext(), canvas);
        tempFlicksy.bitmap = Bitmap.createScaledBitmap(
                tempFlicksy.bitmap,
                tempFlicksy.frameWidth * frameCount,
                tempFlicksy.frameHeight,
                false);
        addFlicksy(tempFlicksy);
    }

    public void spawnNewFlicksyRight(Canvas canvas) {
        tempFlicksy = new Flicksy().new FlicksyRight(getContext(), canvas);
        tempFlicksy.bitmap = Bitmap.createScaledBitmap(
                tempFlicksy.bitmap,
                tempFlicksy.frameWidth * frameCount,
                tempFlicksy.frameHeight,
                false);
        addFlicksy(tempFlicksy);
    }

    public void addShaple(Shaple e) {
        shaple.add(e);
    }

    public void addFlicksy(Flicksy f) {
        flicksy.add(f);
    }

    public void removeShaple(Shaple e) {
        shaple.remove(e);
    }

    public void removeFlicksy(Flicksy f) {
        flicksy.remove(f);
    }

    public void endGame(Context context) {
        Intent intent = new Intent(context, EndActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("passScore", String.valueOf(score));
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}

