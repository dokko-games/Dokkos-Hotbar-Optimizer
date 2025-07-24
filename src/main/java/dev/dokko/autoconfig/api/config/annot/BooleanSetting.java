package dev.dokko.autoconfig.api.config.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BooleanSetting {
    int YESNO = 0;
    int ONOFF = 1;
    int TRUEFALSE = 2;

    int type() default YESNO;
}
