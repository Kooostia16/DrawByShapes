package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;

import static com.mygdx.game.MyGdxGame.Shape.getRandomShape;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Pixmap imgPrev;
	Pixmap pxmap;
	long currentScore = 0;
	float minScore = Integer.MAX_VALUE;
	float maxScore = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		pxmap = new Pixmap(new FileHandle(new File("avatar2.jpg")));
		imgPrev = new Pixmap(pxmap.getWidth(), pxmap.getHeight(), Pixmap.Format.RGB888);
		for (int i = 0; i < imgPrev.getWidth(); i++) {
			for (int j = 0; j < imgPrev.getHeight(); j++) {
				Color color1 = new Color();
				Color color2 = new Color();
				Color.rgb888ToColor(color1, pxmap.getPixel(i, j));
				Color.rgb888ToColor(color2, imgPrev.getPixel(i, j));
				float r = Math.abs(color1.r - color2.r);
				float g = Math.abs(color1.g - color2.g);
				float b = Math.abs(color1.b - color2.b);
				minScore += (r * r + g * g + b * b);
			}
		}
		maxScore = minScore;
	}

	@Override
	public void render () {
		PixmapScore lastPixmap = getHigherScoreShape(pxmap, imgPrev, minScore);
		imgPrev.dispose();
		minScore = lastPixmap.getScore();
		imgPrev = lastPixmap.getImage();
		img = new Texture(imgPrev);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	private PixmapScore getHigherScoreShape(Pixmap image, Pixmap previousScoredImage, float lastMinScore) {
		float minScore = lastMinScore;
		Pixmap shape = null;
		int x = 0;
		int y = 0;
		Pixmap current = new Pixmap(previousScoredImage.getWidth(), previousScoredImage.getHeight(), Pixmap.Format.RGB888);
		current.drawPixmap(previousScoredImage, 0, 0);
		while (shape == null) {

			float div = minScore/maxScore;
			Pixmap currShape = getRandomShape((int) (pxmap.getWidth() * div), (int) (pxmap.getHeight() * div));
			int currX = (int) (Math.random() * (pxmap.getWidth() - currShape.getWidth()));
			int currY = (int) (Math.random() * (pxmap.getHeight() - currShape.getHeight()));
			float score = calculateScore(image, previousScoredImage, currShape, currX, currY);
			if (score < 0) {
				shape = currShape;
				x = currX;
				y = currY;
				minScore += score;
			} else {
				currShape.dispose();
			}
		}

		current.drawPixmap(shape, x, y);
		shape.dispose();

		return new PixmapScore(current, minScore);
	}

	private float calculateScore(Pixmap image, Pixmap previousScoredImage, Pixmap shape, int x, int y) {
		float score = 0f;
		float scoreOld = 0f;
		Pixmap imageNew = new Pixmap(previousScoredImage.getWidth(), previousScoredImage.getHeight(), Pixmap.Format.RGB888);
		imageNew.drawPixmap(previousScoredImage, 0, 0);
		imageNew.drawPixmap(shape, x, y);
		Color color1 = new Color();
		Color color2 = new Color();
		float r;
		float g;
		float b;
		for (int i = x-1; i < x + shape.getWidth()-1; i++) {
			for (int j = y-1; j < y + image.getHeight()-1; j++) {
				Color.rgb888ToColor(color1, image.getPixel(i, j));
				Color.rgb888ToColor(color2, imageNew.getPixel(i, j));
				r = Math.abs(color1.r - color2.r);
				g = Math.abs(color1.g - color2.g);
				b = Math.abs(color1.b - color2.b);
				score += (r * r + g * g + b * b);
				Color.rgb888ToColor(color2, previousScoredImage.getPixel(i, j));
				r = Math.abs(color1.r - color2.r);
				g = Math.abs(color1.g - color2.g);
				b = Math.abs(color1.b - color2.b);
				scoreOld += (r * r + g * g + b * b);
			}
		}
		imageNew.dispose();

		return score < scoreOld ? score - scoreOld : 0;
	}

	static class Shape {
		static public Pixmap getShape() {
			return null;
		}

		static public Pixmap getRandomShape(int w, int h) {

			int color = Color.rgba8888((float) Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random());
			Pixmap pixmap;

			if (Math.random() > 0.5) {
				int wN = (int) (Math.random() * w);
				pixmap = new Pixmap(wN, wN, Pixmap.Format.RGBA8888);
				pixmap.setColor(color);
				pixmap.fillCircle(wN/2, wN/2, (int) (wN/2));
			} else {
				int wN = (int) (Math.random() * w);
				int hN = (int) (Math.random() * h);
				pixmap = new Pixmap(wN, hN, Pixmap.Format.RGBA8888);
				pixmap.setColor(color);
				pixmap.fill();
			}

			return pixmap;
		}
	}

	static class PixmapScore {
		private Pixmap image;
		private float score;

		public PixmapScore(Pixmap pixmap, float score) {
			this.image = pixmap;
			this.score = score;
		}

		public Pixmap getImage() {
			return image;
		}

		public void setImage(Pixmap image) {
			this.image = image;
		}

		public float getScore() {
			return score;
		}

		public void setScore(float score) {
			this.score = score;
		}
	}
}
