package net.mgsx.game.examples.lsystem.model.l2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.lsystem.model.Generator;
import net.mgsx.game.examples.lsystem.model.Parameter;
import net.mgsx.game.examples.lsystem.model.Rule;

// TODO rule can't be stateless (for optim query parameter from symbol or 
public class Rules2D {

	public static class RuleBase extends Rule.RuleBase<Context2D>{}
	
	
	public static final Rule rotateRight = new RuleBase() {
		private Parameter angle;
		@Override
		public void init(Generator<Context2D> context){
			super.init(context);
			angle = context.system.parameter("angle");
		}
		@Override
		public void execute(int depth) {
			context.current.orientation.rotate(Vector3.Z, -this.angle.value);
		}
	};
	public static final Rule size = new RuleBase() {
		private Parameter size;
		@Override
		public void init(Generator<Context2D> context){
			super.init(context);
			size = context.system.parameter("size");
		}
		@Override
		public void execute(int depth) {
			float v = context.symbol.parameters.size > 0 ?  context.symbol.parameters.get(0).value : size.value;
			context.current.scale.scl(v);
		}
	};
	public static final Rule color(final Color c){
		return new RuleBase() {
			@Override
			public void execute(int depth) {
				context.current.color.set(c);
			}
		};
	}
	public static final Rule rotateLeft = new RuleBase() {
		private Parameter angle;
		@Override
		public void init(Generator context){
			super.init(context);
			angle = context.system.parameter("angle");
		}
		@Override
		public void execute(int depth) {
			float angle = context.symbol.parameters.size < 1 ? this.angle.value : context.symbol.parameters.get(0).value;
			context.current.orientation.rotate(Vector3.Z, angle);
		}
	};
	public static final Rule circle = new RuleBase() {
		@Override
		public void execute(int depth) {
			Context2D c = context.current;
			float dx = c.orientation.x * c.scale.x * .2f;
			float dy = c.orientation.y * c.scale.y * .2f;
			c.position.x += dx;
			c.position.y += dy;
			c.renderer.setColor(context.current.color);
			c.renderer.circle(c.position.x, c.position.y, .2f * c.scale.z, 12);
			c.position.x += dx;
			c.position.y += dy;
		}
	};
	public static final Rule line = new RuleBase() {
		@Override
		public void execute(int depth) {
			Context2D c = context.current;
			float x = c.position.x + c.orientation.x * c.scale.x;
			float y = c.position.y + c.orientation.y * c.scale.y;
			c.renderer.setColor(context.current.color);
			c.renderer.line(c.position.x, c.position.y, x, y);
			c.position.x = x;
			c.position.y = y;
		}
	};

}
