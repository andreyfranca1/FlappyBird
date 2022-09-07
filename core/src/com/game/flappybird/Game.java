package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Game extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] birds;
	private Texture background;
	private Texture topPipe;
	private Texture bottomPipe;
	private Texture gameOver;

	// Formas para colisão
	private Circle birdCircle;
	private Rectangle topPipeRectangle;
	private Rectangle bottomPipeRectangle;

	// Attributes
	private float deviceWidth;
	private float deviceHeight;
	private float variation = 0;
	private float gravity = 0;
	private float initialYBirdPosition = 0;
	private float pipeXAxis;
	private float pipeYAxis;
	private float pipeGap;
	private Random random;
	private int score = 0;
	private boolean passedPipe = false;
	private int gameStatus;

	// Exibição de texto
	BitmapFont textScore;
	BitmapFont textRestart;
	BitmapFont textBestScore;

	@Override
	public void create () {
		initTextures();
		initObjects();
	}

	@Override
	public void render () {
		verifyGameStatus();
		checkScore();
		drawTextures();
		detectCollisions();
	}

	private void verifyGameStatus() {
		boolean touchScreen = Gdx.input.justTouched();

		if (gameStatus == 0) {
			// Aplicando o evento de toque.
			if (touchScreen) {
				gravity = -15;
				gameStatus = 1;
			}
		} else if (gameStatus == 1) {
			// Aplicando o evento de toque.
			if (touchScreen) {
				gravity = -15;
			}

			// Movimento dos canos
			pipeXAxis -= Gdx.graphics.getDeltaTime() * 300;
			if (pipeXAxis < -topPipe.getWidth()) {
				pipeXAxis = deviceWidth;
				pipeYAxis = random.nextInt(400) - 200;
				passedPipe = false;
			}

			// Aplicando a gravidade.
			if (initialYBirdPosition > 0 || touchScreen) {
				initialYBirdPosition = initialYBirdPosition - gravity;
			}

			gravity ++;
		} else if (gameStatus == 2) {
			// Aplicando o evento de toque.
			if (touchScreen) {
				gameStatus = 0 ;
				score = 0;
				gravity = 0;
				initialYBirdPosition = deviceHeight / 2;
				pipeXAxis = deviceWidth;
			}
		}
	}

	private void detectCollisions() {

		birdCircle.set(
				50 + birds[0].getWidth()/(float) 2,
				initialYBirdPosition + birds[0].getHeight()/(float) 2,
				birds[0].getWidth()/(float) 2
		);

		topPipeRectangle.set(
				pipeXAxis,
				deviceHeight / 2 + pipeGap / 2 + pipeYAxis,
				topPipe.getWidth(),
				topPipe.getHeight()
		);

		bottomPipeRectangle.set(
				pipeXAxis,
				deviceHeight / 2 - bottomPipe.getHeight() - pipeGap / 2 + pipeYAxis,
				bottomPipe.getWidth(),
				bottomPipe.getHeight()
		);
		boolean topPipeCollision = Intersector.overlaps(birdCircle, topPipeRectangle);
		boolean bottomPipeCollision = Intersector.overlaps(birdCircle, bottomPipeRectangle);


		if (topPipeCollision || bottomPipeCollision) {
			gameStatus = 2;
		}
	}

	public void checkScore() {
		if (pipeXAxis < (50 - birds[0].getWidth())) {
			if (!passedPipe) {
				score++;
				passedPipe = true;
			}
		}

		variation += Gdx.graphics.getDeltaTime() * 5;
		// Verificação de variação para movimento das asas do pássaro
		if (variation > 3)
			variation = 0;
	}

	private void drawTextures() {
		batch.begin();

		batch.draw(background, 0, 0, deviceWidth, deviceHeight);
		batch.draw(birds[(int) variation], 50, initialYBirdPosition);
		batch.draw(bottomPipe, pipeXAxis, deviceHeight / 2 - bottomPipe.getHeight() - pipeGap / 2 + pipeYAxis);
		batch.draw(topPipe, pipeXAxis, deviceHeight / 2 + pipeGap / 2 + pipeYAxis);
		textScore.draw(batch, String.valueOf(score),deviceWidth / 2, deviceHeight - 110);

		if (gameStatus == 2) {
			batch.draw(gameOver, deviceWidth / 2 - gameOver.getWidth() /(float) 2, deviceHeight / 2);
			textRestart.draw(batch, "Toque para reiniciar!", deviceWidth / 2 - 200, deviceHeight / 2 - gameOver.getHeight() / (float) 2);
			textBestScore.draw(batch, "Seu record é: 0 pontos", deviceWidth / 2 - 200, deviceHeight / 2 - gameOver.getHeight());
		}

		batch.end();
	}

	private void initTextures() {
		birds = new Texture[3];
		birds[0] = new Texture("passaro1.png");
		birds[1] = new Texture("passaro2.png");
		birds[2] = new Texture("passaro3.png");

		background = new Texture("fundo.png");

		topPipe = new Texture("cano_topo_maior.png");
		bottomPipe = new Texture("cano_baixo_maior.png");

		gameOver = new Texture("game_over.png");
	}

	private void initObjects() {
		batch = new SpriteBatch();
		random = new Random();

		deviceHeight = Gdx.graphics.getHeight();
		deviceWidth = Gdx.graphics.getWidth();
		initialYBirdPosition = deviceHeight / 2;
		pipeXAxis = deviceWidth;
		pipeGap = 225;

		// Textos configs
		textScore = new BitmapFont();
		textScore.setColor(Color.WHITE);
		textScore.getData().setScale(10);

		textRestart = new BitmapFont();
		textRestart.setColor(Color.GREEN);
		textRestart.getData().setScale(3);

		textBestScore = new BitmapFont();
		textBestScore.setColor(Color.RED);
		textBestScore.getData().setScale(3);


		// Formas Geométricas de colisão
		birdCircle = new Circle();
		topPipeRectangle = new Rectangle();
		bottomPipeRectangle = new Rectangle();

	}

	@Override
	public void dispose () {
		Gdx.app.log("dispose", "Descarte Conteudo");
	}
}
