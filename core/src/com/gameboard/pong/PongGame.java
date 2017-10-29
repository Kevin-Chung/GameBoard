package com.gameboard.pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;


    public class PongGame extends ApplicationAdapter {
    // game's width
    public static int WIDTH;
    // game's height
    public static int HEIGHT;
    // camera to render things
    OrthographicCamera cam;
    // we need this shape renderer to render our shapes.
    ShapeRenderer sr;
    // player 1's rectangle
    Rectangle p1;
    // player 2's rectangle
    Rectangle p2;
    // player's width
    int playerWidth = 15;
    // player's height
    int playerHeight = 100;
    // speed at which players can move
    int playerSpeed = 300;
    // speed at which the pong ball moves
    int pongSpeed = 400;
    // default scores
    int playerOneScore, playerTwoScore;
    // the ball
    Rectangle pong;
    // chance that the trajectory will change
    int pongYspeed = 50;
    // randomly generated number to generate probability of pong's trajectory
    double changePongY;
    // the direction that the pong will move
    double pongDirection;
    // whether or not the pong will move up
    boolean pongUp = false;
    // whether or not the pong will move down
    boolean pongDown = false;
    // whether or not the pong is moving left
    boolean pongLeft = false;
    // whether or not the pong is moving right
    boolean pongRight = false;
    // left boundary
    Rectangle leftBound;
    // right boundary
    Rectangle rightBound;



	@Override
	public void create () {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getWidth();

        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.setToOrtho(false);
        sr = new ShapeRenderer();

        p1 = new Rectangle(30, HEIGHT / 4, playerWidth, playerHeight);
        p2 = new Rectangle(WIDTH - 45, HEIGHT / 4, playerWidth, playerHeight);
        pong = new Rectangle(WIDTH / 2, HEIGHT / 3, 5, 5);
        leftBound = new Rectangle(0, 0, 3, HEIGHT);
        rightBound = new Rectangle(WIDTH, 0, 3, HEIGHT);

        playerOneScore = 0;
        playerTwoScore = 0;
        pongDirection = Math.random();


        if(pongDirection > 0.5)
        {
            pongLeft = true;
        } else {
            pongRight = true;
        }

	}

    @Override
    public void dispose () {
        sr.dispose();
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.circle(pong.x, pong.y, 5);
        sr.rect(p1.x, p1.y, p1.width, p1.height);
        sr.rect(p2.x, p2.y, p2.width, p2.height);
        sr.end();

        if(Gdx.input.getDeltaY() < 0) {
            p1.y += playerSpeed * Gdx.graphics.getDeltaTime(); }
        if(Gdx.input.getDeltaY() > 0) {
            p1.y -= playerSpeed * Gdx.graphics.getDeltaTime(); }

        changePongY = Math.random();

        // determine pong's direction and change movement appropriately
        if(pongRight && !pongLeft)
        {
            pong.x += pongSpeed * Gdx.graphics.getDeltaTime();
        }
        else if(pongLeft && !pongRight)
        {
            pong.x -= pongSpeed * Gdx.graphics.getDeltaTime();
        }

        // change the up/down trajectory of the pong if necessary
        if(pongUp && !pongDown)
        {
            pong.y += pongYspeed * Gdx.graphics.getDeltaTime();
        }
        else if(pongDown && !pongUp)
        {
            pong.y -= pongYspeed * Gdx.graphics.getDeltaTime();
        }

        // collision
        if(pong.overlaps(p1))
        {
            pongRight = true;
            pongLeft = false;

            checkPongTrajectory();
        }
        else if(pong.overlaps(p2))
        {
            pongRight = false;
            pongLeft = true;

            changePongY = Math.random();
        }

        // reset gameboard if the pong goes out of the boundaries
        else if(pong.overlaps(leftBound))
        {
            resetBoard();
            playerTwoScore += 1;
        }

        else if(pong.overlaps(rightBound))
        {
            resetBoard();
            playerOneScore += 1;
        }

    }

    private void resetBoard() {
        pong.x = WIDTH / 2;
        pong.y = HEIGHT / 3;
        p1.x = 30;
        p1.y = HEIGHT / 4;
        p2.x = WIDTH - 45;
        p2.y = HEIGHT / 4;
        pongUp = false;
        pongDown  = false;
    }

    // determine whether or not the pong will go up or down.
    private void checkPongTrajectory() {
        changePongY = Math.random();
        // if the pong is in the top half of the board and the trajectory shifts, send it downwards
        if((changePongY > 0.3) && (pong.y > (HEIGHT / 2))) {
            pongUp = false;
            pongDown = true;
        }
        // if the pong is the bottom half of the board and the trajectory shifts, send it upwards
        else if((changePongY < 0.7) && (pong.y < (HEIGHT / 2))) {
            pongUp = true;
            pongDown = false;
        }
        // if the trajectory doesn't shift at all, don't change the direction of the pong.
        else {
            pongUp = false;
            pongDown = false;
        }
    }



    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }
}


