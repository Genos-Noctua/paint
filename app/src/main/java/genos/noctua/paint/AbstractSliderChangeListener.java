package genos.noctua.paint;

import android.widget.TextView;

abstract class AbstractSliderChangeListener {

    protected final TextView textView;
    protected final CanvasSettingsDto settingsDto;

    protected AbstractSliderChangeListener(TextView textView, CanvasSettingsDto settingsDto) {
        this.textView = textView;
        this.settingsDto = settingsDto;
    }
}
