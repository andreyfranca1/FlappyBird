package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] birds;
	private Texture background;
	private int xAxis;
	private int yAxis;

	// Attributes
	private float deviceWidth;
	private float deviceHeight;
	private float variation = 0;
	private float gravity = 0;
	private float initialBirdPositionY = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();

		birds = new Texture[3];
		birds[0] = new Texture("passaro1.png");
		birds[1] = new Texture("passaro2.png");
		birds[2] = new Texture("passaro3.png");

		background = new Texture("fundo.png");

		deviceHeight = Gdx.graphics.getHeight();
		deviceWidth = Gdx.graphics.getWidth();
		initialBirdPositionY = deviceHeight / 2;
	}

	@Override
	public void render () {
		batch.begin();

		if (variation > 3)
			variation = 0;

		// Aplicando o evento de toque.
		boolean touchScreen = Gdx.input.justTouched();

		if (touchScreen) {
			gravity = -15;
		}

		// Aplicando a gravidade.
		if (initialBirdPositionY > 0 || touchScreen) {
			initialBirdPositionY = initialBirdPositionY - gravity;
		}

		batch.draw(background, 0, 0, deviceWidth, deviceHeight);
		batch.draw(birds[(int) variation], 30, initialBirdPositionY);

		variation += Gdx.graphics.getDeltaTime() * 5;

		gravity ++;
		xAxis++;
		yAxis++;

		batch.end();
	}
	
	@Override
	public void dispose () {
		Gdx.app.log("dispose", "Descarte Conteudo");
	}
}
