package net.mgsx.game.tutorials;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Tutorial {
	String title();
	String id();
	String group() default "";
	Class<?>[] dependsOn() default {};
	int order() default 0;
}
