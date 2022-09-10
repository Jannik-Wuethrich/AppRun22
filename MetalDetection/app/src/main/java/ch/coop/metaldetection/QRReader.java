package ch.coop.metaldetection;

import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRReader {
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        // show a message to the user, e.g., with a Toast
                    }
                } else {
                    String code = result.getContents();
                    // save code
                    Toast.makeText(this, "Scanned: " + code, Toast.LENGTH_LONG).show();
                }
            });
    public void launchScanner() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setCaptureActivity(MyCaptureActivity.class);
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        scanOptions.setOrientationLocked(false);
        scanOptions.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        scanOptions.setBeepEnabled(false);
        scanOptions.setPrompt("Scan a QR code");
        qrLauncher
}
