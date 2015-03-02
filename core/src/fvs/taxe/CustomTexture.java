package fvs.taxe;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

/**
 * @author Stefan Kokov sk1056@york.ac.uk
 *Subclass of com.badlogic.gdx.graphics.Texture, defines a Texture object and sets
 *a linear filter to that object upon creation. This class should be used for all images
 *in order to avoid loss of quality of images upon resizing the game window.
 */
public class CustomTexture extends Texture {

	CustomTexture(String internalPath) {
		super(internalPath);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(FileHandle file) {
		super(file);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(Pixmap pixmap) {
		super(pixmap);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(TextureData data) {
		super(data);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(FileHandle file, boolean useMipMaps) {
		super(file, useMipMaps);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(Pixmap pixmap, boolean useMipMaps) {
		super(pixmap, useMipMaps);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(FileHandle file, Format format, boolean useMipMaps) {
		super(file, format, useMipMaps);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(Pixmap pixmap, Format format, boolean useMipMaps) {
		super(pixmap, format, useMipMaps);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public CustomTexture(int width, int height, Format format) {
		super(width, height, format);
		this.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

}
