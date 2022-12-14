package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
	private float birdXAxis;
	private float pipeXAxis;
	private float pipeYAxis;
	private float pipeGap;
	private Random random;
	private int score = 0;
	private int maxScore = 0;
	private boolean passedPipe = false;
	private int gameStatus;

	// Exibição de texto
	BitmapFont textScore;
	BitmapFont textRestart;
	BitmapFont textBestScore;

	// Configurações de som
	Sound flySound;
	Sound collisionSound;
	Sound scoreSound;

	// Salvar pontuação
	Preferences preferences;

	// Objetos para camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_HEIGHT = 1280;
	private final float VIRTUAL_WIDTH = 720;


	@Override
	public void create () {
		initTextures();
		initObjects();
	}

	@Override
	public void render () {
		// Limpar frames Anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

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
				gravity = -8;
				gameStatus = 1;
				flySound.play();
			}
		} else if (gameStatus == 1) {
			// Aplicando o evento de toque.
			if (touchScreen) {
				gravity = -8;
				flySound.play();
			}

			// Movimento dos canos
			pipeXAxis -= Gdx.graphics.getDeltaTime() * 300;
			if (pipeXAxis < -topPipe.getWidth()) {
				pipeXAxis = deviceWidth;
				pipeYAxis = random.nextInt(600) - 300;
				passedPipe = false;
			}

			// Aplicando a gravidade.
			if (initialYBirdPosition > 0 || touchScreen) {
				initialYBirdPosition = initialYBirdPosition - gravity;
			}

			gravity += 0.3;
		} else if (gameStatus == 2) {
//			if (initialYBirdPosition > 0 || touchScreen) {
//				initialYBirdPosition = initialYBirdPosition - gravity;
//				gravity++;
//			}

			if (score > maxScore) {
				maxScore = score;
				preferences.putInteger("maxScore", maxScore);
			}

			birdXAxis -= Gdx.graphics.getDeltaTime() * 500;

			// Aplicando o evento de toque.
			if (touchScreen) {
				gameStatus = 0 ;
				score = 0;
				gravity = 0;
				birdXAxis = 0;
				initialYBirdPosition = deviceHeight / 2;
				pipeXAxis = deviceWidth;
			}
		}
	}

	private void detectCollisions() {

		birdCircle.set(
				50 + birdXAxis + birds[0].getWidth()/(float) 2,
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
			if (gameStatus == 1) {
				collisionSound.play();
				gameStatus = 2;
			}
		}
	}

	public void checkScore() {
		if (pipeXAxis < (50 - birds[0].getWidth())) {
			if (!passedPipe) {
				score++;
				passedPipe = true;
				scoreSound.play();
			}
		}

		variation += Gdx.graphics.getDeltaTime() * 5;
		// Verificação de variação para movimento das asas do pássaro
		if (variation > 3)
			variation = 0;
	}

	private void drawTextures() {
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(background, 0, 0, deviceWidth, deviceHeight);
		batch.draw(birds[(int) variation], 50 + birdXAxis, initialYBirdPosition);
		batch.draw(bottomPipe, pipeXAxis, deviceHeight / 2 - bottomPipe.getHeight() - pipeGap / 2 + pipeYAxis);
		batch.draw(topPipe, pipeXAxis, deviceHeight / 2 + pipeGap / 2 + pipeYAxis);
		textScore.draw(batch, String.valueOf(score),deviceWidth / 2, deviceHeight - 110);

		if (gameStatus == 2) {
			batch.draw(gameOver, deviceWidth / 2 - gameOver.getWidth() /(float) 2, deviceHeight / 2);
			textRestart.draw(batch, "Toque para reiniciar!", deviceWidth / 2 - 200, deviceHeight / 2 - gameOver.getHeight() / (float) 2);
			textBestScore.draw(batch, "Seu record é: " + maxScore + " pontos", deviceWidth / 2 - 200, deviceHeight / 2 - gameOver.getHeight());
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

		deviceHeight = VIRTUAL_HEIGHT;
		deviceWidth = VIRTUAL_WIDTH;
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

		// Sons
		flySound = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		// Configs de preferencias
		preferences = Gdx.app.getPreferences("flappyBird");
		maxScore = preferences.getInteger("maxScore", 0);

		// Configs Camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {

	}
}
