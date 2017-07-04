package net.mgsx.game.plugins.procedural.tools;

import com.badlogic.gdx.math.RandomXS128;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

@Editable
public class ProceduralHeightFieldTool extends Tool
{
	@Editable public int width = 16, height = 16;
	@Editable public float scale = 1;
	@Editable public float frequency = 1;
	@Editable public float persistence = .5f;
	@Editable public int octaves = 3;
	@Editable(type=EnumType.RANDOM) public long seed = 0xdeadbeef;
	
	public ProceduralHeightFieldTool(EditorScreen editor) {
		super("Procedural Height Field", editor);
	}
	
	@Editable
	public void generate()
	{
		ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
		RandomXS128 rand = new RandomXS128(this.seed);
		
		float [] values = new float[width * height];
		
		long seed = this.seed;
		float amplitude = 1;
		float sum = 0;
		float frequency = this.frequency;
		for(int i=0 ; i<octaves ; i++){
			noise.seed(seed);
			for(int y=0 ; y<height ; y++){
				for(int x=0 ; x<width ; x++){
					float fy = (float)y / (float)(height-1);
					float fx = (float)x / (float)(width-1);
					
					values[y*width+x] += noise.get(fx * frequency, fy * frequency) * amplitude;
				}
			}
			sum += amplitude;
			amplitude *= persistence;
			frequency *= 2;
			seed = rand.nextLong();
		}
		for(int y=0 ; y<height ; y++){
			for(int x=0 ; x<width ; x++){
				values[y*width+x] *= scale / sum;
			}
		}
		
		HeightFieldComponent hfc = getEngine().createComponent(HeightFieldComponent.class);
		
		hfc.width = width;
		hfc.height = height;
		hfc.values = values;
		
		currentEntity().add(hfc);
	}

}
