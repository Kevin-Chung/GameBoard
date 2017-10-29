package com.gameboard.pong;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.Socket;

public class PongGame extends ApplicationAdapter {


    OnSendGameMessage.OnSendGameMessageListener listener;
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
    int playerWidth = 40;
    // player's height
    int playerHeight = 200;
    // speed at which players can move
    int playerSpeed = 300;
    int inputPlayerSpeed = 1000;
    // speed at which the pong ball moves
    int pongSpeed = 1000;
    // default scores
    int playerOneScore, playerTwoScore;
    // the ball
    public Rectangle pong;
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

    Socket socket;
    Gson gson;

    boolean isHost = false;



	@Override
	public void create () {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
	    gson = new Gson();

        Gdx.app.debug("MyTag", "creat my debug message");

        WIDTH = Gdx.graphics.getWidth() * 2;
        HEIGHT = Gdx.graphics.getWidth();

        cam = new OrthographicCamera(WIDTH / 2, HEIGHT);
        cam.setToOrtho(false);

        sr = new ShapeRenderer();

        p1 = new Rectangle(30, HEIGHT / 4, playerWidth, playerHeight);
        p2 = new Rectangle(WIDTH - 45, HEIGHT / 4, playerWidth, playerHeight);
        pong = new Rectangle(WIDTH / 2, HEIGHT / 3, 20, 20);
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

	public void sendEvent(String strData) {
        Gdx.app.debug("MyTag", "my debug message");
        JsonObject data = new JsonParser().parse(strData).getAsJsonObject();
        String type = data.get("event_type").getAsString();

        Gdx.app.debug("MyTag", type);
        if(type.equals("pong_update")) {
            Gdx.app.debug("MyTag", "Pong update");
            pong.x = data.get("x").getAsFloat();
            pong.y = data.get("y").getAsFloat();
            pongYspeed = data.get("pongYspeed").getAsInt();
            changePongY = data.get("changePongY").getAsDouble();
            pongDirection = data.get("pongDirection").getAsDouble();
            pongUp = data.get("pongUp").getAsBoolean();
            pongDown = data.get("pongDown").getAsBoolean();
            pongLeft = data.get("pongLeft").getAsBoolean();
            pongRight = data.get("pongRight").getAsBoolean();
        }

        else if (type.equals("player_update")) {
            boolean hostPlays = data.get("host").getAsBoolean();
            float newY = data.get("y").getAsFloat();

            if (hostPlays != isHost) {
                if (hostPlays) {
                    p1.y = newY;
                } else {
                    p2.y = newY;
                }
            }
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
        sr.circle(pong.x, pong.y, 20);
        sr.rect(p1.x, p1.y, p1.width, p1.height);
        sr.rect(p2.x, p2.y, p2.width, p2.height);
        sr.end();

        if(Gdx.input.getDeltaY() < 0) {
            if (isHost) {
                p1.y += inputPlayerSpeed * Gdx.graphics.getDeltaTime();
            } else {
                p2.y += inputPlayerSpeed * Gdx.graphics.getDeltaTime();
            }

            sendPlayerDataToServer();

        }
        if(Gdx.input.getDeltaY() > 0) {
            if (isHost) {
                p1.y -= inputPlayerSpeed * Gdx.graphics.getDeltaTime();
            } else {
                p2.y -= inputPlayerSpeed * Gdx.graphics.getDeltaTime();
            }

            sendPlayerDataToServer();
        }

        // determine pong's direction and change movement appropriately

        if (pongRight && !pongLeft) {
            pong.x += pongSpeed * Gdx.graphics.getDeltaTime();
        } else if (pongLeft && !pongRight) {
            pong.x -= pongSpeed * Gdx.graphics.getDeltaTime();
        }

        // change the up/down trajectory of the pong if necessary
        if (pongUp && !pongDown) {
            pong.y += pongYspeed * Gdx.graphics.getDeltaTime();
        } else if (pongDown && !pongUp) {
            pong.y -= pongYspeed * Gdx.graphics.getDeltaTime();
        }

        if (this.isHost) {
            changePongY = Math.random();



            // collision
            if (pong.overlaps(p1)) {
                pongRight = true;
                pongLeft = false;

                checkPongTrajectory();
            } else if (pong.overlaps(p2)) {
                pongRight = false;
                pongLeft = true;

                changePongY = Math.random();
                sendPongDataToServer();
            }

            // reset gameboard if the pong goes out of the boundaries
            else if (pong.overlaps(leftBound)) {
                resetBoard();
                playerTwoScore += 1;
                sendPongDataToServer();
            } else if (pong.overlaps(rightBound)) {
                resetBoard();
                playerOneScore += 1;
                sendPongDataToServer();
            }
        }
    }

    private void sendPongDataToServer() {
        // Send pong to server
        JsonObject pongUpdate = new JsonObject();
        pongUpdate.addProperty("event_type", "pong_update");
        pongUpdate.addProperty("x", pong.x);
        pongUpdate.addProperty("y", pong.y);
        pongUpdate.addProperty("pongYspeed", pongYspeed);
        pongUpdate.addProperty("changePongY", changePongY);
        pongUpdate.addProperty("pongDirection", pongDirection);
        pongUpdate.addProperty("pongUp", pongUp);
        pongUpdate.addProperty("pongDown", pongDown);
        pongUpdate.addProperty("pongLeft", pongLeft);
        pongUpdate.addProperty("pongRight", pongRight);

        listener.sendMessage(pongUpdate.toString());
    }

    private void sendPlayerDataToServer() {
        JsonObject playerUpdate = new JsonObject();
        playerUpdate.addProperty("event_type", "player_update");
        playerUpdate.addProperty("host", isHost);

        if (isHost) {
            playerUpdate.addProperty("y", p1.y);
        } else {
            playerUpdate.addProperty("y", p2.y);
        }


        listener.sendMessage(playerUpdate.toString());
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
            sendPongDataToServer();
        }
        // if the pong is the bottom half of the board and the trajectory shifts, send it upwards
        else if((changePongY < 0.7) && (pong.y < (HEIGHT / 2))) {
            pongUp = true;
            pongDown = false;
            sendPongDataToServer();
        }
        // if the trajectory doesn't shift at all, don't change the direction of the pong.
        else {
            pongUp = false;
            pongDown = false;
        }
    }

    public void setHost(boolean isHost) {
	    this.isHost = isHost;

        if (!isHost) {
            cam.position.set(cam.position.x + WIDTH  / 2, cam.position.y, 0);
        }
    }

    public void setOnUpdateListener(OnSendGameMessage.OnSendGameMessageListener listener) {
	    this.listener = listener;
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


