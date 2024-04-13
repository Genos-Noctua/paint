package genos.noctua.paint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    protected RelativeLayout canvasPolygon;
    protected Button clearCanvas;
    protected Button saveToFile;

    private CanvasPolygonView canvasPolygonView;
    private Bitmap bitmapToSave;

    private final Gson gson = new Gson();
    private final CanvasSettingsDto settingsDto = new CanvasSettingsDto();

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private final ActivityResultCallback<ActivityResult> fromSettings = r -> {
        if (r.getResultCode() != RESULT_OK || Objects.isNull(r.getData())) return;

        final String settingsPreparsed = Objects.requireNonNull(r.getData()).getStringExtra("settingsDto");
        final CanvasSettingsDto copy = gson.fromJson(settingsPreparsed, CanvasSettingsDto.class);

        if (copy.getBrushMode().equals(BrushMode.CLEARING)) {
            Toast.makeText(this, "Ваш текущий режим: стирание", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ваш текущий режим: рисование", Toast.LENGTH_SHORT).show();
        }
        settingsDto.copy(copy);
        canvasPolygonView.updateSettings();
    };

    private ActivityResultLauncher<Intent> saveFileActivityLauncher;
    private final ActivityResultCallback<ActivityResult> onSaveFile = r -> {
        if (r.getResultCode() != RESULT_OK || Objects.isNull(r.getData())) return;
        if (Objects.isNull(Objects.requireNonNull(r.getData()).getData())) return;

        final Uri uri = r.getData().getData();
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (Objects.isNull(outputStream)) return;

            bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Не удалось сохранить изображение", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Paint");

        canvasPolygon = findViewById(R.id.canvasPolygon);
        clearCanvas = findViewById(R.id.clearCanvasButton);
        saveToFile = findViewById(R.id.saveToFileButton);

        canvasPolygonView = new CanvasPolygonView(this);
        canvasPolygon.addView(canvasPolygonView);

        clearCanvas.setOnClickListener(this::onClearCanvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            saveToFile.setOnClickListener(this::onSaveToFile);
        }
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), fromSettings
        );
        saveFileActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), onSaveFile
        );
    }

    private void onClearCanvas(View view) {
        canvasPolygonView.clearCanvas();
        Toast.makeText(this, "Холст был очищен", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onSaveToFile(View view) {
        bitmapToSave = createBitmap();

        final String imageName = "image.png";
        final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File file = new File(dir, imageName);

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, imageName);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(file));
        saveFileActivityLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("settingsDto", gson.toJson(settingsDto));
        activityResultLauncher.launch(intent);
        return super.onOptionsItemSelected(item);
    }

    private Bitmap createBitmap() {
        final int width = canvasPolygonView.getWidth();
        final int height = canvasPolygonView.getHeight();

        final Bitmap originalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas originalCanvas = new Canvas(originalBitmap);
        canvasPolygonView.draw(originalCanvas);

        final Bitmap bitmapToSave = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvasToSave = new Canvas(bitmapToSave);
        canvasToSave.drawColor(Color.WHITE);
        canvasToSave.drawBitmap(originalBitmap, 0, 0, null);
        return bitmapToSave;
    }

    CanvasSettingsDto getSettingsDto() {
        return settingsDto;
    }
}