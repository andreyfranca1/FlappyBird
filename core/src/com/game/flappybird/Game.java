package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Game extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] birds;
	private Texture background;
	private Texture bottomPipe;
	private Texture topPipe;

	// Attributes
	private float deviceWidth;
	private float deviceHeight;
	private float variation = 0;
	private float gravity = 0;
	private float initialBirdPositionY = 0;
	private float pipeXAxis;
	private float pipeYAxis;
	private float pipeGap;
	private Random random;

	@Override
	public void create () {
		initTextures();
		initObjects();
	}

	@Override
	public void render () {
		verifyGameStatus();
		drawTextures();
	}

	private void verifyGameStatus() {
		// Movimento dos canos
		pipeXAxis -= Gdx.graphics.getDeltaTime() * 300;
		if (pipeXAxis < -topPipe.getWidth()) {
			pipeXAxis = deviceWidth;
			pipeYAxis = random.nextInt(800) - 400;
		}

		// Aplicando o evento de toque.
		boolean touchScreen = Gdx.input.justTouched();

		if (touchScreen) {
			gravity = -15;
		}

		// Aplicando a gravidade.
		if (initialBirdPositionY > 0 || touchScreen) {
			initialBirdPositionY = initialBirdPositionY - gravity;
		}


		variation += Gdx.graphics.getDeltaTime() * 5;
		// Verificação de variação para movimento das asas do pássaro
		if (variation > 3)
			variation = 0;

		gravity ++;
	}

	private void drawTextures() {
		batch.begin();

		batch.draw(background, 0, 0, deviceWidth, deviceHeight);
		batch.draw(birds[(int) variation], 30, initialBirdPositionY);
		batch.draw(bottomPipe, pipeXAxis, deviceHeight / 2 - bottomPipe.getHeight() - pipeGap / 2 + pipeYAxis);
		batch.draw(topPipe, pipeXAxis, deviceHeight / 2 + pipeGap / 2 + pipeYAxis);

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
	}

	private void initObjects() {
		batch = new SpriteBatch();
		random = new Random();

		deviceHeight = Gdx.graphics.getHeight();
		deviceWidth = Gdx.graphics.getWidth();
		initialBirdPositionY = deviceHeight / 2;
		pipeXAxis = deviceWidth;
		pipeGap = 225;
	}

	@Override
	public void dispose () {
		Gdx.app.log("dispose", "Descarte Conteudo");
	}
}
