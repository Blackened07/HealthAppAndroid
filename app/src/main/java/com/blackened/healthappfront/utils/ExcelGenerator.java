package com.blackened.healthappfront.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

public final class ExcelGenerator {

    private ExcelGenerator() {
    }

    public static void createReport(Context context, List<HealthRecordResponseDTO> reportList, String userName) {

        String filepath = context.getExternalFilesDir(null) + File.separator
                + LocalDateTime.now().toString().substring(0, 10) + userName + "_report.xlsx";
        File file = new File(filepath);

        HealthRecordResponseDTO element = reportList.get(0);

        try(OutputStream os = new FileOutputStream(file);
            Workbook wb = new Workbook(os, "HealthAppFront", "1.0")) {;

            Worksheet sheet = wb.newWorksheet("Отчёт по " + element.getDisplayType());

            sheet.value(0, 0, element.getType());
            sheet.range(0, 0, 0, 5).merge();
            sheet.style(0,0).bold().horizontalAlignment("center").set();

            sheet.value(1, 0, "№");
            sheet.value(1, 1, "Дата");
            sheet.value(1, 2, "Значение");
            sheet.value(1, 3, "Комментарий");

            for (int i = 0; i < reportList.size(); i++) {

                element = reportList.get(i);

                sheet.value(i + 2, 0, i + 1);
                sheet.value(i + 2, 1, element.getDisplayDate());
                sheet.value(i + 2, 2, element.getDisplayValue());
                sheet.value(i + 2, 3, element.getNote());
            }

            sheet.finish();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void shareExcelFile(Context context, String userName) {

        String filepath = context.getExternalFilesDir(null) + File.separator
                + LocalDateTime.now().toString().substring(0, 10) + userName + "_report.xlsx";
        File file = new File(filepath);

        if (!file.exists()) {
            return;
        }

        String authority = context.getPackageName() + ".fileprovider";
        Uri fileUri = FileProvider.getUriForFile(
                context,
                authority,
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Отправить отчет через..."));
    }
}

