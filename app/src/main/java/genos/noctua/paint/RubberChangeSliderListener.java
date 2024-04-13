package genos.noctua.paint;

import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.function.Function;

class RubberChangeSliderListener extends AbstractSliderChangeListener implements Slider.OnChangeListener {

    public static final int DEF_RUBBER_SIZE = 10;
    public static final int MAX_RUBBER_SIZE = 200;

    private final Function<Integer, String> rubberLabelText = v ->
            String.format("Размер ластика: %d / %d", v, MAX_RUBBER_SIZE);

    RubberChangeSliderListener(TextView textView, CanvasSettingsDto settingsDto) {
        super(textView, settingsDto);
        textView.setText(rubberLabelText.apply(settingsDto.getRubberSize()));
    }

    @Override
    public void onValueChange(Slider slider, float value, boolean fromUser) {
        textView.setText(rubberLabelText.apply((int)value));
        settingsDto.setRubberSize((int)value);
    }
}
