package genos.noctua.paint;

import android.graphics.Paint;
import android.graphics.Path;

class PolygonPointDto {
    private final Path path;
    private final Paint paint;

    PolygonPointDto(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    Path getPath() {
        return path;
    }

    Paint getPaint() {
        return paint;
    }
}
