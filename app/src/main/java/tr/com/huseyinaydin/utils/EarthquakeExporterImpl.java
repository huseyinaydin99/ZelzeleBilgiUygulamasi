package tr.com.huseyinaydin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import androidx.compose.foundation.pager.PageInfo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

//import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.models.Earthquake;

public class EarthquakeExporterImpl implements EarthquakeExporter {

    private final Context context;

    // Constructor ile Context alınıyor (drawable erişimi için gerekli)
    public EarthquakeExporterImpl(Context context) {
        this.context = context;
    }

    /*@Override
    public void exportToPdf(List<Earthquake> earthquakes, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Drawable'dan logo alınıyor ve geçici dosyaya yazılıyor
            InputStream is = context.getResources().openRawResource(R.raw.sismograf); // raw/sismograf.png
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // Geçici dosya oluşturuluyor
            File tempLogoFile = new File(context.getCacheDir(), "sismograf.png");
            FileOutputStream fos = new FileOutputStream(tempLogoFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Geçici dosyadan görsel alınıyor
            Image img = Image.getInstance(tempLogoFile.getAbsolutePath());
            img.scaleToFit(50, 50);

            // Başlık ve logo ekleniyor
            Paragraph header = new Paragraph(" Zelzele Bilgi Uygulaması",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            header.setAlignment(Element.ALIGN_CENTER);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{1, 9});
            headerTable.addCell(new PdfPCell(img));

            PdfPCell textCell = new PdfPCell(header);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            textCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(textCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // Tablo ekleniyor
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Tarih");
            table.addCell("Şiddet");
            table.addCell("Lokasyon");

            for (Earthquake e : earthquakes) {
                table.addCell(e.getFormattedDate());
                table.addCell(String.valueOf(e.getMagnitude()));
                table.addCell(e.getLocation());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    // PDF formatında dışa aktarma metodu
    public void exportToPdf(List<Earthquake> earthquakes, String filePath) {
        try {
            // PDF dosyasını oluşturmak için PdfDocument başlatılıyor
            PdfDocument document = new PdfDocument();

            // Sayfa bilgisi (örnek: 595x842 pixels)
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // Başlık ekleniyor
            paint.setTextSize(20);
            paint.setFakeBoldText(true);
            canvas.drawText("Zelzele Bilgi Uygulaması", 200, 50, paint);

            // Logo ekleniyor
            InputStream is = context.getResources().openRawResource(R.raw.sismograf); // raw/sismograf.png
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // Görseli PDF'e ekliyoruz
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 200, 100, paint); // Logo yerleşimi (x=200, y=100)
            }

            // Tablo başlıkları
            paint.setTextSize(12);
            paint.setFakeBoldText(false);
            canvas.drawText("Tarih", 50, 150, paint);
            canvas.drawText("Şiddet", 200, 150, paint);
            canvas.drawText("Lokasyon", 350, 150, paint);

            // Tabloyu dolduruyoruz
            int yPosition = 170;
            for (Earthquake e : earthquakes) {
                canvas.drawText(e.getFormattedDate(), 50, yPosition, paint);
                canvas.drawText(String.valueOf(e.getMagnitude()), 200, yPosition, paint);
                canvas.drawText(e.getLocation(), 350, yPosition, paint);
                yPosition += 20; // Her bir satır için y konumunu artırıyoruz
            }

            // Sayfa bitişi
            document.finishPage(page);

            // PDF dosyasını kaydediyoruz
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            document.writeTo(fileOutputStream);

            // Dosya kapama
            fileOutputStream.close();
            document.close();
            Log.d("PDF Export", "PDF başarıyla oluşturuldu.");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDF Export", "PDF oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    // HTML formatında dışa aktarma metodu
    @Override
    public void exportToHtml(List<Earthquake> earthquakes, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("<html><head><meta charset='utf-8'><title>Zelzele Bilgi Uygulaması</title></head><body>");
            writer.write("<h2>Zelzele Bilgi Uygulaması</h2>");
            writer.write("<table border='1'><tr><th>Tarih</th><th>Şiddet</th><th>Lokasyon</th></tr>");
            for (Earthquake e : earthquakes) {
                writer.write("<tr><td>" + e.getFormattedDate() + "</td><td>" + e.getMagnitude() + "</td><td>" + e.getLocation() + "</td></tr>");
            }
            writer.write("</table></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TXT formatında dışa aktarma metodu
    @Override
    public void exportToTxt(List<Earthquake> earthquakes, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Zelzele Bilgi Uygulaması\n");
            writer.write("Tarih\t\tŞiddet\tLokasyon\n");
            for (Earthquake e : earthquakes) {
                writer.write(e.getFormattedDate() + "\t" + e.getMagnitude() + "\t" + e.getLocation() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Word (docx) formatında dışa aktarma metodu
    /*@Override
    public void exportToWord(List<Earthquake> earthquakes, String filePath) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph title = document.createParagraph();
            XWPFRun run = title.createRun();
            run.setText("Zelzele Bilgi Uygulaması");
            run.setBold(true);
            run.setFontSize(16);

            XWPFTable table = document.createTable();
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Tarih");
            header.addNewTableCell().setText("Şiddet");
            header.addNewTableCell().setText("Lokasyon");

            for (Earthquake e : earthquakes) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(e.getDate());
                row.getCell(1).setText(String.valueOf(e.getMagnitude()));
                row.getCell(2).setText(e.getLocation());
            }

            document.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void exportToWord(List<Earthquake> earthquakes, String filePath) {
        // .docx yerine .html uzantısı verilmeli, Word zaten açabiliyor.
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("<html><head><meta charset='UTF-8'><title>Zelzele Bilgi Uygulaması</title></head><body>");
            writer.write("<h2>Zelzele Bilgi Uygulaması</h2>");
            writer.write("<table border='1' cellpadding='5'><tr><th>Tarih</th><th>Şiddet</th><th>Lokasyon</th></tr>");
            for (Earthquake e : earthquakes) {
                writer.write("<tr><td>" + e.getFormattedDate() + "</td><td>" + e.getMagnitude() + "</td><td>" + e.getLocation() + "</td></tr>");
            }
            writer.write("</table></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
