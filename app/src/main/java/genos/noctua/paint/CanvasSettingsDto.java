package genos.noctua.paint;

import static genos.noctua.paint.BrushChangeSliderListener.DEF_BRUSH_WIDTH;
import static genos.noctua.paint.RubberChangeSliderListener.DEF_RUBBER_SIZE;
import static genos.noctua.paint.SettingsActivity.DEF_COLOR;

class CanvasSettingsDto {
    private float brushSize;
    private int rubberSize;
    private BrushMode brushMode;
    private int color;

    CanvasSettingsDto() {
        brushSize = DEF_BRUSH_WIDTH;
        rubberSize = DEF_RUBBER_SIZE;
        brushMode = BrushMode.DRAWING;
        color = DEF_COLOR;
    }

    float getBrushSize() {
        return brushSize;
    }

    void setBrushSize(float brushSize) {
        this.brushSize = brushSize;
    }

    int getRubberSize() {
        return rubberSize;
    }

    void setRubberSize(int rubberSize) {
        this.rubberSize = rubberSize;
    }

    BrushMode getBrushMode() {
        return brushMode;
    }

    void setBrushMode(BrushMode brushMode) {
        this.brushMode = brushMode;
    }

    int getColor() {
        return color;
    }

    void setColor(int color) {
        this.color = color;
    }

    void copy(CanvasSettingsDto canvasSettingsDto) {
        brushSize = canvasSettingsDto.getBrushSize();
        rubberSize = canvasSettingsDto.getRubberSize();
        brushMode = canvasSettingsDto.getBrushMode();
        color = canvasSettingsDto.getColor();
    }

    boolean isDrawingEnabled() {
        return !brushMode.equals(BrushMode.DRAWING);
    }

    boolean isClearingEnabled() {
        return !brushMode.equals(BrushMode.CLEARING);
    }
}
