package genos.noctua.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CanvasPolygonView extends View implements View.OnTouchListener {

    private final List<PolygonPointDto> pathsWithColors = new ArrayList<>();
    private final Rect rubberRect = new Rect();
    private final Paint rubberPaint = new Paint();

    private Paint paint = new Paint();
    private boolean isRubberVisible;

    private final CanvasSettingsDto settingsDto;
    private final Context context;

    CanvasPolygonView(Context context) {
        super(context);
        this.context = context;
        this.settingsDto = ((MainActivity)context).getSettingsDto();
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(settingsDto.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(settingsDto.getBrushSize());

        rubberPaint.setAntiAlias(true);
        rubberPaint.setColor(Color.WHITE);
        rubberPaint.setStyle(Paint.Style.FILL);
        rubberPaint.setStrokeWidth(2);
        rubberPaint.setColor(Color.BLACK);
        rubberPaint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (final PolygonPointDto dto : pathsWithColors) {
            canvas.drawPath(dto.getPath(), dto.getPaint());
        }
        if (isRubberVisible && settingsDto.getBrushMode().equals(BrushMode.CLEARING)) {
            canvas.drawRect(rubberRect, rubberPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final Path path = new Path();
            drawRubberRect(path, x, y);
            path.moveTo(x, y);
            pathsWithColors.add(new PolygonPointDto(path, paint));
            isRubberVisible = true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            final Path path = pathsWithColors.get(pathsWithColors.size() - 1).getPath();
            drawRubberRect(path, x, y);
            path.lineTo(x, y);
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isRubberVisible = false;
            invalidate();
        }
        return true;
    }

    public void drawRubberRect(Path path, float x, float y) {
        if (!settingsDto.getBrushMode().equals(BrushMode.CLEARING)) return;
        final int size = settingsDto.getRubberSize();
        final float doubleSize = size + size / 2f;
        final float halfSize = size / 2f;
        rubberRect.set((int)(x - halfSize), (int)(y - halfSize), (int)(x + doubleSize), (int)(y + doubleSize));
        path.addRect(x, y, x + size, y + size, Path.Direction.CW);
    }

    public void updateSettings() {
        paint = new Paint();
        paint.setAntiAlias(true);

        if (settingsDto.getBrushMode().equals(BrushMode.CLEARING)) {
            paint.setColor(Color.parseColor("#fffbfe"));
            paint.setStrokeWidth(settingsDto.getRubberSize());
        } else {
            paint.setColor(settingsDto.getColor());
            paint.setStrokeWidth(settingsDto.getBrushSize());
        }
        paint.setStyle(Paint.Style.STROKE);
    }

    public void clearCanvas() {
        pathsWithColors.clear();
        invalidate();
    }
}
