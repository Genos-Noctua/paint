package genos.noctua.paint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {

    protected Slider brushSizeSlider;
    protected Slider rubberSizeSlider;
    protected TextView brushSizeLabel;
    protected TextView rubberSizeLabel;
    protected Button drawingToggleButton;
    protected Button clearingToggleButton;
    protected GridLayout colorsGridLayout;

    public static final int DEF_COLOR = Color.BLACK;
    public static final int AVAILABLE_COLORS = 8;

    private View[] declaredColors;
    private final Gson gson = new Gson();
    private CanvasSettingsDto settingsDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Настройки");

        brushSizeSlider = findViewById(R.id.brushSizeSlider);
        rubberSizeSlider = findViewById(R.id.rubberSizeSlider);
        brushSizeLabel = findViewById(R.id.brushSizeLabel);
        rubberSizeLabel = findViewById(R.id.rubberSizeLabel);
        drawingToggleButton = findViewById(R.id.drawingToggleButton);
        clearingToggleButton = findViewById(R.id.clearingToggleButton);
        colorsGridLayout = findViewById(R.id.colorsGridLayout);

        settingsDto = parseSettings();
        insertAvailableColors();

        drawingToggleButton.setEnabled(settingsDto.isDrawingEnabled());
        clearingToggleButton.setEnabled(settingsDto.isClearingEnabled());
        brushSizeSlider.setValue(settingsDto.getBrushSize());
        rubberSizeSlider.setValue(settingsDto.getRubberSize());

        brushSizeSlider.addOnChangeListener(new BrushChangeSliderListener(brushSizeLabel, settingsDto));
        rubberSizeSlider.addOnChangeListener(new RubberChangeSliderListener(rubberSizeLabel, settingsDto));

        drawingToggleButton.setOnClickListener(this::enableDrawingOnClick);
        clearingToggleButton.setOnClickListener(this::enableClearingOnClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.return_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent resultIntent = new Intent();
        resultIntent.putExtra("settingsDto", gson.toJson(settingsDto));
        setResult(RESULT_OK, resultIntent);
        finish();
        return true;
    }

    private void insertAvailableColors() {
        declaredColors = new View[AVAILABLE_COLORS];
        for (int i = 0; i < AVAILABLE_COLORS; i++) {
            declaredColors[i] = colorsGridLayout.getChildAt(i);
            declaredColors[i].setOnClickListener(this::changeSelectedColor);

            final Drawable background = declaredColors[i].getBackground();
            if (!(background instanceof ColorDrawable)) continue;

            final ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setAlpha(i == 0 ? 255 : 128);
        }
    }

    private void enableDrawingOnClick(View view) {
        drawingToggleButton.setEnabled(false);
        clearingToggleButton.setEnabled(true);
        settingsDto.setBrushMode(BrushMode.DRAWING);
    }

    private void enableClearingOnClick(View view) {
        drawingToggleButton.setEnabled(true);
        clearingToggleButton.setEnabled(false);
        settingsDto.setBrushMode(BrushMode.CLEARING);
    }

    private void changeSelectedColor(View view) {
        final Drawable background = view.getBackground();
        if (!(background instanceof ColorDrawable)) return;

        final ColorDrawable colorDrawable = (ColorDrawable) background;
        colorDrawable.setAlpha(255);
        settingsDto.setColor(colorDrawable.getColor());
        Toast.makeText(this, "Цвет изменён", Toast.LENGTH_SHORT).show();

        for (final View declaredView : declaredColors) {
            if (declaredView.getId() == view.getId()) continue;
            final ColorDrawable declaredColor = (ColorDrawable) declaredView.getBackground();
            declaredColor.setAlpha(128);
        }
    }

    private CanvasSettingsDto parseSettings() {
        final String dataString = getIntent().getStringExtra("settingsDto");
        return gson.fromJson(dataString, CanvasSettingsDto.class);
    }
}
